package br.com.eletromotosbh.agendamento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AgendamentoRepository
        extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByDataAgendamentoOrderByHoraAgendamentoAscIdAsc(
        LocalDate dataAgendamento
    );
}