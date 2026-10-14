package br.com.eletromotosbh.agendamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoRequest(
    @NotNull(message = "Informe o veículo")
    @Positive(message = "Informe um veículo válido")
    Long veiculoId,

    @NotNull(message = "Informe o mecânico")
    @Positive(message = "Informe um mecânico válido")
    Long mecanicoId,

    @NotNull(message = "Informe a data do agendamento")
    LocalDate dataAgendamento,

    @NotNull(message = "Informe o horário do agendamento")
    LocalTime horaAgendamento,

    @NotBlank(message = "Informe o problema relatado pelo cliente")
    @Size(max = 5000, message = "O relato deve ter até 5000 caracteres")
    String relatoCliente
) {
}