package br.com.senac.bibliotech.application.service;

import br.com.senac.bibliotech.application.dto.LoginRequest;
import br.com.senac.bibliotech.application.dto.LoginResponse;
import br.com.senac.bibliotech.application.dto.UsuarioLogadoResponse;
import br.com.senac.bibliotech.application.exception.CredenciaisInvalidasException;
import br.com.senac.bibliotech.application.port.SenhaPort;
import br.com.senac.bibliotech.application.port.TokenPort;
import br.com.senac.bibliotech.domain.entities.Usuario;
import br.com.senac.bibliotech.domain.enums.EnumStatusUsuario;
import br.com.senac.bibliotech.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final SenhaPort senhaPort;
    private final TokenPort tokenPort;

    // Hash "de mentira", usado quando o e-mail não existe (evita timing attack).
    private final String hashFalso;

    public AuthService(UsuarioRepository usuarioRepository, SenhaPort senhaPort, TokenPort tokenPort) {
        this.usuarioRepository = usuarioRepository;
        this.senhaPort = senhaPort;
        this.tokenPort = tokenPort;
        this.hashFalso = senhaPort.codificar("senha-descartavel");
    }

    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        String hashParaComparar = (usuario != null) ? usuario.getSenha() : hashFalso;
        boolean senhaConfere = senhaPort.confere(request.senha(), hashParaComparar);

        if (usuario == null || !senhaConfere || usuario.getStatus() != EnumStatusUsuario.ATIVO) {
            throw new CredenciaisInvalidasException();
        }

        TokenPort.TokenEmitido token = tokenPort.emitir(usuario);
        return new LoginResponse(token.valor(), "Bearer", token.expiraEm(), UsuarioLogadoResponse.from(usuario));
    }

    public void logout(String token) {
        tokenPort.revogar(token);
    }
}