package br.com.senac.bibliotech.application.port;

import br.com.senac.bibliotech.application.dto.UsuarioLogado;
import br.com.senac.bibliotech.domain.entities.Usuario;

import java.time.Instant;

public interface TokenPort {
    TokenEmitido emitir(Usuario usuario);
    UsuarioLogado verificar(String token);
    void revogar(String token);
    void revogarTodos();

    record TokenEmitido(String valor, Instant expiraEm) {}
}