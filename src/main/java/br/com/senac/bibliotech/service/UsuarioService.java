package br.com.senac.bibliotech.service;

import br.com.senac.bibliotech.dto.AlterarSenhaRequest;
import br.com.senac.bibliotech.dto.AtualizarUsuarioRequest;
import br.com.senac.bibliotech.dto.UsuarioRequest;
import br.com.senac.bibliotech.dto.UsuarioResponse;
import br.com.senac.bibliotech.entities.Usuario;
import br.com.senac.bibliotech.enums.EnumPerfil;
import br.com.senac.bibliotech.enums.EnumStatusUsuario;
import br.com.senac.bibliotech.exception.ConflitoException;
import br.com.senac.bibliotech.exception.RecursoNaoEncontradoException;
import br.com.senac.bibliotech.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Regras de negócio de usuário. Tudo que o controller antigo fazia com "if" e
 * "orElse(null)" mora aqui, e o controller fica só com HTTP.
 *
 * ATENÇÃO ao import do @Transactional: é o do SPRING
 * (org.springframework.transaction.annotation), não o do jakarta.transaction.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest request) {
        String email = normalizar(request.email());

        // Erro amigável (409) na maioria dos casos. Mas duas requisições simultâneas
        // podem passar por este if ao mesmo tempo: a garantia FINAL é o UNIQUE no
        // banco (migration), e o ApiExceptionHandler trata o
        // DataIntegrityViolationException que ele lançaria.
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflitoException("Já existe um usuário com este email");
        }

        // builder() é um método ESTÁTICO gerado pelo Lombok (@SuperBuilder):
        // escreve-se Usuario.builder(), SEM "new".
        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(email)
                .senha(passwordEncoder.encode(request.senha())) // grava o HASH, nunca a senha
                .cpf(request.cpf())
                // O perfil é decidido AQUI, no servidor, e nunca pelo cliente.
                .perfil(EnumPerfil.BIBLIOTECARIO)
                .status(EnumStatusUsuario.ATIVO)
                .build();

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    // Usuários EXCLUIDOS (soft delete) não aparecem na listagem.
    // DOWNSIDE: sem paginação. Com muitos usuários, troque por Page<UsuarioResponse>.
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findByStatusNot(EnumStatusUsuario.EXCLUIDO).stream()
                .map(UsuarioResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscar(Long id) {
        return UsuarioResponse.from(buscarEntidade(id));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, AtualizarUsuarioRequest request) {
        Usuario usuario = buscarEntidade(id);
        String email = normalizar(request.email());

        // "existe OUTRO usuário com este email?" (o próprio não conta)
        if (usuarioRepository.existsByEmailAndIdNot(email, id)) {
            throw new ConflitoException("Já existe um usuário com este email");
        }

        // Copiamos campo a campo. O id vem da URL (fonte da verdade) e senha, perfil
        // e status ficam intactos. Sem save(): o Hibernate detecta a mudança da
        // entidade gerenciada e faz o UPDATE no commit (dirty checking).
        usuario.setNome(request.nome());
        usuario.setEmail(email);
        usuario.setCpf(request.cpf());

        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public UsuarioResponse atualizarStatus(Long id, EnumStatusUsuario status) {
        Usuario usuario = buscarEntidade(id);
        usuario.setStatus(status);
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public UsuarioResponse atualizarPerfil(Long id, EnumPerfil perfil) {
        Usuario usuario = buscarEntidade(id);
        usuario.setPerfil(perfil);
        // LIMITAÇÃO: se o token do usuário já foi emitido, ele continua com o perfil
        // antigo até expirar (o perfil vai dentro do JWT).
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public void alterarSenha(Long id, AlterarSenhaRequest request) {
        Usuario usuario = buscarEntidade(id);

        if (!passwordEncoder.matches(request.senhaAtual(), usuario.getSenha())) {
            // 409 é o mais próximo que temos. Depois vale criar uma exceção própria
            // (400/422), porque um 401 aqui faria o front deslogar o usuário.
            throw new ConflitoException("A senha atual está incorreta");
        }

        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
    }

    // SOFT DELETE: o registro continua no banco com status EXCLUIDO.
    // Por quê? Um usuário pode ter empréstimos no histórico; apagar a linha quebraria
    // a FK ou destruiria a auditoria.
    // DOWNSIDE: o email do excluído continua ocupado pelo UNIQUE, então ninguém
    // consegue se cadastrar de novo com ele (o certo depois é anonimizar os dados).
    @Transactional
    public void excluir(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuario.setStatus(EnumStatusUsuario.EXCLUIDO);
    }

    // Um usuário EXCLUIDO é tratado como "não existe" (404) em todas as operações.
    private Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .filter(usuario -> usuario.getStatus() != EnumStatusUsuario.EXCLUIDO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }

    // Locale.ROOT: o resultado não depende do idioma da máquina (o turco, por
    // exemplo, converte "I" de um jeito diferente). Use a MESMA regra no login.
    private String normalizar(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}