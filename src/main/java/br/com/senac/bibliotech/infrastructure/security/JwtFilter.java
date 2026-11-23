package br.com.senac.bibliotech.infrastructure.security;

import br.com.senac.bibliotech.application.exception.TokenException;
import br.com.senac.bibliotech.application.port.TokenPort;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
 Lê o "Authorization: Bearer", pede à PORTA de token para verificar (JWT + banco)
 e coloca o UsuarioLogado como principal do SecurityContext.
 A infraestrutura de segurança não conhece mais nenhuma classe concreta de serviço.
*/
public class JwtFilter extends OncePerRequestFilter {

    private final TokenPort tokenPort;

    public JwtFilter(TokenPort tokenPort) {
        this.tokenPort = tokenPort;
    }

    // Rotas públicas não passam pela verificação do token
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-resources")
                || uri.startsWith("/webjars")
                || uri.equals("/auth/login")
                || uri.equals("/auth/bootstrap")
                || uri.equals("/error");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Sem token: segue como anônimo. O SecurityConfiguration responde 401 nas rotas protegidas.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            var usuarioLogado = tokenPort.verificar(authHeader.substring(7));

            var autenticacao = new UsernamePasswordAuthenticationToken(
                    usuarioLogado,
                    null,
                    usuarioLogado.authorities());

            SecurityContextHolder.getContext().setAuthentication(autenticacao);
        } catch (JWTVerificationException | TokenException e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/problem+json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    "{\"title\":\"Unauthorized\",\"status\":401,\"detail\":\"Token inválido ou expirado\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}