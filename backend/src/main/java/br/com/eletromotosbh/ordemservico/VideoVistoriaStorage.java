package br.com.eletromotosbh.ordemservico;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Service
public class VideoVistoriaStorage {

    private static final Logger log =
        LoggerFactory.getLogger(VideoVistoriaStorage.class);

    private static final long LIMITE_BYTES =
        100L * 1024 * 1024;

    private static final Map<String, String> EXTENSOES = Map.of(
        "video/mp4", ".mp4",
        "video/webm", ".webm",
        "video/quicktime", ".mov"
    );

    private final Path diretorio;

    public VideoVistoriaStorage(
            @Value("${app.videos.diretorio}") String diretorio
    ) {
        this.diretorio = Path.of(diretorio)
            .toAbsolutePath()
            .normalize();
    }

    public record VideoSalvo(
        String chave,
        String tipo,
        long tamanho
    ) {
    }

    public VideoSalvo salvar(MultipartFile video) {
        if (video == null || video.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Envie o vídeo de vistoria"
            );
        }

        if (video.getSize() > LIMITE_BYTES) {
            throw new ResponseStatusException(
                HttpStatus.valueOf(413),
                "O vídeo deve ter no máximo 100 MB"
            );
        }

        String tipo = video.getContentType();

        if (tipo == null || !EXTENSOES.containsKey(tipo)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Envie um vídeo MP4, WebM ou MOV"
            );
        }

        Path arquivo = null;

        try {
            Files.createDirectories(diretorio);

            arquivo = Files.createTempFile(
                diretorio,
                "vistoria-",
                EXTENSOES.get(tipo)
            );

            long tamanho;

            try (InputStream entrada = video.getInputStream()) {
                tamanho = Files.copy(
                    entrada,
                    arquivo,
                    StandardCopyOption.REPLACE_EXISTING
                );
            }

            return new VideoSalvo(
                arquivo.getFileName().toString(),
                tipo,
                tamanho
            );

        } catch (IOException exception) {
            if (arquivo != null) {
                excluir(arquivo.getFileName().toString());
            }

            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Não foi possível armazenar o vídeo de vistoria",
                exception
            );
        }
    }

    public Resource carregar(String chave) {
        Path arquivo = resolverCaminho(chave);

        if (!Files.isRegularFile(arquivo)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Vídeo de vistoria não encontrado"
            );
        }

        return new FileSystemResource(arquivo);
    }

    public void excluir(String chave) {
        Path arquivo = resolverCaminho(chave);

        try {
            Files.deleteIfExists(arquivo);
        } catch (IOException exception) {
            log.error(
                "Não foi possível remover o vídeo {}",
                chave,
                exception
            );
        }
    }

    private Path resolverCaminho(String chave) {
        Path arquivo = diretorio.resolve(chave).normalize();

        if (!diretorio.equals(arquivo.getParent())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Identificação de vídeo inválida"
            );
        }

        return arquivo;
    }
}