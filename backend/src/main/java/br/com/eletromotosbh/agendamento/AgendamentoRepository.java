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

    @Query("""
            select a from Agendamento a
            join a.veiculo v
            join v.cliente c
            where (:data is null or a.dataAgendamento = :data)
              and (
                :busca is null
                or lower(c.nome) like lower(concat('%', :busca, '%'))
                or c.telefone like concat('%', :busca, '%')
              )
            order by a.dataAgendamento, a.horaAgendamento, a.id
            """)
    List<Agendamento> filtrar(
            @Param("data") LocalDate data,
            @Param("busca") String busca);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Agendamento a where a.id = :id")
    Optional<Agendamento> buscarParaRecebimento(@Param("id") Long id);
}