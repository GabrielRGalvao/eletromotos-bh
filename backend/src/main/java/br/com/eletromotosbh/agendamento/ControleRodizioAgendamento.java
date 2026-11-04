package br.com.eletromotosbh.agendamento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "controle_rodizio_agendamentos")
public class ControleRodizioAgendamento {

    @Id
    private Short id;

    @Column(name = "ultimo_mecanico_id")
    private Long ultimoMecanicoId;

    protected ControleRodizioAgendamento() {
    }

    public Long getUltimoMecanicoId() {
        return ultimoMecanicoId;
    }

    public void registrarUltimoMecanico(Long mecanicoId) {
        this.ultimoMecanicoId = mecanicoId;
    }
}