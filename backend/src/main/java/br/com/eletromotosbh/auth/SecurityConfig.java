
package br.com.eletromotosbh.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(AdministradorProperties.class)
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public UserDetailsService userDetailsService(
                        AdministradorProperties administrador,
                        PasswordEncoder passwordEncoder) {

                if (administrador.email() == null || administrador.email().isBlank()
                                || administrador.senha() == null || administrador.senha().isBlank()) {
                        throw new IllegalStateException(
                                        "Defina APP_ADMIN_EMAIL e APP_ADMIN_SENHA no arquivo .env");
                }

                var usuario = User.withUsername(administrador.email().trim().toLowerCase())
                                .password(passwordEncoder.encode(administrador.senha()))
                                .roles("ADMIN")
                                .build();

                return new InMemoryUserDetailsManager(usuario);
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        UserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {

                var provider = new DaoAuthenticationProvider(userDetailsService);
                provider.setPasswordEncoder(passwordEncoder);

                return new ProviderManager(provider);
        }

        @Bean
        public SecurityContextRepository securityContextRepository() {
                return new HttpSessionSecurityContextRepository();
        }

        @Bean
        public CookieCsrfTokenRepository csrfTokenRepository(
                        @Value("${server.servlet.session.cookie.secure:false}") boolean cookieSecure) {

                var repository = CookieCsrfTokenRepository.withHttpOnlyFalse();

                repository.setCookiePath("/");
                repository.setCookieCustomizer(cookie -> cookie
                                .path("/")
                                .httpOnly(false)
                                .secure(cookieSecure)
                                .sameSite("Lax"));

                return repository;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        SecurityContextRepository securityContextRepository,
                        CookieCsrfTokenRepository csrfTokenRepository) throws Exception {

                return http
                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(csrfTokenRepository)
                                                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
                                .httpBasic(basic -> basic.disable())
                                .formLogin(form -> form.disable())
                                .logout(logout -> logout.disable())
                                .securityContext(context -> context
                                                .securityContextRepository(securityContextRepository)
                                                .requireExplicitSave(true))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                                .authorizeHttpRequests(autorizacoes -> autorizacoes
                                                .requestMatchers(
                                                                "/api/auth/login",
                                                                "/api/auth/csrf",
                                                                "/api/status")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .build();
        }

        private static final class SpaCsrfTokenRequestHandler
                        implements CsrfTokenRequestHandler {

                private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();

                private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

                @Override
                public void handle(
                                HttpServletRequest request,
                                HttpServletResponse response,
                                Supplier<CsrfToken> csrfToken) {

                        this.xor.handle(request, response, csrfToken);
                        csrfToken.get();
                }

                @Override
                public String resolveCsrfTokenValue(
                                HttpServletRequest request,
                                CsrfToken csrfToken) {

                        String headerValue = request.getHeader(csrfToken.getHeaderName());

                        return (StringUtils.hasText(headerValue) ? this.plain : this.xor)
                                        .resolveCsrfTokenValue(request, csrfToken);
                }
        }
}
