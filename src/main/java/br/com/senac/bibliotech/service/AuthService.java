package br.com.senac.bibliotech.service;

import br.com.senac.bibliotech.dto.LoginRequest;
import br.com.senac.bibliotech.dto.LoginResponse;
import br.com.senac.bibliotech.entities.Usuario;
import br.com.senac.bibliotech.enums.EnumStatusUsuario;
import br.com.senac.bibliotech.exception.CredenciaisInvalidasException;
import br.com.senac.bibliotech.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Orquestra o login: acha o usuário, confere a senha, gera o token.
 * Cada peça tem seu dono: o repository busca, o PasswordEncoder compara,
 * o TokenService assina. O AuthService só coordena.
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

    // SEM @Transactional de propósito: o BCrypt leva ~100 ms e não usa o banco.
    // Uma transação aqui seguraria uma conexão do pool à toa. O findByEmail já roda
    // na sua própria transação curta, aberta pelo Spring Data.
    public LoginResponse login(LoginRequest request) {
        // Normalizar (minúsculo, sem espaços) precisa ser IGUAL no cadastro e no login,
        // senão "Ana@x.com" e "ana@x.com" viram duas pessoas diferentes.
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        // Por que comparar com um hash falso quando o usuário não existe?
        // O BCrypt é lento de propósito (~100 ms). Se pulássemos a comparação, "email
        // inexistente" responderia bem mais rápido que "senha errada", e um atacante
        // mediria o tempo para descobrir quais emails estão cadastrados (timing attack).
        String hashParaComparar = (usuario != null) ? usuario.getSenha() : hashFalso;
        boolean senhaConfere = passwordEncoder.matches(request.senha(), hashParaComparar);

        // Um único erro para os três casos: usuário inexistente, senha errada, inativo.
        // ASSUMIDO: o enum tem a constante ATIVO. Ajuste ao seu EnumStatusUsuario.
        if (usuario == null || !senhaConfere || usuario.getStatus() != EnumStatusUsuario.ATIVO) {
            throw new CredenciaisInvalidasException();
        }

        TokenService.TokenGerado token = tokenService.gerar(usuario);
        return new LoginResponse(token.valor(), "Bearer", token.expiraEm());
    }
}