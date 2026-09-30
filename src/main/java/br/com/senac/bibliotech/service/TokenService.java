package br.com.senac.bibliotech.service;

import br.com.senac.bibliotech.entities.Usuario;
import br.com.senac.bibliotech.exception.TokenException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class TokenService {

    private static final String EMISSOR = "bibliotech-api";
    public static final String CLAIM_PERFIL = "perfil";

    private final Algorithm algorithm;
    private final long expiracaoMinutos;

    public record TokenGerado(String valor, Instant expiraEm) {
    }

    public TokenService(
            @Value("${api.security.token.secret}") String segredo,
            @Value("${api.security.token.expiration-minutes:60}") long expiracaoMinutos) {

        if (segredo.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET precisa ter 32 ou mais caracteres para HS256");
        }
        this.algorithm = Algorithm.HMAC256(segredo);
        this.expiracaoMinutos = expiracaoMinutos;
    }

    public TokenGerado gerar(Usuario usuario) {
        Instant agora = Instant.now();
        Instant expiraEm = agora.plus(expiracaoMinutos, ChronoUnit.MINUTES);

        String token = JWT.create()
                .withIssuer(EMISSOR)
                .withSubject(String.valueOf(usuario.getId()))
                .withClaim(CLAIM_PERFIL, usuario.getPerfil().name())
                .withIssuedAt(agora)
                .withExpiresAt(expiraEm)
                .sign(algorithm);

        return new TokenGerado(token, expiraEm);
    }

    /**
     * Valida assinatura, emissor e expiração.
     * Retorna Optional vazio se o token for inválido.
     *
     * POR QUE Optional em vez de lançar exception?
     * - O filtro decide como tratar (retornar 401, logar, etc)
     * - Mantém o serviço mais flexível e reutilizável
     */
    public Optional<DecodedJWT> validar(String token) {
        try {
            return Optional.of(JWT.require(algorithm)
                    .withIssuer(EMISSOR)
                    .build()
                    .verify(token));
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }
}