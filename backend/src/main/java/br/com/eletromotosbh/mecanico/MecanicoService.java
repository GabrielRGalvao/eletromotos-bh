package br.com.eletromotosbh.mecanico;

import br.com.eletromotosbh.mecanico.dto.MecanicoRequest;
import br.com.eletromotosbh.mecanico.dto.MecanicoResponse;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MecanicoService {

    private final MecanicoRepository mecanicoRepository;

    public MecanicoService(MecanicoRepository mecanicoRepository) {
        this.mecanicoRepository = mecanicoRepository;
    }

    @Transactional
    public MecanicoResponse cadastrar(MecanicoRequest request) {
        Mecanico mecanico = new Mecanico(request.nome().trim());
        Mecanico salvo = mecanicoRepository.save(mecanico);

        return converterParaResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<MecanicoResponse> listar(boolean somenteAtivos) {
        List<Mecanico> mecanicos;

        if (somenteAtivos) {
            mecanicos = mecanicoRepository.findByAtivoTrueOrderByNomeAscIdAsc();
        } else {
            mecanicos = mecanicoRepository.findAll(Sort.by("nome", "id"));
        }

        return mecanicos.stream()
            .map(this::converterParaResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public MecanicoResponse buscarPorId(Long id) {
        return converterParaResponse(encontrarMecanico(id));
    }

    @Transactional
    public MecanicoResponse atualizar(Long id, MecanicoRequest request) {
        Mecanico mecanico = encontrarMecanico(id);
        mecanico.atualizarDados(request.nome().trim());

        return converterParaResponse(mecanico);
    }

    @Transactional
    public MecanicoResponse alterarStatus(Long id, boolean ativo) {
        Mecanico mecanico = encontrarMecanico(id);

        if (ativo) {
            mecanico.ativar();
        } else {
            mecanico.inativar();
        }

        return converterParaResponse(mecanico);
    }

    private Mecanico encontrarMecanico(Long id) {
        return mecanicoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Mecânico não encontrado"
            ));
    }

    private MecanicoResponse converterParaResponse(Mecanico mecanico) {
        return new MecanicoResponse(
            mecanico.getId(),
            mecanico.getNome(),
            mecanico.isAtivo(),
            mecanico.getDataCadastro()
        );
    }
}