package br.com.senac.bibliotech.application.service;

import br.com.senac.bibliotech.application.dto.AlterarSenhaRequest;
import br.com.senac.bibliotech.application.dto.AtualizarUsuarioRequest;
import br.com.senac.bibliotech.application.dto.UsuarioLogado;
import br.com.senac.bibliotech.application.dto.UsuarioRequest;
import br.com.senac.bibliotech.application.dto.UsuarioResponse;
import br.com.senac.bibliotech.application.exception.AcessoNegadoException;
import br.com.senac.bibliotech.application.port.SenhaPort;
import br.com.senac.bibliotech.application.port.UsuarioLogadoPort;
import br.com.senac.bibliotech.domain.entities.Leitor;
import br.com.senac.bibliotech.domain.entities.Usuario;
import br.com.senac.bibliotech.domain.enums.EnumPerfil;
import br.com.senac.bibliotech.domain.enums.EnumStatusUsuario;
import br.com.senac.bibliotech.domain.exception.ConflitoException;
import br.com.senac.bibliotech.domain.exception.RecursoNaoEncontradoException;
import br.com.senac.bibliotech.domain.repository.BibliotecaRepository;
import br.com.senac.bibliotech.domain.repository.UsuarioRepository;
import br.com.senac.bibliotech.domain.valueObject.CPF;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;

/*
 Regras de negócio de usuário. A camada de aplicação só conhece PORTAS:
 SenhaPort (BCrypt) e UsuarioLogadoPort (SecurityContext) ficam na infraestrutura.
*/
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SenhaPort senhaPort;
    private final UsuarioLogadoPort usuarioLogadoPort;
    private final BibliotecaRepository bibliotecaRepository;

    // Senha especial exigida para promover alguém a ADMINISTRADOR.
    @Value("${app.admin.senha-promocao:}")
    private String senhaPromocao;

    public UsuarioService(UsuarioRepository usuarioRepository, SenhaPort senhaPort,
                          UsuarioLogadoPort usuarioLogadoPort, BibliotecaRepository bibliotecaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.senhaPort = senhaPort;
        this.usuarioLogadoPort = usuarioLogadoPort;
        this.bibliotecaRepository = bibliotecaRepository;
    }

    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest request) {
        String email = normalizar(request.email());
        CPF cpf = new CPF(request.cpf());

        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflitoException("Já existe um usuário com este email");
        }
        if (usuarioRepository.existsByCpf_Cpf(cpf.getCpf())) {
            throw new ConflitoException("Já existe um usuário com este CPF");
        }

        Leitor leitor = Leitor.builder()
                .nome(request.nome())
                .email(email)
                .senha(senhaPort.codificar(request.senha())) // grava o HASH, nunca a senha
                .cpf(cpf)
                .perfil(EnumPerfil.LEITOR)
                .status(EnumStatusUsuario.ATIVO)
                .build();

        // Multibiblioteca: quem cadastra define a biblioteca (pelo token), nunca o front.
        UsuarioLogado logado = usuarioLogadoOuNulo();
        if (logado != null && logado.bibliotecaId() != null) {
            leitor.setBiblioteca(bibliotecaRepository.referencia(logado.bibliotecaId()));
        }

        return UsuarioResponse.from(usuarioRepository.save(leitor));
    }

    public UsuarioLogado getUsuarioLogado() {
        UsuarioLogado logado = usuarioLogadoOuNulo();
        if (logado == null) {
            throw new AcessoNegadoException("Nenhum usuário logado");
        }
        return logado;
    }

    private UsuarioLogado usuarioLogadoOuNulo() {
        return usuarioLogadoPort.atual().orElse(null);
    }

    // Usuários excluídos não aparecem na listagem; e só os da minha biblioteca.
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        Long bibliotecaId = getUsuarioLogado().bibliotecaId();
        return usuarioRepository.findByStatusNotAndBiblioteca_Id(EnumStatusUsuario.EXCLUIDO, bibliotecaId)
                .stream()
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

        if (usuarioRepository.existsByEmailAndIdNot(email, id)) {
            throw new ConflitoException("Já existe um usuário com este email");
        }

        usuario.setNome(request.nome());
        usuario.setEmail(email);

        if (request.cpf() != null && !request.cpf().isBlank()) {
            CPF cpf = new CPF(request.cpf());
            if (usuarioRepository.existsByCpf_CpfAndIdNot(cpf.getCpf(), id)) {
                throw new ConflitoException("Já existe um usuário com este CPF");
            }
            usuario.setCpf(cpf);
        }

        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public UsuarioResponse atualizarStatus(Long id, EnumStatusUsuario status) {
        Usuario usuario = buscarEntidade(id);
        usuario.setStatus(status);
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public UsuarioResponse atualizarPerfil(Long id, EnumPerfil perfil, String senhaEspecial) {
        if (perfil == EnumPerfil.ADMINISTRADOR && !senhaEspecialConfere(senhaEspecial)) {
            throw new AcessoNegadoException("Senha especial incorreta");
        }

        Usuario usuario = buscarEntidade(id);
        usuario.setPerfil(perfil);
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public void alterarSenha(Long id, AlterarSenhaRequest request) {
        Usuario usuario = buscarEntidade(id);

        if (!senhaPort.confere(request.senhaAtual(), usuario.getSenha())) {
            throw new ConflitoException("A senha atual está incorreta");
        }

        usuario.setSenha(senhaPort.codificar(request.novaSenha()));
    }

    // SOFT DELETE: o registro continua no banco com status EXCLUIDO (histórico de empréstimos).
    @Transactional
    public void excluir(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuario.setStatus(EnumStatusUsuario.EXCLUIDO);
    }

    // Escopo da biblioteca + EXCLUIDO = "não existe" (404), não 403: não confirma que o registro existe.
    private Usuario buscarEntidade(Long id) {
        return usuarioRepository.findByIdAndBiblioteca_Id(id, getUsuarioLogado().bibliotecaId())
                .filter(u -> u.getStatus() != EnumStatusUsuario.EXCLUIDO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }

    private boolean senhaEspecialConfere(String informada) {
        if (senhaPromocao == null || senhaPromocao.isBlank() || informada == null) {
            return false;
        }
        return MessageDigest.isEqual(
                senhaPromocao.getBytes(StandardCharsets.UTF_8),
                informada.getBytes(StandardCharsets.UTF_8));
    }

    private String normalizar(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}