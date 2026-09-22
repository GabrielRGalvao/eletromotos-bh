package br.com.eletromotosbh.exception;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> tratarValidacao(
            MethodArgumentNotValidException exception) {

        Map<String, String> campos = new TreeMap<>();

        for (var erro : exception.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(
                erro.getField(),
                erro.getDefaultMessage()
            );
        }

        ApiError resposta = new ApiError(
            400,
            "Confira os campos informados",
            campos
        );

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
            Map.of()
        );

        return ResponseEntity
            .status(exception.getStatusCode())
            .headers(exception.getHeaders())
            .body(resposta);
    }
}