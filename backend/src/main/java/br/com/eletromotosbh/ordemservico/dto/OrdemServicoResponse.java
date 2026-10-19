package br.com.eletromotosbh.ordemservico.dto;

import br.com.eletromotosbh.ordemservico.StatusOrdemServico;

import java.time.Instant;

public record OrdemServicoResponse(
    Long id,
    Long agendamentoId,
    Long clienteId,
    String clienteNome,
    String clienteTelefone,
    Long veiculoId,
    String veiculoMarca,
    String veiculoModelo,
    Long mecanicoId,
    String mecanicoNome,
    String relatoCliente,
    boolean carregadorRecebido,
    String acessoriosRecebidos,
    String observacoesRecebimento,
    StatusOrdemServico status,
    Instant recebidoEm,
    String videoUrl,
    long videoTamanho
) {
}