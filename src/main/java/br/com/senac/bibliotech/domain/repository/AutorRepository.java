package br.com.senac.bibliotech.domain.repository;

import br.com.senac.bibliotech.domain.entities.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository<Autor, Long> {
}
