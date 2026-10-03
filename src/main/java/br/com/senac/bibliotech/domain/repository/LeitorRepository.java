package br.com.senac.bibliotech.domain.repository;

import br.com.senac.bibliotech.domain.entities.Leitor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeitorRepository extends JpaRepository<Leitor, Long> {
}
