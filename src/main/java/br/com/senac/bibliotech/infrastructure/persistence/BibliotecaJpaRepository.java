package br.com.senac.bibliotech.infrastructure.persistence;

import br.com.senac.bibliotech.domain.entities.Biblioteca;
import org.springframework.data.jpa.repository.JpaRepository;

interface BibliotecaJpaRepository extends JpaRepository<Biblioteca, Long> {
    boolean existsByCnpj(String cnpj);
}
