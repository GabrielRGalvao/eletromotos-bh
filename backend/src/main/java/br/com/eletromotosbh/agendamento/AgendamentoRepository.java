package br.com.eletromotosbh.agendamento;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AgendamentoRepository
        extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByDataAgendamentoOrderByHoraAgendamentoAscIdAsc(
        LocalDate dataAgendamento
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Agendamento a where a.id = :id")
    Optional<Agendamento> buscarParaRecebimento(
        @Param("id") Long id
    );
}