package br.com.senac.bibliotech.domain.repository;

import br.com.senac.bibliotech.domain.entities.Editora;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EditoraRepository extends JpaRepository<Editora, Long> {
}
