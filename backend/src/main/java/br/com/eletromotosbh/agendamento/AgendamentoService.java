package br.com.eletromotosbh.agendamento;

import br.com.eletromotosbh.agendamento.dto.AgendamentoRequest;
import br.com.eletromotosbh.agendamento.dto.AgendamentoResponse;
import br.com.eletromotosbh.cliente.Cliente;
import br.com.eletromotosbh.mecanico.Mecanico;
import br.com.eletromotosbh.mecanico.MecanicoRepository;
import br.com.eletromotosbh.veiculo.Veiculo;
import br.com.eletromotosbh.veiculo.VeiculoRepository;
import org.springframework.data.domain.Sort;
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

    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            VeiculoRepository veiculoRepository,
            MecanicoRepository mecanicoRepository
    ) {
        this.agendamentoRepository = agendamentoRepository;
        this.veiculoRepository = veiculoRepository;
        this.mecanicoRepository = mecanicoRepository;
    }

    @Transactional
    public AgendamentoResponse cadastrar(AgendamentoRequest request) {
        Veiculo veiculo = veiculoRepository.findById(request.veiculoId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Veículo não encontrado"
            ));

        Mecanico mecanico = mecanicoRepository.findById(request.mecanicoId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Mecânico não encontrado"
            ));

        if (!mecanico.isAtivo()) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Não é possível agendar com um mecânico inativo"
            );
        }

        Agendamento agendamento = new Agendamento(
            veiculo,
            mecanico,
            request.dataAgendamento(),
            request.horaAgendamento(),
            request.relatoCliente().trim()
        );

        Agendamento salvo = agendamentoRepository.save(agendamento);
        return converterParaResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> listar(LocalDate data) {
        List<Agendamento> agendamentos;

        if (data != null) {
            agendamentos =
                agendamentoRepository.findByDataAgendamentoOrderByHoraAgendamentoAscIdAsc(
                    data
                );
        } else {
            agendamentos = agendamentoRepository.findAll(
                Sort.by("dataAgendamento", "horaAgendamento", "id")
            );
        }

        return agendamentos.stream()
            .map(this::converterParaResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public AgendamentoResponse buscarPorId(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Agendamento não encontrado"
            ));

        return converterParaResponse(agendamento);
    }

    private AgendamentoResponse converterParaResponse(
            Agendamento agendamento
    ) {
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
            agendamento.getDataCadastro()
        );
    }
}