package br.com.eletromotosbh.ordemservico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecebimentoRequest(

    @NotNull(message = "Informe se o carregador foi recebido")
    Boolean carregadorRecebido,

    @NotBlank(
        message = "Informe os acessórios recebidos ou escreva Nenhum"
    )
    @Size(
        max = 2000,
        message = "Os acessórios devem ter até 2000 caracteres"
    )
    String acessoriosRecebidos,

    @Size(
        max = 5000,
        message = "As observações devem ter até 5000 caracteres"
    )
    String observacoesRecebimento

) {
}