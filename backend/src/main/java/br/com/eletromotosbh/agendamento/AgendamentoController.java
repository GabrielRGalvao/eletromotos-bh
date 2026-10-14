package br.com.eletromotosbh.agendamento;

import br.com.eletromotosbh.agendamento.dto.AgendamentoRequest;
import br.com.eletromotosbh.agendamento.dto.AgendamentoResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgendamentoResponse cadastrar(
            @Valid @RequestBody AgendamentoRequest request
    ) {
        return agendamentoService.cadastrar(request);
    }

    @GetMapping
    public List<AgendamentoResponse> listar(
            @RequestParam(name = "data", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate data
    ) {
        return agendamentoService.listar(data);
    }

    @GetMapping("/{id}")
    public AgendamentoResponse buscarPorId(
            @PathVariable("id") Long id
    ) {
        return agendamentoService.buscarPorId(id);
    }
}