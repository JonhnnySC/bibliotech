package br.com.senac.bibliotech.repository;

import br.com.senac.bibliotech.entities.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository<Autor, Long> {
}
