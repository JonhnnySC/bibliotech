package br.com.senac.bibliotech.infrastructure.security;

import br.com.senac.bibliotech.application.dto.UsuarioLogado;
import br.com.senac.bibliotech.application.exception.TokenException;
import br.com.senac.bibliotech.application.port.TokenPort;
import br.com.senac.bibliotech.domain.entities.Token;
import br.com.senac.bibliotech.domain.entities.Usuario;
import br.com.senac.bibliotech.domain.enums.EnumStatusUsuario;
import br.com.senac.bibliotech.domain.repository.TokenRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/*
 Adaptador da porta de token: auth0/JWT e a tabela de tokens são detalhes daqui.
 O JWT leva só o e-mail; perfil e biblioteca vêm do banco a cada requisição.
*/
@Component
public class JwtTokenAdapter implements TokenPort {

    private static final String EMISSOR = "bibliotech-api";

    private final Algorithm algorithm;
    private final long expiracaoMinutos;
    private final TokenRepository tokenRepository;

    public JwtTokenAdapter(
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

    @Override
    @Transactional
    public TokenEmitido emitir(Usuario usuario) {
        Instant agora = Instant.now();
        Instant expiraEm = agora.plus(expiracaoMinutos, ChronoUnit.MINUTES);

        String token = JWT.create()
                .withIssuer(EMISSOR)
                .withSubject(usuario.getEmail())
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(agora)
                .withExpiresAt(expiraEm)
                .sign(algorithm);

        tokenRepository.save(new Token(token, usuario));
        return new TokenEmitido(token, expiraEm);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioLogado verificar(String token) {
        JWT.require(algorithm).withIssuer(EMISSOR).build().verify(token);

        Token tokenBanco = tokenRepository.findTokenByToken(token)
                .orElseThrow(() -> new TokenException("Token inválido"));

        Usuario usuario = tokenBanco.getUsuario();
        if (usuario.getStatus() != EnumStatusUsuario.ATIVO) {
            throw new TokenException("Usuário inativo");
        }
        return new UsuarioLogado(usuario);
    }

    @Override
    @Transactional
    public void revogar(String token) {
        tokenRepository.findTokenByToken(token).ifPresent(tokenRepository::delete);
    }

    @Override
    @Transactional
    public void revogarTodos() {
        tokenRepository.deleteAllInBatch();
    }
}