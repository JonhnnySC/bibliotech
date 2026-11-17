package br.com.senac.bibliotech.application.dto;

import br.com.senac.bibliotech.domain.entities.Usuario;
import br.com.senac.bibliotech.domain.enums.EnumPerfil;

public record UsuarioLogadoResponse(
        Long id,
        String nome,
        String email, EnumPerfil perfil, Long bibliotecaID
) {
                                        //pq from?
    public static UsuarioLogadoResponse from (Usuario usuario) {
                                                        //pq null e ?
        Long bibliotecaId = usuario.getBiblioteca() == null ? null :
                usuario.getBiblioteca().getId();

        return new UsuarioLogadoResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                bibliotecaId);
    }
}
