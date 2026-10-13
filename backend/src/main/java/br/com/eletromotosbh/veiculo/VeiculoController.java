package br.com.eletromotosbh.veiculo;

import br.com.eletromotosbh.veiculo.dto.VeiculoRequest;
import br.com.eletromotosbh.veiculo.dto.VeiculoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VeiculoResponse cadastrar(
            @Valid @RequestBody VeiculoRequest request
    ) {
        return veiculoService.cadastrar(request);
    }

    @GetMapping
    public List<VeiculoResponse> listarPorCliente(
            @RequestParam("clienteId") Long clienteId
    ) {
        return veiculoService.listarPorCliente(clienteId);
    }

    @GetMapping("/{id}")
    public VeiculoResponse buscarPorId(@PathVariable("id") Long id) {
        return veiculoService.buscarPorId(id);
    }
}