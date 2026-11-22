package br.com.senac.bibliotech.infrastructure.persistence;

import br.com.senac.bibliotech.domain.entities.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

interface LivroJpaRepository extends JpaRepository<Livro, Long> {
    Optional<Livro> findByIdAndBiblioteca_Id(Long id, Long bibliotecaId);
    List<Livro> findAllByBiblioteca_Id(Long bibliotecaId);
    List<Livro> findAllByIdInAndBiblioteca_Id(Collection<Long> ids, Long bibliotecaId);
    boolean existsByIdAndBiblioteca_Id(Long id, Long bibliotecaId);
    boolean existsByBiblioteca_IdAndIsbn(Long bibliotecaId, String isbn);
}
