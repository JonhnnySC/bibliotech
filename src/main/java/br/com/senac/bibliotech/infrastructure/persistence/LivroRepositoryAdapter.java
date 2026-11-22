package br.com.senac.bibliotech.infrastructure.persistence;

import br.com.senac.bibliotech.domain.entities.Livro;
import br.com.senac.bibliotech.domain.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
class LivroRepositoryAdapter implements LivroRepository {

    private final LivroJpaRepository jpa;

    @Override
    public Livro save(Livro livro) {
        return jpa.save(livro);
    }

    @Override
    public Optional<Livro> findByIdAndBibliotecaId(Long id, Long bibliotecaId) {
        return jpa.findByIdAndBiblioteca_Id(id, bibliotecaId);
    }

    @Override
    public List<Livro> findAllByBibliotecaId(Long bibliotecaId) {
        return jpa.findAllByBiblioteca_Id(bibliotecaId);
    }

    @Override
    public List<Livro> findAllByIdInAndBibliotecaId(Collection<Long> ids, Long bibliotecaId) {
        return jpa.findAllByIdInAndBiblioteca_Id(ids, bibliotecaId);
    }

    @Override
    public boolean existsByIdAndBibliotecaId(Long id, Long bibliotecaId) {
        return jpa.existsByIdAndBiblioteca_Id(id, bibliotecaId);
    }

    @Override
    public boolean existsByBibliotecaIdAndIsbn(Long bibliotecaId, String isbn) {
        return jpa.existsByBiblioteca_IdAndIsbn(bibliotecaId, isbn);
    }

    @Override
    public void delete(Livro livro) {
        jpa.delete(livro);
    }
}
