package br.com.senac.bibliotech.domain.repository;

import br.com.senac.bibliotech.domain.entities.Emprestimo;
import br.com.senac.bibliotech.domain.enums.EnumStatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    //precio adicioonar para buscar os emprestimos que estao com algum status, como no enum, para saber se ta atiovo, desativo etrc


    // O JOIN FETCH obriga o Hibernate a trazer o Leitor, o Exemplar e o Livro
    // na MESMA query, evitando o erro de "no session"
    //FIZ ISSO POR QUE TA DANDO ERTRRADO
    @Query("SELECT e FROM Emprestimo e " +
            "JOIN FETCH e.leitor " +
            "JOIN FETCH e.exemplar ex " +
            "LEFT JOIN FETCH ex.livro " +
            "WHERE e.statusEmprestimo = :statusEmprestimo")

    List<Emprestimo> findByStatusEmprestimo(EnumStatusEmprestimo statusEmprestimo);


}
