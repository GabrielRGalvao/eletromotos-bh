package br.com.eletromotosbh.agendamento;

import br.com.eletromotosbh.mecanico.Mecanico;
import br.com.eletromotosbh.veiculo.Veiculo;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mecanico_id", nullable = false)
    private Mecanico mecanico;

    @Column(name = "data_agendamento", nullable = false)
    private LocalDate dataAgendamento;

    @Column(name = "hora_agendamento", nullable = false)
    private LocalTime horaAgendamento;

    @Column(name = "relato_cliente", nullable = false, columnDefinition = "TEXT")
    private String relatoCliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAgendamento status = StatusAgendamento.AGENDADO;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private Instant dataCadastro;

    protected Agendamento() {
    }

    public Agendamento(
            Veiculo veiculo,
            Mecanico mecanico,
            LocalDate dataAgendamento,
            LocalTime horaAgendamento,
            String relatoCliente
    ) {
        this.veiculo = veiculo;
        this.mecanico = mecanico;
        this.dataAgendamento = dataAgendamento;
        this.horaAgendamento = horaAgendamento;
        this.relatoCliente = relatoCliente;
    }

    @PrePersist
    private void definirDataCadastro() {
        if (dataCadastro == null) {
            dataCadastro = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public Mecanico getMecanico() {
        return mecanico;
    }

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public LocalTime getHoraAgendamento() {
        return horaAgendamento;
    }

    public String getRelatoCliente() {
        return relatoCliente;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public Instant getDataCadastro() {
        return dataCadastro;
    }
}