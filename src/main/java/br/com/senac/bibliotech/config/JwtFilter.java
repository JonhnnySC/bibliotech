package br.com.senac.bibliotech.config;

import br.com.senac.bibliotech.exception.TokenException;
import br.com.senac.bibliotech.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();

        // POR QUE: Rotas públicas não precisam de token.
        // Swagger e login devem ser acessíveis sem autenticação.
        if (uri.startsWith("/swagger-ui")
                || uri.startsWith("/v2/api-docs")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/auth/login")) {

            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        // POR QUE: Verificamos se o header existe e começa com "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");

            try {
                // Valida o token
                var jwtOptional = tokenService.validar(token);

                if (jwtOptional.isPresent()) {
                    var jwt = jwtOptional.get();
                    String userId = jwt.getSubject();

                    // POR QUE: Criamos um objeto de autenticação e registramos no contexto
                    // do Spring Security. Agora o Spring "sabe" quem é o usuário.
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                } else {
                    // POR QUE: Token inválido ou expirado - lançamos exception unificada
                    throw new TokenException("Token inválido ou expirado");
                }

            } catch (TokenException e) {
                // POR QUE: Capturamos especificamente a exception de token
                // e retornamos 401 com mensagem clara
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"erro\": \"" + e.getMessage() + "\"}");
                return;

            } catch (Exception e) {
                // POR QUE: Qualquer outra exception inesperada também é tratada como 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"erro\": \"Erro ao processar token\"}");
                return;
            }

        } else {
            // POR QUE: Se não tem header Authorization, negamos acesso
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"erro\": \"Token não fornecido\"}");
            return;
        }

        // POR QUE: Se chegou aqui, o token é válido. Liberamos a requisição.
        filterChain.doFilter(request, response);
    }
}