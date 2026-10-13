package br.com.eletromotosbh.veiculo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    List<Veiculo> findByClienteIdOrderByIdAsc(Long clienteId);

    boolean existsByClienteId(Long clienteId);
}