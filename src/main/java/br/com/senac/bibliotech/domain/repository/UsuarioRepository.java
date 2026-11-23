package br.com.senac.bibliotech.domain.repository;

import br.com.senac.bibliotech.domain.entities.Usuario;
import br.com.senac.bibliotech.domain.enums.EnumStatusUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Login e unicidade são GLOBAIS de propósito: e-mail/CPF identificam a pessoa,
    // e no login ainda não existe biblioteca no contexto.
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByCpf_Cpf(String cpf);
    boolean existsByCpf_CpfAndIdNot(String cpf, Long id);

    // Operações de negócio: sempre dentro da biblioteca do token.
    List<Usuario> findByStatusNotAndBiblioteca_Id(EnumStatusUsuario status, Long bibliotecaId);
    Optional<Usuario> findByIdAndBiblioteca_Id(Long id, Long bibliotecaId);


}