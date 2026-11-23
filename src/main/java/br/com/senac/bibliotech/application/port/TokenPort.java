package br.com.senac.bibliotech.application.port;

import br.com.senac.bibliotech.domain.entities.Usuario;

import java.time.Instant;

// seguranças portas
public interface TokenPort {
    TokenEmitido emitir (Usuario usuario);
    void revogar(String token);
    void revogarTodos();

    record TokenEmitido(String valor, Instant expiraEm) {}
}
