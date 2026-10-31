package br.com.senac.bibliotech.domain.repository;

import br.com.senac.bibliotech.domain.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    // "buscar o token pelo token" (Aula 3): vazio = o servidor nunca gerou este token
    Optional<Token> findTokenByToken(String token);
}
