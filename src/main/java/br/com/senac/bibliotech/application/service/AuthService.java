package br.com.senac.bibliotech.application.service;

import br.com.senac.bibliotech.application.dto.LoginRequest;
import br.com.senac.bibliotech.application.dto.LoginResponse;
import br.com.senac.bibliotech.domain.entities.Usuario;
import br.com.senac.bibliotech.domain.enums.EnumStatusUsuario;
import br.com.senac.bibliotech.application.exception.CredenciaisInvalidasException;
import br.com.senac.bibliotech.domain.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import br.com.senac.bibliotech.application.dto.UsuarioLogadoResponse;

import java.util.Locale;

/*

fluxo:

 orquestra do login -> acha o usuario -> confere a senha -> gera o token.
 cada panela com sua tampa - o repository busca, o PasswordEncoder compara, o TokenService assina.

  AuthService so coordena.
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    // Hash "de mentira", usado quando o email não existe (explicação no login()).
    private final String hashFalso;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.hashFalso = passwordEncoder.encode("senha-descartavel");
    }


    /*
    por que sem o transactional?

    resposta = mema coisa, bcrypt lento, muito texto
     */
    public LoginResponse login(LoginRequest request) {
        // normalização pra nao dar probnlema
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        /*
        pq comparar um hash falso cse o uisuario nao existir?
        resposta = O bcryp é lento de proposito, se pulasse a comparation, ele respoonderia mais rapido
        perigo de timing atack
         */
        String hashParaComparar = (usuario != null) ? usuario.getSenha() : hashFalso;
        boolean senhaConfere = passwordEncoder.matches(request.senha(), hashParaComparar);

        /*
        um erro só pra os casos de usuario, senha errada e inativo
         */
        if (usuario == null || !senhaConfere || usuario.getStatus() != EnumStatusUsuario.ATIVO) {
            throw new CredenciaisInvalidasException();
        }
         /*
         mnenkadsnasnidjasjdnasndjasn
          */
        TokenService.TokenGerado token = tokenService.gerar(usuario);
        return new LoginResponse(token.valor(), "Bearer", token.expiraEm(), UsuarioLogadoResponse.from(usuario));
    }

    // novo metodo pra logout quanto o token fort revodgado, como fazer isso?
        // r - colocar no authcontroller seu burro

    public void logout(String token) {
        tokenService.revogar(token);
    }
}