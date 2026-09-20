package br.com.senac.bibliotech.repository;

import br.com.senac.bibliotech.entities.Exemplar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExemplarRepository extends JpaRepository<Exemplar, Long> {
}
