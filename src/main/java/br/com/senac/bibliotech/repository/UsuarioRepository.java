package br.com.senac.bibliotech.repository;


import br.com.senac.bibliotech.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepository  extends JpaRepository<Usuario, Long> {


}
