package br.com.senac.bibliotech.repository;

import br.com.senac.bibliotech.entities.Leitor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeitorRepository extends JpaRepository<Leitor, Long> {
}
