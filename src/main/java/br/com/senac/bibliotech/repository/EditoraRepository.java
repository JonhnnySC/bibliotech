package br.com.senac.bibliotech.repository;

import br.com.senac.bibliotech.entities.Editora;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EditoraRepository extends JpaRepository<Editora, Long> {
}
