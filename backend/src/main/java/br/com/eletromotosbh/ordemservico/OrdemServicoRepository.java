package br.com.eletromotosbh.ordemservico;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdemServicoRepository
        extends JpaRepository<OrdemServico, Long> {

    boolean existsByAgendamentoId(Long agendamentoId);
}