package br.com.eletromotosbh.veiculo.dto;

import java.time.Instant;

public record VeiculoResponse(
    Long id,
    Long clienteId,
    String tipo,
    String marca,
    String modelo,
    String cor,
    String placa,
    Instant dataCadastro
) {
}