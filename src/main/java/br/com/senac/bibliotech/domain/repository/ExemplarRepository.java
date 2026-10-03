package br.com.senac.bibliotech.domain.repository;

import br.com.senac.bibliotech.domain.entities.Exemplar;
import br.com.senac.bibliotech.domain.enums.EnumStatusExemplar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExemplarRepository extends JpaRepository<Exemplar, Long> {
    List<Exemplar> findByStatus(EnumStatusExemplar statusExemplar);
    List<Exemplar> findByLivroId(Long livroId);

}
