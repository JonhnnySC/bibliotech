package br.com.senac.bibliotech.infrastructure.persistence;

import br.com.senac.bibliotech.domain.entities.Biblioteca;
import br.com.senac.bibliotech.domain.repository.BibliotecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
class BibliotecaRepositoryAdapter implements BibliotecaRepository {

    private final BibliotecaJpaRepository jpa;

    @Override
    public Biblioteca save(Biblioteca biblioteca) {
        return jpa.save(biblioteca);
    }

    @Override
    public Optional<Biblioteca> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public boolean existsByCnpj(String cnpj) {
        return jpa.existsByCnpj(cnpj);
    }

    @Override
    public Biblioteca referencia(Long id) {
        return jpa.getReferenceById(id);
    }
}
