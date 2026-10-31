package br.com.senac.bibliotech.application.service;

import br.com.senac.bibliotech.application.dto.UsuarioLogado;
import br.com.senac.bibliotech.application.exception.TokenException;
import br.com.senac.bibliotech.domain.entities.Token;
import br.com.senac.bibliotech.domain.entities.Usuario;
import br.com.senac.bibliotech.domain.enums.EnumStatusUsuario;
import br.com.senac.bibliotech.domain.repository.TokenRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/*
 * Gera o JWT, SALVA o token no banco e, na volta, confere as duas coisas (Aula 3):
 *   1. a assinatura e a validade do JWT;
 *   2. se o token existe no banco, ou seja, se fomos nós que o geramos.
 *
 * O JWT leva só o EMAIL (subject). Perfil e demais dados vêm do banco a cada
 * requisição, porque token vazado com dados dentro é inseguro.
 */
@Service
public class TokenService {

    private static final String EMISSOR = "bibliotech-api";

    private final Algorithm algorithm;
    private final long expiracaoMinutos;
    private final TokenRepository tokenRepository;

    public record TokenGerado(String valor, Instant expiraEm) {
    }

    public TokenService(
            @Value("${api.security.token.secret}") String segredo,
            @Value("${api.security.token.expiration-minutes:60}") long expiracaoMinutos,
            TokenRepository tokenRepository) {

        if (segredo.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET precisa ter 32 ou mais caracteres para HS256");
        }
        this.algorithm = Algorithm.HMAC256(segredo);
        this.expiracaoMinutos = expiracaoMinutos;
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public TokenGerado gerar(Usuario usuario) {
        Instant agora = Instant.now();
        Instant expiraEm = agora.plus(expiracaoMinutos, ChronoUnit.MINUTES);

        String token = JWT.create()
                .withIssuer(EMISSOR)
                .withSubject(usuario.getEmail())
                .withJWTId(UUID.randomUUID().toString()) // dois logins no mesmo segundo geram tokens diferentes
                .withIssuedAt(agora)
                .withExpiresAt(expiraEm)
                .sign(algorithm);

        tokenRepository.save(new Token(token, usuario));

        return new TokenGerado(token, expiraEm);
    }

    /**
     * Lança JWTVerificationException (assinatura/validade) ou TokenException
     * (token que não está no banco, ou de usuário que não está mais ATIVO).
     */
    @Transactional(readOnly = true)
    public UsuarioLogado verificarToken(String token) {
        JWT.require(algorithm).withIssuer(EMISSOR).build().verify(token);

        Token tokenBanco = tokenRepository.findTokenByToken(token)
                .orElseThrow(() -> new TokenException("Token inválido"));

        Usuario usuario = tokenBanco.getUsuario();
        if (usuario.getStatus() != EnumStatusUsuario.ATIVO) {
            throw new TokenException("Usuário inativo");
        }

        return new UsuarioLogado(usuario);
    }
}
