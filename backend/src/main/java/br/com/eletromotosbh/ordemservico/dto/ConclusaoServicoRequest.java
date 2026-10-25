package br.com.eletromotosbh.ordemservico.dto;

import jakarta.validation.constraints.Size;

public record ConclusaoServicoRequest(

    @Size(
        max = 5000,
        message = "As observações devem ter até 5000 caracteres"
    )
    String observacoesConclusao

) {
}