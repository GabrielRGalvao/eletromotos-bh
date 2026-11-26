
package br.com.eletromotosbh.ordemservico;

import br.com.eletromotosbh.agendamento.Agendamento;
import br.com.eletromotosbh.agendamento.AgendamentoRepository;
import br.com.eletromotosbh.agendamento.StatusAgendamento;
import br.com.eletromotosbh.ordemservico.dto.DecisaoOrcamentoRequest;
import br.com.eletromotosbh.ordemservico.dto.DiagnosticoOrcamentoRequest;
import br.com.eletromotosbh.ordemservico.dto.OrdemServicoResponse;
import br.com.eletromotosbh.ordemservico.dto.RecebimentoRequest;
import br.com.eletromotosbh.ordemservico.dto.ConclusaoServicoRequest;
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
            VideoVistoriaStorage videoStorage) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.videoStorage = videoStorage;
    }

    @Transactional
    public OrdemServicoResponse receber(
            Long agendamentoId,
            RecebimentoRequest request,
            MultipartFile video) {
        Agendamento agendamento = agendamentoRepository
                .buscarParaRecebimento(agendamentoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Agendamento não encontrado"));

        if (agendamento.getStatus() != StatusAgendamento.AGENDADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Somente agendamentos na situação AGENDADO podem ser recebidos");
        }

        if (ordemServicoRepository.existsByAgendamentoId(agendamentoId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este agendamento já possui uma ordem de serviço");
        }

        if (!agendamento.getMecanico().isAtivo()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O mecânico do agendamento está inativo");
        }

        var videoSalvo = salvarVideoOpcional(video);

        OrdemServico ordem = new OrdemServico(
                agendamento,
                request.carregadorRecebido(),
                request.acessoriosRecebidos().trim(),
                normalizarOpcional(request.observacoesRecebimento()),
                videoSalvo != null ? videoSalvo.chave() : null,
                videoSalvo != null ? videoSalvo.tipo() : null,
                videoSalvo != null ? videoSalvo.tamanho() : null);

        agendamento.marcarComoRecebido();

        OrdemServico salva = ordemServicoRepository.saveAndFlush(ordem);

        return converterParaResponse(salva);
    }

    @Transactional
    public OrdemServicoResponse registrarDiagnostico(
            Long id,
            DiagnosticoOrcamentoRequest request) {
        OrdemServico ordem = buscarParaAtualizacao(id);

        if (ordem.getStatus() != StatusOrdemServico.EM_DIAGNOSTICO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Somente ordens em diagnóstico podem receber orçamento");
        }

        ordem.registrarDiagnostico(
                request.diagnostico().trim(),
                request.descricaoOrcamento().trim(),
                request.valorOrcamento());

        OrdemServico salva = ordemServicoRepository.saveAndFlush(ordem);

        return converterParaResponse(salva);
    }

    @Transactional
    public OrdemServicoResponse registrarDecisaoOrcamento(
            Long id,
            DecisaoOrcamentoRequest request) {
        OrdemServico ordem = buscarParaAtualizacao(id);

        if (ordem.getStatus() != StatusOrdemServico.AGUARDANDO_APROVACAO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Somente ordens aguardando aprovação podem receber uma decisão");
        }

        ordem.registrarDecisao(
                request.aprovado(),
                normalizarOpcional(request.observacao()));

        OrdemServico salva = ordemServicoRepository.saveAndFlush(ordem);

        return converterParaResponse(salva);
    }

    @Transactional
    public OrdemServicoResponse iniciarExecucao(Long id) {
        OrdemServico ordem = buscarParaAtualizacao(id);

        if (ordem.getStatus() != StatusOrdemServico.APROVADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Somente ordens aprovadas podem iniciar a execução");
        }

        ordem.iniciarExecucao();

        OrdemServico salva = ordemServicoRepository.saveAndFlush(ordem);

        return converterParaResponse(salva);
    }

    @Transactional
    public OrdemServicoResponse concluir(
            Long id,
            ConclusaoServicoRequest request) {
        OrdemServico ordem = buscarParaAtualizacao(id);

        if (ordem.getStatus() != StatusOrdemServico.EM_EXECUCAO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Somente ordens em execução podem ser concluídas");
        }

        ordem.concluir(
                normalizarOpcional(request.observacoesConclusao()));

        OrdemServico salva = ordemServicoRepository.saveAndFlush(ordem);

        return converterParaResponse(salva);
    }

    @Transactional
    public OrdemServicoResponse registrarEntrega(
            Long id,
            String entreguePor,
            MultipartFile video) {
        OrdemServico ordem = buscarParaAtualizacao(id);

        if (ordem.getStatus() != StatusOrdemServico.CONCLUIDO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Somente ordens concluídas podem ser entregues");
        }

        String responsavel = normalizarOpcional(entreguePor);

        if (responsavel == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o funcionário responsável pela entrega");
        }

        if (responsavel.length() > 150) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome do responsável deve ter no máximo 150 caracteres");
        }

        var videoSalvo = salvarVideoOpcional(video);

        ordem.registrarEntrega(
                videoSalvo != null ? videoSalvo.chave() : null,
                videoSalvo != null ? videoSalvo.tipo() : null,
                videoSalvo != null ? videoSalvo.tamanho() : null,
                responsavel);

        OrdemServico salva = ordemServicoRepository.saveAndFlush(ordem);

        return converterParaResponse(salva);
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoResponse> listar() {
        return ordemServicoRepository.findAll(
                Sort.by(
                        Sort.Order.desc("recebidoEm"),
                        Sort.Order.desc("id")))
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
        return buscarVideoEntrada(id);
    }

    @Transactional(readOnly = true)
    public VideoDownload buscarVideoEntrada(Long id) {
        OrdemServico ordem = buscarEntidade(id);

        if (ordem.getVideoEntradaChave() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Esta ordem de serviço não possui vídeo de entrada");
        }

        return new VideoDownload(
                videoStorage.carregar(ordem.getVideoEntradaChave()),
                ordem.getVideoEntradaTipo());
    }

    @Transactional(readOnly = true)
    public VideoDownload buscarVideoSaida(Long id) {
        OrdemServico ordem = buscarEntidade(id);

        if (ordem.getVideoSaidaChave() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Esta ordem de serviço não possui vídeo de saída");
        }

        return new VideoDownload(
                videoStorage.carregar(ordem.getVideoSaidaChave()),
                ordem.getVideoSaidaTipo());
    }

    private VideoVistoriaStorage.VideoSalvo salvarVideoOpcional(
            MultipartFile video) {
        if (video == null || video.isEmpty()) {
            return null;
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
                });

        return videoSalvo;
    }

    private OrdemServico buscarParaAtualizacao(Long id) {
        return ordemServicoRepository.buscarParaAtualizacao(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ordem de serviço não encontrada"));
    }

    private OrdemServico buscarEntidade(Long id) {
        return ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ordem de serviço não encontrada"));
    }

    private String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }

    private OrdemServicoResponse converterParaResponse(
            OrdemServico ordem) {
        var veiculo = ordem.getVeiculo();
        var cliente = veiculo.getCliente();
        var mecanico = ordem.getMecanico();

        String videoEntradaUrl = ordem.getVideoEntradaChave() != null
                ? "/api/ordens-servico/" + ordem.getId() + "/video-entrada"
                : null;

        String videoSaidaUrl = ordem.getVideoSaidaChave() != null
                ? "/api/ordens-servico/" + ordem.getId() + "/video-saida"
                : null;

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
                ordem.getExecucaoIniciadaEm(),
                ordem.getConcluidoEm(),
                ordem.getObservacoesConclusao(),
                ordem.getStatus(),
                ordem.getRecebidoEm(),
                videoEntradaUrl,
                ordem.getVideoTamanho(),
                videoEntradaUrl,
                ordem.getVideoEntradaEm(),
                videoSaidaUrl,
                ordem.getVideoSaidaTamanho(),
                ordem.getVideoSaidaEm(),
                ordem.getEntregueEm(),
                ordem.getEntreguePor());
    }
}
