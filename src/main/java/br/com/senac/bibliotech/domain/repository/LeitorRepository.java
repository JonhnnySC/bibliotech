package br.com.senac.bibliotech.domain.repository;

import br.com.senac.bibliotech.domain.entities.Leitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeitorRepository extends JpaRepository<Leitor, Long> {
    Optional<Leitor> findByIdAndBiblioteca_Id(Long id, Long bibliotecaId);
}