package br.com.senac.bibliotech.domain.repository;

import br.com.senac.bibliotech.domain.entities.Biblioteca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BibliotecaRepository extends JpaRepository<Biblioteca, Long> {
    boolean existsByCnpj(String cnpj); // botei por que ve se ja existe uma biblioteca com o cnpj

}
