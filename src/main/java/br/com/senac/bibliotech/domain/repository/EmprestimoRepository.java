package br.com.senac.bibliotech.domain.repository;

import br.com.senac.bibliotech.domain.entities.Emprestimo;
import br.com.senac.bibliotech.domain.enums.EnumStatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    Optional<Emprestimo> findByIdAndBiblioteca_Id(Long id, Long bibliotecaId);

    // JOIN FETCH traz leitor, exemplar, livro e registrante na MESMA query (sem "no session")
    @Query("SELECT e FROM Emprestimo e " +
            "JOIN FETCH e.leitor " +
            "JOIN FETCH e.exemplar ex " +
            "LEFT JOIN FETCH ex.livro " +
            "LEFT JOIN FETCH e.registradoPor " +
            "WHERE e.statusEmprestimo = :statusEmprestimo " +
            "AND e.biblioteca.id = :bibliotecaId")
    List<Emprestimo> findByStatusEmprestimoAndBibliotecaId(
            @Param("statusEmprestimo") EnumStatusEmprestimo statusEmprestimo,
            @Param("bibliotecaId") Long bibliotecaId);
}