package br.com.senac.bibliotech.domain.repository;

import br.com.senac.bibliotech.domain.entities.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    List<Autor> findAllByBiblioteca_Id(Long bibliotecaId);
    Optional<Autor> findByIdAndBiblioteca_Id(Long id, Long bibliotecaId);
    boolean existsByIdAndBiblioteca_Id(Long id, Long bibliotecaId);
}