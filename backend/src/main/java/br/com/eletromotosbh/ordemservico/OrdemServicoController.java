package br.com.eletromotosbh.ordemservico;

import br.com.eletromotosbh.ordemservico.dto.DecisaoOrcamentoRequest;
import br.com.eletromotosbh.ordemservico.dto.DiagnosticoOrcamentoRequest;
import br.com.eletromotosbh.ordemservico.dto.OrdemServicoResponse;
import br.com.eletromotosbh.ordemservico.dto.RecebimentoRequest;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import br.com.eletromotosbh.ordemservico.dto.ConclusaoServicoRequest;

import java.util.List;

@RestController
@RequestMapping("/api/ordens-servico")
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    public OrdemServicoController(
            OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    @PostMapping(value = "/receber-agendamento/{agendamentoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public OrdemServicoResponse receber(
            @PathVariable("agendamentoId") Long agendamentoId,
            @Valid @RequestPart("dados") RecebimentoRequest request,
            @RequestPart(value = "video", required = false) MultipartFile video) {
        return ordemServicoService.receber(
                agendamentoId,
                request,
                video);
    }

    @PatchMapping(value = "/{id}/diagnostico", consumes = MediaType.APPLICATION_JSON_VALUE)
    public OrdemServicoResponse registrarDiagnostico(
            @PathVariable("id") Long id,
            @Valid @RequestBody DiagnosticoOrcamentoRequest request) {
        return ordemServicoService.registrarDiagnostico(id, request);
    }

    @PatchMapping(value = "/{id}/decisao-orcamento", consumes = MediaType.APPLICATION_JSON_VALUE)
    public OrdemServicoResponse registrarDecisaoOrcamento(
            @PathVariable("id") Long id,
            @Valid @RequestBody DecisaoOrcamentoRequest request) {
        return ordemServicoService.registrarDecisaoOrcamento(
                id,
                request);
    }

    @PatchMapping("/{id}/iniciar-execucao")
    public OrdemServicoResponse iniciarExecucao(
            @PathVariable("id") Long id) {
        return ordemServicoService.iniciarExecucao(id);
    }

    @PatchMapping(value = "/{id}/concluir", consumes = MediaType.APPLICATION_JSON_VALUE)
    public OrdemServicoResponse concluir(
            @PathVariable("id") Long id,
            @Valid @RequestBody ConclusaoServicoRequest request) {
        return ordemServicoService.concluir(id, request);
    }

    @GetMapping
    public List<OrdemServicoResponse> listar() {
        return ordemServicoService.listar();
    }

    @GetMapping("/{id}")
    public OrdemServicoResponse buscarPorId(
            @PathVariable("id") Long id) {
        return ordemServicoService.buscarPorId(id);
    }

    @GetMapping("/{id}/video")
    public ResponseEntity<Resource> baixarVideo(
            @PathVariable("id") Long id) {
        var video = ordemServicoService.buscarVideo(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(video.tipo()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                .header("X-Content-Type-Options", "nosniff")
                .body(video.arquivo());
    }
}