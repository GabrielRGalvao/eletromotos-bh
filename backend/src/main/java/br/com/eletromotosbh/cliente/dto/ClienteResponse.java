package br.com.eletromotosbh.cliente.dto;

import java.time.Instant;

public record ClienteResponse(
    Long id,
    String nome,
    String telefone,
    String email,
    String cpf,
    String observacoes,
    Instant dataCadastro
) {
}