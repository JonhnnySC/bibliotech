package br.com.senac.bibliotech.infrastructure.config;

import br.com.senac.bibliotech.application.service.TokenService;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class JwtFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public JwtFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Optional<DecodedJWT> jwtOptional = tokenService.validar(token);

            if (jwtOptional.isPresent()) {
                DecodedJWT jwt = jwtOptional.get();
                String userId = jwt.getSubject();

                // lê o claim "perfil" (ADMINISTRADOR, BIBLIOTECARIO, LEITOR)
                String perfil = jwt.getClaim("perfil").asString();

                // hasRole("X") procura a authority "ROLE_X"
                List<GrantedAuthority> authorities = (perfil == null || perfil.isBlank())
                        ? Collections.emptyList()
                        : List.of(new SimpleGrantedAuthority("ROLE_" + perfil));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}