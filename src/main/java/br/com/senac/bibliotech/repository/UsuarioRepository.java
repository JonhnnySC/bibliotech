package br.com.senac.bibliotech.repository;

import br.com.senac.bibliotech.entities.Usuario;
import br.com.senac.bibliotech.enums.EnumStatusUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsUsuarioByEmailAndSenha(String email, String senha);

    Optional<List<Usuario>> findByStatusNot(EnumStatusUsuario status);
}
