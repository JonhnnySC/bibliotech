package br.com.senac.bibliotech.application.service;

import br.com.senac.bibliotech.application.dto.BootstrapRequest;
import br.com.senac.bibliotech.application.dto.BootstrapResponse;
import br.com.senac.bibliotech.application.exception.AcessoNegadoException;
import br.com.senac.bibliotech.domain.entities.Biblioteca;
import br.com.senac.bibliotech.domain.entities.Usuario;
import br.com.senac.bibliotech.domain.enums.EnumPerfil;
import br.com.senac.bibliotech.domain.exception.ConflitoException;
import br.com.senac.bibliotech.domain.repository.BibliotecaRepository;
import br.com.senac.bibliotech.domain.repository.UsuarioRepository;
import br.com.senac.bibliotech.domain.valueObject.CPF;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class BootstrapService {

    private final BibliotecaRepository bibliotecaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    //por que?
    @Value("${app.bootstrap.key:}")
    private String chaveConfigurada;


//metodinho chave confere, que compara a chave informada com a chave configurada, usando MessageDigest.isEqual para evitar ataques de timing
    private boolean chaveConfere(String informada) {
        if (chaveConfigurada == null || chaveConfigurada.isBlank() || informada == null) {
            return false;
        }
        return MessageDigest.isEqual(
                informada.getBytes(StandardCharsets.UTF_8),
                chaveConfigurada.getBytes(StandardCharsets.UTF_8));
    }

    @Transactional
    public BootstrapResponse provisionar(String ChaveInformada, BootstrapRequest request) {
        if (!chaveConfere(ChaveInformada)) {
            throw new AcessoNegadoException("Chave de bootstrap inválida");
        }
        CPF cpf = new CPF(request.adminCpf());
        String email = request.adminEmail().trim().toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByEmail(email))
            throw new ConflitoException("Já existe um usuário com o email informado: " + email);
        if (usuarioRepository.existsByCpf_Cpf(cpf.getCpf()))
            throw new ConflitoException("Já existe um usuário com o CPF informado: " + cpf.getCpf());
        if (bibliotecaRepository.existsByCnpj(request.empresaCnpj()))
            throw new ConflitoException("Já existe uma biblioteca com o CNPJ informado: " + request.empresaCnpj());

        Biblioteca biblioteca = new Biblioteca();
        biblioteca.setNome(request.empresaNome().trim());
        biblioteca.setCnpj(request.empresaCnpj().trim());
        bibliotecaRepository.save(biblioteca);

        Usuario admin = Usuario.builder()
                .nome(request.adminNome().trim())
                .email(email)
                .cpf(cpf)
                .senha(passwordEncoder.encode(request.adminSenha()))
                .perfil(EnumPerfil.ADMINISTRADOR)   // tarra faltano por que senao o a pessoa  cadastrada nao teria acesso do adm no front
                .biblioteca(biblioteca)
                .build();
        usuarioRepository.save(admin);

        return new BootstrapResponse(biblioteca.getId(), biblioteca.getNome(), admin.getId(), admin.getEmail());
    }
}
