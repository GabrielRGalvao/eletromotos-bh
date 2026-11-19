
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

        List<Agendamento> findAllByOrderByDataAgendamentoAscHoraAgendamentoAscIdAsc();

        List<Agendamento> findByDataAgendamentoOrderByDataAgendamentoAscHoraAgendamentoAscIdAsc(
                        LocalDate dataAgendamento);

        @Query("""
                        select a from Agendamento a
                        join a.veiculo v
                        join v.cliente c
                        where lower(c.nome) like lower(concat('%', :busca, '%'))
                           or c.telefone like concat('%', :busca, '%')
                        order by a.dataAgendamento, a.horaAgendamento, a.id
                        """)
        List<Agendamento> buscarPorClienteOuTelefone(
                        @Param("busca") String busca);

        @Query("""
                        select a from Agendamento a
                        join a.veiculo v
                        join v.cliente c
                        where a.dataAgendamento = :data
                          and (
                            lower(c.nome) like lower(concat('%', :busca, '%'))
                            or c.telefone like concat('%', :busca, '%')
                          )
                        order by a.dataAgendamento, a.horaAgendamento, a.id
                        """)
        List<Agendamento> buscarPorDataEClienteOuTelefone(
                        @Param("data") LocalDate data,
                        @Param("busca") String busca);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("select a from Agendamento a where a.id = :id")
        Optional<Agendamento> buscarParaRecebimento(
                        @Param("id") Long id);

        boolean existsByVeiculoId(Long veiculoId);
}
