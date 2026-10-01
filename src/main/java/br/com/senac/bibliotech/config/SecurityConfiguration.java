package br.com.senac.bibliotech.config;

import br.com.senac.bibliotech.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final TokenService tokenService;

    public SecurityConfiguration(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    //Cadeia de filtros de segurança  eprimeiro match vence).
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Libera
                .cors(Customizer.withDefaults())

                // stateless n precisa de token
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        //publico
                        .requestMatchers(
                                "/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-resources/**", "/webjars/**",
                                "/auth/login", "/auth/registro", "/error"
                        ).permitAll()

                        // Ppesquisar o pq sisso
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Apenas adm
                        .requestMatchers(HttpMethod.PATCH,
                                "/usuarios/*/perfil",
                                "/usuarios/*/status").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE,
                                "/usuarios/**").hasRole("ADMINISTRADOR")

                        // adm ou bibliotecrario
                        .requestMatchers(HttpMethod.POST,
                                "/livros", "/autores", "/exemplares", "/usuarios", "/emprestimos"
                        ).hasAnyRole("ADMINISTRADOR", "BIBLIOTECARIO")

                        // Editar cooisas
                        .requestMatchers(HttpMethod.PUT,
                                "/livros/**", "/autores/**").hasAnyRole("ADMINISTRADOR", "BIBLIOTECARIO")
                        .requestMatchers(HttpMethod.DELETE,
                                "/livros/**", "/autores/**", "/exemplares/**"
                        ).hasAnyRole("ADMINISTRADOR", "BIBLIOTECARIO")

                        // Mudar status de exemplar
                        .requestMatchers(HttpMethod.PATCH,
                                "/exemplares/*/status").hasAnyRole("ADMINISTRADOR", "BIBLIOTECARIO")

                        //qualquwer um
                        .requestMatchers(HttpMethod.PATCH,
                                "/emprestimos/*/devolver").authenticated()
                        .anyRequest().authenticated()
                )

                // JwtFilter roda ANTES do filtro de autenticação padrão:
                // ele lê o token, valida e popula o SecurityContext com o perfil do usuário
                .addFilterBefore(new JwtFilter(tokenService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // mostra o AuthenticationManager para o AuthService usar no login (email + senha).
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }
}