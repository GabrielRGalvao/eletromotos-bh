package br.com.eletromotosbh.auth;

public record UsuarioAutenticadoResponse(
        String nome,
        String email
) {
}