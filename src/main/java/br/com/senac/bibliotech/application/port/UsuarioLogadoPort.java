package br.com.senac.bibliotech.application.port;

import br.com.senac.bibliotech.application.dto.UsuarioLogado;

import java.util.Optional;

public interface UsuarioLogadoPort {
    Optional<UsuarioLogado> atual();
}