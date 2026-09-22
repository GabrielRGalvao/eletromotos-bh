package br.com.eletromotosbh.exception;

import java.util.Map;

public record ApiError(
    int status,
    String mensagem,
    Map<String, String> campos
) {
}