package br.com.eletromotosbh.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin")
public record AdministradorProperties(
        String nome,
        String email,
        String senha
) {
}