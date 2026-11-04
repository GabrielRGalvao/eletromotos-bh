package br.com.eletromotosbh.agendamento;

import br.com.eletromotosbh.agendamento.dto.AgendamentoRequest;
import br.com.eletromotosbh.agendamento.dto.AgendamentoResponse;
import br.com.eletromotosbh.cliente.Cliente;
import br.com.eletromotosbh.mecanico.Mecanico;
import br.com.eletromotosbh.mecanico.MecanicoRepository;
import br.com.eletromotosbh.veiculo.Veiculo;
import br.com.eletromotosbh.veiculo.VeiculoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final VeiculoRepository veiculoRepository;
    private final MecanicoRepository mecanicoRepository;
    private final ControleRodizioAgendamentoRepository controleRodizioRepository;

    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            VeiculoRepository veiculoRepository,
            MecanicoRepository mecanicoRepository,
            ControleRodizioAgendamentoRepository controleRodizioRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.veiculoRepository = veiculoRepository;
        this.mecanicoRepository = mecanicoRepository;
        this.controleRodizioRepository = controleRodizioRepository;
    }

    @Transactional
    public AgendamentoResponse cadastrar(AgendamentoRequest request) {
        Veiculo veiculo = veiculoRepository.findById(request.veiculoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Veículo não encontrado"));

        Mecanico mecanico = definirMecanicoDoRodizio();

        Agendamento agendamento = new Agendamento(
                veiculo,
                mecanico,
                request.dataAgendamento(),
                request.horaAgendamento(),
                request.relatoCliente().trim());

        return converterParaResponse(agendamentoRepository.save(agendamento));
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> listar(LocalDate data, String busca) {
        String buscaNormalizada = busca == null ? null : busca.trim();

        if (buscaNormalizada != null && buscaNormalizada.isBlank()) {
            buscaNormalizada = null;
        }

        return agendamentoRepository.filtrar(data, buscaNormalizada).stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AgendamentoResponse buscarPorId(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Agendamento não encontrado"));

        return converterParaResponse(agendamento);
    }

    private Mecanico definirMecanicoDoRodizio() {
        List<Mecanico> mecanicosAtivos = mecanicoRepository.findByAtivoTrueOrderByOrdemRodizioAscIdAsc();

        if (mecanicosAtivos.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não há mecânicos ativos para receber o agendamento");
        }

        ControleRodizioAgendamento controle = controleRodizioRepository
                .buscarParaAtualizar()
                .orElseThrow(() -> new IllegalStateException(
                        "Controle de rodízio não encontrado"));

        int indiceUltimo = -1;

        for (int indice = 0; indice < mecanicosAtivos.size(); indice++) {
            if (mecanicosAtivos.get(indice).getId()
                    .equals(controle.getUltimoMecanicoId())) {
                indiceUltimo = indice;
                break;
            }
        }

        Mecanico proximo = mecanicosAtivos.get(
                (indiceUltimo + 1) % mecanicosAtivos.size());

        controle.registrarUltimoMecanico(proximo.getId());

        return proximo;
    }

    private AgendamentoResponse converterParaResponse(Agendamento agendamento) {
        Veiculo veiculo = agendamento.getVeiculo();
        Cliente cliente = veiculo.getCliente();
        Mecanico mecanico = agendamento.getMecanico();

        return new AgendamentoResponse(
                agendamento.getId(),
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                veiculo.getId(),
                veiculo.getTipo(),
                veiculo.getMarca(),
                veiculo.getModelo(),
                veiculo.getCor(),
                veiculo.getPlaca(),
                mecanico.getId(),
                mecanico.getNome(),
                agendamento.getDataAgendamento(),
                agendamento.getHoraAgendamento(),
                agendamento.getRelatoCliente(),
                agendamento.getStatus(),
                agendamento.getDataCadastro());
    }
}