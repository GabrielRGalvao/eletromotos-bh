package br.com.eletromotosbh.ordemservico.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record DiagnosticoOrcamentoRequest(

    @NotBlank(message = "Informe o diagnóstico")
    @Size(
        max = 5000,
        message = "O diagnóstico deve ter até 5000 caracteres"
    )
    String diagnostico,

    @NotBlank(message = "Informe a descrição do orçamento")
    @Size(
        max = 5000,
        message = "A descrição do orçamento deve ter até 5000 caracteres"
    )
    String descricaoOrcamento,

    @NotNull(message = "Informe o valor do orçamento")
    @DecimalMin(
        value = "0.01",
        message = "O valor do orçamento deve ser maior que zero"
    )
    @Digits(
        integer = 10,
        fraction = 2,
        message = "Informe um valor com até 10 dígitos e 2 casas decimais"
    )
    BigDecimal valorOrcamento

) {
}