package br.com.eletromotosbh.veiculo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record VeiculoRequest(
    @NotNull(message = "Informe o cliente")
    @Positive(message = "Informe um cliente válido")
    Long clienteId,

    @NotBlank(message = "O tipo é obrigatório")
    @Size(max = 50, message = "O tipo deve ter até 50 caracteres")
    String tipo,

    @NotBlank(message = "A marca é obrigatória")
    @Size(max = 100, message = "A marca deve ter até 100 caracteres")
    String marca,

    @NotBlank(message = "O modelo é obrigatório")
    @Size(max = 100, message = "O modelo deve ter até 100 caracteres")
    String modelo,

    @NotBlank(message = "A cor é obrigatória")
    @Size(max = 50, message = "A cor deve ter até 50 caracteres")
    String cor,

    @Size(max = 20, message = "A placa deve ter até 20 caracteres")
    String placa
) {
}