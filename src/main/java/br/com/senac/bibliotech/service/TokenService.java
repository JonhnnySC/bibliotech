package br.com.senac.bibliotech.service;

/*
    Responsabilidade unica de criar e validar tokens do jtw

    JWT são tres bases separadas BASE64 separadas
    - Payload é legivel por qualquer um por base 64 nao ser criptografia, nunca colocar senha, cpf opu dado sensivel
    - A assinatudo (hmac e  segrefo) garante que ningeum atuterou o conteudo

    Trade offs = stateless: o servidor não guarda sessao, então escala rapido
                mas não dá para invalidar uim token antes de expirar
                (loggout real, usuario bloqueado)
                    mitigação - expiração curta - refresh token e lista de revogação existe
                HASH256 usa um segredo simetrico - quem invalida também consegue assinar
                serve bem, pra api unica. com varios serviços é melhor RS256.
 */

import br.com.senac.bibliotech.entities.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
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

    // resultado de gerar  - o  token e quando ele exopira e o AUTHSERVICE devolve ao cliente
    public record TokenGerado(String valor, Instant expiraEm){
    }

    /*
    O segredo vem de .env, nunca fixo no codigo
     */
    public TokenService(
            @org.springframework.beans.factory.annotation.Value("${api.security.token.secret}") String segredo,
            @org.springframework.beans.factory.annotation.Value("${api.security.token.expiration-minutes:60}") long expiracaoMinutos) {
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

    /*
     * Valida assinatura, emissor e expiração de uma vez.
     * Optional vazio = token inválido (adulterado, expirado, de outro emissor...).
     * Quem chamar (o JwtFilter) não precisa lidar com exceção nenhuma da biblioteca.
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
