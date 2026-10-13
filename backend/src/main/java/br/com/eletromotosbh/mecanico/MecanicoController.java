package br.com.eletromotosbh.mecanico;

import br.com.eletromotosbh.mecanico.dto.MecanicoRequest;
import br.com.eletromotosbh.mecanico.dto.MecanicoResponse;
import br.com.eletromotosbh.mecanico.dto.MecanicoStatusRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mecanicos")
public class MecanicoController {

    private final MecanicoService mecanicoService;

    public MecanicoController(MecanicoService mecanicoService) {
        this.mecanicoService = mecanicoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MecanicoResponse cadastrar(
            @Valid @RequestBody MecanicoRequest request
    ) {
        return mecanicoService.cadastrar(request);
    }

    @GetMapping
    public List<MecanicoResponse> listar(
            @RequestParam(name = "somenteAtivos", defaultValue = "false")
            boolean somenteAtivos
    ) {
        return mecanicoService.listar(somenteAtivos);
    }

    @GetMapping("/{id}")
    public MecanicoResponse buscarPorId(@PathVariable("id") Long id) {
        return mecanicoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public MecanicoResponse atualizar(
            @PathVariable("id") Long id,
            @Valid @RequestBody MecanicoRequest request
    ) {
        return mecanicoService.atualizar(id, request);
    }

    @PatchMapping("/{id}/status")
    public MecanicoResponse alterarStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody MecanicoStatusRequest request
    ) {
        return mecanicoService.alterarStatus(id, request.ativo());
    }
}