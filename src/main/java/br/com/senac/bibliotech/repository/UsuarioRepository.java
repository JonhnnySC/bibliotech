package br.com.senac.bibliotech.repository;

import br.com.senac.bibliotech.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
