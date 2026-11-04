package br.com.eletromotosbh.mecanico;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MecanicoRepository extends JpaRepository<Mecanico, Long> {

    List<Mecanico> findByAtivoTrueOrderByOrdemRodizioAscIdAsc();

    @Query("select coalesce(max(m.ordemRodizio), 0) from Mecanico m")
    int buscarMaiorOrdemRodizio();
}