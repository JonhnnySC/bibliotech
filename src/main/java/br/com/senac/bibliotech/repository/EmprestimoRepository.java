package br.com.senac.bibliotech.repository;

import br.com.senac.bibliotech.entities.Emprestimo;
import br.com.senac.bibliotech.enums.EnumStatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    //precio adicioonar para buscar os emprestimos que estao com algum status, como no enum, para saber se ta atiovo, desativo etrc

    List<Emprestimo> findByStatusEmprestimo(EnumStatusEmprestimo statusEmprestimo);
}
