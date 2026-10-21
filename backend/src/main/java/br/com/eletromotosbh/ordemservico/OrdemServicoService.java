package br.com.eletromotosbh.ordemservico;

import br.com.eletromotosbh.agendamento.Agendamento;
import br.com.eletromotosbh.agendamento.AgendamentoRepository;
import br.com.eletromotosbh.agendamento.StatusAgendamento;
import br.com.eletromotosbh.ordemservico.dto.DecisaoOrcamentoRequest;
import br.com.eletromotosbh.ordemservico.dto.DiagnosticoOrcamentoRequest;
import br.com.eletromotosbh.ordemservico.dto.OrdemServicoResponse;
import br.com.eletromotosbh.ordemservico.dto.RecebimentoRequest;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final VideoVistoriaStorage videoStorage;

    public OrdemServicoService(
            OrdemServicoRepository ordemServicoRepository,
            AgendamentoRepository agendamentoRepository,
            VideoVistoriaStorage videoStorage
    ) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.videoStorage = videoStorage;
    }

    @Transactional
    public OrdemServicoResponse receber(
            Long agendamentoId,
            RecebimentoRequest request,
            MultipartFile video
    ) {
        Agendamento agendamento = agendamentoRepository
            .buscarParaRecebimento(agendamentoId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Agendamento não encontrado"
            ));

        if (agendamento.getStatus() != StatusAgendamento.AGENDADO) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Somente agendamentos na situação AGENDADO podem ser recebidos"
            );
        }

        if (ordemServicoRepository.existsByAgendamentoId(agendamentoId)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Este agendamento já possui uma ordem de serviço"
            );
        }

        if (!agendamento.getMecanico().isAtivo()) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "O mecânico do agendamento está inativo"
            );
        }

        var videoSalvo = videoStorage.salvar(video);

        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) {
                        videoStorage.excluir(videoSalvo.chave());
                    }
                }
            }
        );

        OrdemServico ordem = new OrdemServico(
            agendamento,
            request.carregadorRecebido(),
            request.acessoriosRecebidos().trim(),
            normalizarOpcional(request.observacoesRecebimento()),
            videoSalvo.chave(),
            videoSalvo.tipo(),
            videoSalvo.tamanho()
        );

        agendamento.marcarComoRecebido();

        OrdemServico salva = ordemServicoRepository.saveAndFlush(ordem);

        return converterParaResponse(salva);
    }

    @Transactional
    public OrdemServicoResponse registrarDiagnostico(
            Long id,
            DiagnosticoOrcamentoRequest request
    ) {
        OrdemServico ordem = buscarParaAtualizacao(id);

        if (ordem.getStatus() != StatusOrdemServico.EM_DIAGNOSTICO) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Somente ordens em diagnóstico podem receber orçamento"
            );
        }

        ordem.registrarDiagnostico(
            request.diagnostico().trim(),
            request.descricaoOrcamento().trim(),
            request.valorOrcamento()
        );

        OrdemServico salva = ordemServicoRepository.saveAndFlush(ordem);

        return converterParaResponse(salva);
    }

    @Transactional
    public OrdemServicoResponse registrarDecisaoOrcamento(
            Long id,
            DecisaoOrcamentoRequest request
    ) {
        OrdemServico ordem = buscarParaAtualizacao(id);

        if (
            ordem.getStatus()
                != StatusOrdemServico.AGUARDANDO_APROVACAO
        ) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Somente ordens aguardando aprovação podem receber uma decisão"
            );
        }

        ordem.registrarDecisao(
            request.aprovado(),
            normalizarOpcional(request.observacao())
        );

        OrdemServico salva = ordemServicoRepository.saveAndFlush(ordem);

        return converterParaResponse(salva);
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoResponse> listar() {
        return ordemServicoRepository.findAll(
                Sort.by(
                    Sort.Order.desc("recebidoEm"),
                    Sort.Order.desc("id")
                )
            )
            .stream()
            .map(this::converterParaResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public OrdemServicoResponse buscarPorId(Long id) {
        return converterParaResponse(buscarEntidade(id));
    }

    public record VideoDownload(Resource arquivo, String tipo) {
    }

    @Transactional(readOnly = true)
    public VideoDownload buscarVideo(Long id) {
        OrdemServico ordem = buscarEntidade(id);

        return new VideoDownload(
            videoStorage.carregar(ordem.getVideoChave()),
            ordem.getVideoTipo()
        );
    }

    private OrdemServico buscarParaAtualizacao(Long id) {
        return ordemServicoRepository.buscarParaAtualizacao(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Ordem de serviço não encontrada"
            ));
    }

    private OrdemServico buscarEntidade(Long id) {
        return ordemServicoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Ordem de serviço não encontrada"
            ));
    }

    private String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }

    private OrdemServicoResponse converterParaResponse(
            OrdemServico ordem
    ) {
        var veiculo = ordem.getVeiculo();
        var cliente = veiculo.getCliente();
        var mecanico = ordem.getMecanico();

        return new OrdemServicoResponse(
            ordem.getId(),
            ordem.getAgendamento().getId(),
            cliente.getId(),
            cliente.getNome(),
            cliente.getTelefone(),
            veiculo.getId(),
            veiculo.getMarca(),
            veiculo.getModelo(),
            mecanico.getId(),
            mecanico.getNome(),
            ordem.getRelatoCliente(),
            ordem.isCarregadorRecebido(),
            ordem.getAcessoriosRecebidos(),
            ordem.getObservacoesRecebimento(),
            ordem.getDiagnostico(),
            ordem.getDescricaoOrcamento(),
            ordem.getValorOrcamento(),
            ordem.getOrcamentoEnviadoEm(),
            ordem.getDecisaoOrcamentoEm(),
            ordem.getObservacaoDecisao(),
            ordem.getStatus(),
            ordem.getRecebidoEm(),
            "/api/ordens-servico/" + ordem.getId() + "/video",
            ordem.getVideoTamanho()
        );
    }
}