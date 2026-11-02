package br.com.eletromotosbh.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final AdministradorProperties administrador;

    public AuthController(
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository,
            AdministradorProperties administrador) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.administrador = administrador;
    }

    @PostMapping("/login")
    public UsuarioAutenticadoResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Authentication autenticacao;

        try {
            autenticacao = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.email().trim().toLowerCase(),
                            request.senha()));
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "E-mail ou senha inválidos");
        }

        var contexto = SecurityContextHolder.createEmptyContext();
        contexto.setAuthentication(autenticacao);
        SecurityContextHolder.setContext(contexto);

        securityContextRepository.saveContext(
                contexto,
                httpRequest,
                httpResponse);

        return usuarioAtual();
    }

    @GetMapping("/me")
    public UsuarioAutenticadoResponse me() {
        return usuarioAtual();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        var sessao = request.getSession(false);

        if (sessao != null) {
            sessao.invalidate();
        }

        SecurityContextHolder.clearContext();
    }

    private UsuarioAutenticadoResponse usuarioAtual() {
        String nome = administrador.nome();

        if (nome == null || nome.isBlank()) {
            nome = "Administrador";
        }

        return new UsuarioAutenticadoResponse(
                nome,
                administrador.email());
    }
}