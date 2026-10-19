package br.com.eletromotosbh.exception;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> tratarValidacao(
            MethodArgumentNotValidException exception) {

        Map<String, String> campos = new TreeMap<>();

        for (var erro : exception.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(
                    erro.getField(),
                    erro.getDefaultMessage());
        }

        ApiError resposta = new ApiError(
                400,
                "Confira os campos informados",
                campos);

        return ResponseEntity.badRequest().body(resposta);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> tratarErroDeStatus(
            ResponseStatusException exception) {

        String mensagem = exception.getReason();

        if (mensagem == null) {
            mensagem = "Não foi possível concluir a solicitação";
        }

        ApiError resposta = new ApiError(
                exception.getStatusCode().value(),
                mensagem,
                Map.of());

        return ResponseEntity
                .status(exception.getStatusCode())
                .headers(exception.getHeaders())
                .body(resposta);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> tratarArquivoMuitoGrande(
            MaxUploadSizeExceededException exception) {
        ApiError resposta = new ApiError(
                413,
                "O vídeo deve ter no máximo 100 MB",
                Map.of());

        return ResponseEntity.status(413).body(resposta);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiError> tratarParteAusente(
            MissingServletRequestPartException exception) {
        ApiError resposta = new ApiError(
                400,
                "Envie a parte obrigatória: "
                        + exception.getRequestPartName(),
                Map.of());

        return ResponseEntity.badRequest().body(resposta);
    }
}