package br.com.senac.bibliotech.infrastructure.security;

import br.com.senac.bibliotech.application.port.TokenPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final TokenPort tokenPort;

    public SecurityConfiguration(TokenPort tokenPort) {
        this.tokenPort = tokenPort;
    }

    // Cadeia de filtros de segurança (primeiro match vence).
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())

                // API stateless com JWT, não precisa de CSRF
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Sem token (ou token recusado) = 401. O front usa o 401 para voltar ao login.
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                .authorizeHttpRequests(auth -> auth

                        // Público
                        .requestMatchers(
                                "/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-resources/**", "/webjars/**",
                                "/auth/login", "/auth/bootstrap", "/error"
                        ).permitAll()

                        // Preflight do CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Apenas ADMIN
                        .requestMatchers(HttpMethod.PATCH,
                                "/usuarios/*/perfil",
                                "/usuarios/*/status")
                        .hasRole("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.DELETE,
                                "/usuarios/**").hasRole("ADMINISTRADOR")

                        // ADMIN ou BIBLIOTECARIO
                        .requestMatchers(HttpMethod.POST,
                                "/livros", "/autores", "/exemplares", "/usuarios")
                        .hasAnyRole("ADMINISTRADOR", "BIBLIOTECARIO")

                        .requestMatchers(HttpMethod.PUT,
                                "/livros/**", "/autores/**", "/usuarios/**")
                        .hasAnyRole("ADMINISTRADOR", "BIBLIOTECARIO")

                        .requestMatchers(HttpMethod.DELETE,
                                "/livros/**", "/autores/**", "/exemplares/**")
                        .hasAnyRole("ADMINISTRADOR", "BIBLIOTECARIO")

                        .requestMatchers(HttpMethod.PATCH,
                                "/exemplares/*/status")
                        .hasAnyRole("ADMINISTRADOR", "BIBLIOTECARIO")

                        // Empréstimos: qualquer usuário autenticado
                        .requestMatchers(HttpMethod.POST, "/emprestimos").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/emprestimos/*/devolver").authenticated()

                        // SEMPRE por último
                        .anyRequest().authenticated()
                )

                .addFilterBefore(new JwtFilter(tokenPort),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }
}