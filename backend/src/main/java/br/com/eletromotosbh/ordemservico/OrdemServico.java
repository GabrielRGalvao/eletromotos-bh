package br.com.eletromotosbh.ordemservico;

import br.com.eletromotosbh.agendamento.Agendamento;
import br.com.eletromotosbh.mecanico.Mecanico;
import br.com.eletromotosbh.veiculo.Veiculo;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "ordens_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "agendamento_id",
        nullable = false,
        unique = true
    )
    private Agendamento agendamento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mecanico_id", nullable = false)
    private Mecanico mecanico;

    @Column(
        name = "relato_cliente",
        nullable = false,
        columnDefinition = "TEXT"
    )
    private String relatoCliente;

    @Column(name = "carregador_recebido", nullable = false)
    private boolean carregadorRecebido;

    @Column(
        name = "acessorios_recebidos",
        nullable = false,
        columnDefinition = "TEXT"
    )
    private String acessoriosRecebidos;

    @Column(
        name = "observacoes_recebimento",
        columnDefinition = "TEXT"
    )
    private String observacoesRecebimento;

    @Column(
        name = "video_chave",
        nullable = false,
        unique = true,
        length = 255
    )
    private String videoChave;

    @Column(name = "video_tipo", nullable = false, length = 50)
    private String videoTipo;

    @Column(name = "video_tamanho", nullable = false)
    private long videoTamanho;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusOrdemServico status =
        StatusOrdemServico.EM_DIAGNOSTICO;

    @Column(
        name = "recebido_em",
        nullable = false,
        updatable = false
    )
    private Instant recebidoEm;

    protected OrdemServico() {
    }

    public OrdemServico(
            Agendamento agendamento,
            boolean carregadorRecebido,
            String acessoriosRecebidos,
            String observacoesRecebimento,
            String videoChave,
            String videoTipo,
            long videoTamanho
    ) {
        this.agendamento = agendamento;
        this.veiculo = agendamento.getVeiculo();
        this.mecanico = agendamento.getMecanico();
        this.relatoCliente = agendamento.getRelatoCliente();
        this.carregadorRecebido = carregadorRecebido;
        this.acessoriosRecebidos = acessoriosRecebidos;
        this.observacoesRecebimento = observacoesRecebimento;
        this.videoChave = videoChave;
        this.videoTipo = videoTipo;
        this.videoTamanho = videoTamanho;
    }

    @PrePersist
    private void definirRecebimento() {
        if (recebidoEm == null) {
            recebidoEm = Instant.now();
        }
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

    public String getVideoChave() {
        return videoChave;
    }

    public String getVideoTipo() {
        return videoTipo;
    }

    public long getVideoTamanho() {
        return videoTamanho;
    }

    public StatusOrdemServico getStatus() {
        return status;
    }

    public Instant getRecebidoEm() {
        return recebidoEm;
    }
}