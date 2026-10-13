package br.com.eletromotosbh.mecanico;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MecanicoRepository extends JpaRepository<Mecanico, Long> {

    List<Mecanico> findByAtivoTrueOrderByNomeAscIdAsc();
}