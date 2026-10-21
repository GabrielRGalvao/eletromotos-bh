package br.com.eletromotosbh.ordemservico.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DecisaoOrcamentoRequest(

    @NotNull(message = "Informe se o orçamento foi aprovado")
    Boolean aprovado,

    @Size(
        max = 5000,
        message = "A observação deve ter até 5000 caracteres"
    )
    String observacao

) {
}