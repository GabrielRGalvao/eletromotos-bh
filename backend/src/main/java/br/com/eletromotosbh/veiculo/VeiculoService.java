package br.com.eletromotosbh.veiculo;

import br.com.eletromotosbh.cliente.Cliente;
import br.com.eletromotosbh.cliente.ClienteRepository;
import br.com.eletromotosbh.veiculo.dto.VeiculoRequest;
import br.com.eletromotosbh.veiculo.dto.VeiculoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;

    public VeiculoService(
            VeiculoRepository veiculoRepository,
            ClienteRepository clienteRepository
    ) {
        this.veiculoRepository = veiculoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public VeiculoResponse cadastrar(VeiculoRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Cliente não encontrado"
            ));

        String placa = request.placa();

        if (placa != null) {
            placa = placa.trim();

            if (placa.isEmpty()) {
                placa = null;
            }
        }

        Veiculo veiculo = new Veiculo(
            cliente,
            request.tipo().trim(),
            request.marca().trim(),
            request.modelo().trim(),
            request.cor().trim(),
            placa
        );

        Veiculo salvo = veiculoRepository.save(veiculo);
        return converterParaResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<VeiculoResponse> listarPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Cliente não encontrado"
            );
        }

        return veiculoRepository.findByClienteIdOrderByIdAsc(clienteId)
            .stream()
            .map(this::converterParaResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public VeiculoResponse buscarPorId(Long id) {
        Veiculo veiculo = veiculoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Veículo não encontrado"
            ));

        return converterParaResponse(veiculo);
    }

    private VeiculoResponse converterParaResponse(Veiculo veiculo) {
        return new VeiculoResponse(
            veiculo.getId(),
            veiculo.getCliente().getId(),
            veiculo.getTipo(),
            veiculo.getMarca(),
            veiculo.getModelo(),
            veiculo.getCor(),
            veiculo.getPlaca(),
            veiculo.getDataCadastro()
        );
    }
}