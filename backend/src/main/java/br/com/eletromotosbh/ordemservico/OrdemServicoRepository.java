package br.com.eletromotosbh.ordemservico;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrdemServicoRepository
        extends JpaRepository<OrdemServico, Long> {

    boolean existsByAgendamentoId(Long agendamentoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select ordem
        from OrdemServico ordem
        where ordem.id = :id
        """)
    Optional<OrdemServico> buscarParaAtualizacao(
            @Param("id") Long id
    );
}