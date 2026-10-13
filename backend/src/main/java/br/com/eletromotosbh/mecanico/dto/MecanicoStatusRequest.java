package br.com.eletromotosbh.mecanico.dto;

import jakarta.validation.constraints.NotNull;

public record MecanicoStatusRequest(
    @NotNull(message = "Informe se o mecânico está ativo")
    Boolean ativo
) {
}