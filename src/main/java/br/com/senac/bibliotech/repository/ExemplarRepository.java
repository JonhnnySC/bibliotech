package br.com.senac.bibliotech.repository;

import br.com.senac.bibliotech.entities.Emprestimo;
import br.com.senac.bibliotech.entities.Exemplar;
import br.com.senac.bibliotech.entities.Livro;
import br.com.senac.bibliotech.enums.EnumStatusEmprestimo;
import br.com.senac.bibliotech.enums.EnumStatusExemplar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExemplarRepository extends JpaRepository<Exemplar, Long> {
    List<Exemplar> findByStatus(EnumStatusExemplar statusExemplar);
    List<Exemplar> findByLivroId(Long livroId);

}
