package br.com.senac.bibliotech.config;

import br.com.senac.bibliotech.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component // por que component?
public class JwtFIlter extends OncePerRequestFilter { // pesquisar mais sobre filtros do spring


    /*
                    PEQUISAR MAIS SOBRE INTERCEPTADORES E POR QUE USA-LOS

                    Fluxo de funcionamento:

                    1 - Requisição chega -> filtro verifica se precisa de autentiucation
                    2 - Se precisa -> verigfica header "Autorizaion: Beares <token>
                    3- valida toke com o token service
                    3- se válido -> define usuario no Securirity context e deixa passar
                    4- se inválido -> 401 nao autorizado
     */

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (uri.startsWith("/swagger-ui")
                || uri.startsWith("/v2/api-docs")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-resources")
                || uri.startsWith("/webjars")
                || uri.startsWith("/auth/login")
                || uri.equals("/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");
            try {
                var jwtOptional = tokenService.validar(token);
                System.out.println(jwtValidador.getSubject());
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().println("Token inválido");
                return;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Token inválido");
            return;
        }

        filterChain.doFilter(request, response);
    }
}


    /*
    protected por que precisa la vamos nós:
    template method

    a própria classe e as classes filhassubclasses podem acessar

    o spring precisa grantir que o filtro rode apenas uma vez por requisição, mas ele também precisa dar um lugar para escrever logica do jwt

     - interface: FIlçter que é publica: java exige filtros tenha filtros public. O spring implementa isso na classe OncePerRequestFIlter
     - OncePerRequestFIlter (GERENCIADOR): O metodo public doFIltier do Srping verifica "Esse filtro já rodou na requisição?" se não rodou, chama o metodo protected doFilterInternal
     - Classe JwtFilter (SERVO): Como doFilterInternal é protected, a sua classe(que é filha de OncePerRequestFIlter) tem permissão para ver e sobrescrevelo


    Por que não public?
    Porque ninguém de "fora" (como o Tomcat ou outras classes do seu sistema) deve chamar doFilterInternal diretamente. Eles devem chamar o doFilter público, que é o "gerente" que controla o fluxo.

    Por que não private?
    Porque se fosse private, a sua classe JwtFilter não conseguiria enxergar o metodo para poder sobrescrevelo e colocar logica de validacao ddo token
     */

