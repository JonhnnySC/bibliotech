package br.com.senac.bibliotech.application.port;

import br.com.senac.bibliotech.domain.entities.Usuario;

public interface UsuarioLogadoPort {
    Usuario usuario();
    Long bibliotecaId();
}
