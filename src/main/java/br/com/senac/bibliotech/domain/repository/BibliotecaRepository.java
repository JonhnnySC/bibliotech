package br.com.senac.bibliotech.domain.repository;

import br.com.senac.bibliotech.domain.entities.Biblioteca;

import java.util.List;
import java.util.Optional;

public interface BibliotecaRepository {
    Biblioteca save(Biblioteca biblioteca);
    Optional<Biblioteca> findById(Long id);
    boolean existsByCnpj(String cnpj);
    Biblioteca referencia(Long id);
    //apenas pra registro de pessoa leitor
    List<Biblioteca> findAll();
}
