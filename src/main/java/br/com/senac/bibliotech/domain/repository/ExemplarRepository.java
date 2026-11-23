package br.com.senac.bibliotech.domain.repository;

import br.com.senac.bibliotech.domain.entities.Exemplar;
import br.com.senac.bibliotech.domain.enums.EnumStatusExemplar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExemplarRepository extends JpaRepository<Exemplar, Long> {
    List<Exemplar> findByStatus(EnumStatusExemplar statusExemplar);
    List<Exemplar> findByLivroId(Long livroId);

    // o exemplar herda a biblioteca pelo livro
    Optional<Exemplar> findByIdAndLivro_Biblioteca_Id(Long id, Long bibliotecaId);
    List<Exemplar> findByLivroIdAndLivro_Biblioteca_Id(Long livroId, Long bibliotecaId);
}