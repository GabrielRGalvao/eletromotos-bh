
package br.com.eletromotosbh.ordemservico;

import br.com.eletromotosbh.agendamento.Agendamento;
import br.com.eletromotosbh.mecanico.Mecanico;
import br.com.eletromotosbh.veiculo.Veiculo;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "ordens_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agendamento_id", nullable = false, unique = true)
    private Agendamento agendamento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mecanico_id", nullable = false)
    private Mecanico mecanico;

    @Column(name = "relato_cliente", nullable = false, columnDefinition = "TEXT")
    private String relatoCliente;

    @Column(name = "carregador_recebido", nullable = false)
    private boolean carregadorRecebido;

    @Column(name = "acessorios_recebidos", nullable = false, columnDefinition = "TEXT")
    private String acessoriosRecebidos;

    @Column(name = "observacoes_recebimento", columnDefinition = "TEXT")
    private String observacoesRecebimento;

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(name = "descricao_orcamento", columnDefinition = "TEXT")
    private String descricaoOrcamento;

    @Column(name = "valor_orcamento", precision = 12, scale = 2)
    private BigDecimal valorOrcamento;

    @Column(name = "orcamento_enviado_em")
    private Instant orcamentoEnviadoEm;

    @Column(name = "decisao_orcamento_em")
    private Instant decisaoOrcamentoEm;

    @Column(name = "observacao_decisao", columnDefinition = "TEXT")
    private String observacaoDecisao;

    @Column(name = "execucao_iniciada_em")
    private Instant execucaoIniciadaEm;

    @Column(name = "concluido_em")
    private Instant concluidoEm;

    @Column(name = "observacoes_conclusao", columnDefinition = "TEXT")
    private String observacoesConclusao;

    @Column(name = "video_entrada_chave", unique = true, length = 255)
    private String videoEntradaChave;

    @Column(name = "video_entrada_tipo", length = 50)
    private String videoEntradaTipo;

    @Column(name = "video_entrada_tamanho")
    private Long videoEntradaTamanho;

    @Column(name = "video_entrada_em")
    private Instant videoEntradaEm;

    @Column(name = "video_saida_chave", unique = true, length = 255)
    private String videoSaidaChave;

    @Column(name = "video_saida_tipo", length = 50)
    private String videoSaidaTipo;

    @Column(name = "video_saida_tamanho")
    private Long videoSaidaTamanho;

    @Column(name = "video_saida_em")
    private Instant videoSaidaEm;

    @Column(name = "entregue_em")
    private Instant entregueEm;

    @Column(name = "entregue_por", length = 150)
    private String entreguePor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusOrdemServico status = StatusOrdemServico.EM_DIAGNOSTICO;

    @Column(name = "recebido_em", nullable = false, updatable = false)
    private Instant recebidoEm;

    protected OrdemServico() {
    }

    public OrdemServico(
            Agendamento agendamento,
            boolean carregadorRecebido,
            String acessoriosRecebidos,
            String observacoesRecebimento,
            String videoEntradaChave,
            String videoEntradaTipo,
            Long videoEntradaTamanho) {
        this.agendamento = agendamento;
        this.veiculo = agendamento.getVeiculo();
        this.mecanico = agendamento.getMecanico();
        this.relatoCliente = agendamento.getRelatoCliente();
        this.carregadorRecebido = carregadorRecebido;
        this.acessoriosRecebidos = acessoriosRecebidos;
        this.observacoesRecebimento = observacoesRecebimento;
        this.videoEntradaChave = videoEntradaChave;
        this.videoEntradaTipo = videoEntradaTipo;
        this.videoEntradaTamanho = videoEntradaTamanho;

        if (videoEntradaChave != null) {
            this.videoEntradaEm = Instant.now();
        }
    }

    @PrePersist
    private void definirRecebimento() {
        if (recebidoEm == null) {
            recebidoEm = Instant.now();
        }
    }

    public void registrarDiagnostico(
            String diagnostico,
            String descricaoOrcamento,
            BigDecimal valorOrcamento) {
        this.diagnostico = diagnostico;
        this.descricaoOrcamento = descricaoOrcamento;
        this.valorOrcamento = valorOrcamento;
        this.orcamentoEnviadoEm = Instant.now();
        this.decisaoOrcamentoEm = null;
        this.observacaoDecisao = null;
        this.status = StatusOrdemServico.AGUARDANDO_APROVACAO;
    }

    public void registrarDecisao(
            boolean aprovado,
            String observacao) {
        this.status = aprovado
                ? StatusOrdemServico.APROVADO
                : StatusOrdemServico.REPROVADO;

        this.observacaoDecisao = observacao;
        this.decisaoOrcamentoEm = Instant.now();
    }

    public void iniciarExecucao() {
        this.status = StatusOrdemServico.EM_EXECUCAO;
        this.execucaoIniciadaEm = Instant.now();
    }

    public void concluir(String observacoesConclusao) {
        this.status = StatusOrdemServico.CONCLUIDO;
        this.observacoesConclusao = observacoesConclusao;
        this.concluidoEm = Instant.now();
    }

    public void registrarEntrega(
            String videoSaidaChave,
            String videoSaidaTipo,
            Long videoSaidaTamanho,
            String entreguePor) {

        if (status != StatusOrdemServico.CONCLUIDO) {
            throw new IllegalStateException(
                    "Somente ordens concluídas podem ser entregues");
        }

        if (entreguePor == null || entreguePor.isBlank()) {
            throw new IllegalArgumentException(
                    "O responsável pela entrega é obrigatório");
        }

        if (videoSaidaChave != null) {
            if (videoSaidaTipo == null
                    || videoSaidaTamanho == null
                    || videoSaidaTamanho <= 0) {
                throw new IllegalArgumentException(
                        "Os dados do vídeo de saída são inválidos");
            }

            this.videoSaidaChave = videoSaidaChave;
            this.videoSaidaTipo = videoSaidaTipo;
            this.videoSaidaTamanho = videoSaidaTamanho;
            this.videoSaidaEm = Instant.now();
        }

        this.entreguePor = entreguePor.trim();
        this.entregueEm = Instant.now();
        this.status = StatusOrdemServico.ENTREGUE;
    }

    public Long getId() {
        return id;
    }

    public Agendamento getAgendamento() {
        return agendamento;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public Mecanico getMecanico() {
        return mecanico;
    }

    public String getRelatoCliente() {
        return relatoCliente;
    }

    public boolean isCarregadorRecebido() {
        return carregadorRecebido;
    }

    public String getAcessoriosRecebidos() {
        return acessoriosRecebidos;
    }

    public String getObservacoesRecebimento() {
        return observacoesRecebimento;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public String getDescricaoOrcamento() {
        return descricaoOrcamento;
    }

    public BigDecimal getValorOrcamento() {
        return valorOrcamento;
    }

    public Instant getOrcamentoEnviadoEm() {
        return orcamentoEnviadoEm;
    }

    public Instant getDecisaoOrcamentoEm() {
        return decisaoOrcamentoEm;
    }

    public String getObservacaoDecisao() {
        return observacaoDecisao;
    }

    public String getVideoEntradaChave() {
        return videoEntradaChave;
    }

    public String getVideoEntradaTipo() {
        return videoEntradaTipo;
    }

    public Long getVideoEntradaTamanho() {
        return videoEntradaTamanho;
    }

    public Instant getVideoEntradaEm() {
        return videoEntradaEm;
    }

    public String getVideoSaidaChave() {
        return videoSaidaChave;
    }

    public String getVideoSaidaTipo() {
        return videoSaidaTipo;
    }

    public Long getVideoSaidaTamanho() {
        return videoSaidaTamanho;
    }

    public Instant getVideoSaidaEm() {
        return videoSaidaEm;
    }

    public Instant getEntregueEm() {
        return entregueEm;
    }

    public String getEntreguePor() {
        return entreguePor;
    }

    public String getVideoChave() {
        return videoEntradaChave;
    }

    public String getVideoTipo() {
        return videoEntradaTipo;
    }

    public long getVideoTamanho() {
        return videoEntradaTamanho == null ? 0L : videoEntradaTamanho;
    }

    public StatusOrdemServico getStatus() {
        return status;
    }

    public Instant getRecebidoEm() {
        return recebidoEm;
    }

    public Instant getExecucaoIniciadaEm() {
        return execucaoIniciadaEm;
    }

    public Instant getConcluidoEm() {
        return concluidoEm;
    }

    public String getObservacoesConclusao() {
        return observacoesConclusao;
    }
}
