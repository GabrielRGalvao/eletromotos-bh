package br.com.eletromotosbh.mecanico.dto;

import java.time.Instant;

public record MecanicoResponse(
    Long id,
    String nome,
    boolean ativo,
    Instant dataCadastro
) {
}