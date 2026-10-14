package br.com.eletromotosbh.agendamento.dto;

import br.com.eletromotosbh.agendamento.StatusAgendamento;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoResponse(
    Long id,
    Long clienteId,
    String clienteNome,
    String clienteTelefone,
    Long veiculoId,
    String veiculoTipo,
    String veiculoMarca,
    String veiculoModelo,
    String veiculoCor,
    String veiculoPlaca,
    Long mecanicoId,
    String mecanicoNome,
    LocalDate dataAgendamento,
    LocalTime horaAgendamento,
    String relatoCliente,
    StatusAgendamento status,
    Instant dataCadastro
) {
}