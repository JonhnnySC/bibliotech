package br.com.senac.bibliotech.repository;

import br.com.senac.bibliotech.entities.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {
}
