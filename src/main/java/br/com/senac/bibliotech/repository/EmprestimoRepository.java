package br.com.senac.bibliotech.repository;

import br.com.senac.bibliotech.entities.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
}
