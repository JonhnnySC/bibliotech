package br.com.senac.bibliotech.domain.repository;

import br.com.senac.bibliotech.domain.entities.Livro;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LivroRepository {
    Livro save(Livro livro);
    Optional<Livro> findByIdAndBibliotecaId(Long id, Long bibliotecaId);
    List<Livro> findAllByBibliotecaId(Long bibliotecaId);
    List<Livro> findAllByIdInAndBibliotecaId(Collection<Long> ids, Long bibliotecaId);
    boolean existsByIdAndBibliotecaId(Long id, Long bibliotecaId);
    boolean existsByBibliotecaIdAndIsbn(Long bibliotecaId, String isbn);
    void delete(Livro livro);
}
