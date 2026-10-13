package br.com.eletromotosbh.mecanico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MecanicoRequest(
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 150, message = "O nome deve ter até 150 caracteres")
    String nome
) {
}