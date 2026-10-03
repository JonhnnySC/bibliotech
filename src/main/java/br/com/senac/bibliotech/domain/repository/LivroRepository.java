package br.com.senac.bibliotech.domain.repository;

import br.com.senac.bibliotech.domain.entities.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {
}
