package br.com.eletromotosbh.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequest(

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
    String nome,

    @NotBlank(message = "O telefone é obrigatório")
    @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres")
    String telefone,

    @Email(message = "Informe um email válido")
    @Size(max = 254, message = "O email deve ter no máximo 254 caracteres")
    String email,

    @Pattern(regexp = "[0-9]{11}", message = "O CPF deve conter 11 dígitos, sem pontuação")
    String cpf,

    String observacoes

) {
}