package br.com.eletromotosbh.ordemservico.dto;

import br.com.eletromotosbh.ordemservico.StatusOrdemServico;

import java.math.BigDecimal;
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
        String diagnostico,
        String descricaoOrcamento,
        BigDecimal valorOrcamento,
        Instant orcamentoEnviadoEm,
        Instant decisaoOrcamentoEm,
        String observacaoDecisao,
        Instant execucaoIniciadaEm,
        Instant concluidoEm,
        String observacoesConclusao,
        StatusOrdemServico status,
        Instant recebidoEm,
        String videoUrl,
        long videoTamanho) {
}