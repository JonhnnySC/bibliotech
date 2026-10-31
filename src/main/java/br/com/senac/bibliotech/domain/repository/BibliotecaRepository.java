package br.com.senac.bibliotech.domain.repository;

import br.com.senac.bibliotech.domain.entities.Biblioteca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BibliotecaRepository extends JpaRepository<Biblioteca, Long> {
}
