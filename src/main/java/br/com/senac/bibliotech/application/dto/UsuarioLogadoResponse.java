package br.com.senac.bibliotech.application.dto;

import br.com.senac.bibliotech.domain.entities.Usuario;
import br.com.senac.bibliotech.domain.enums.EnumPerfil;

/*
 * Dados PUBLICOS de quem esta logado: e o corpo do login e o que a store guarda.
 * Ficam fora CPF, senha e authorities.
 * O nome do campo importa: o Jackson serializa "bibliotecaId", e e esse nome
 * que o front (types/auth.ts e authSlice) espera.
 */
public record UsuarioLogadoResponse(
        Long id,
        String nome,
        String email,
        EnumPerfil perfil,
        Long bibliotecaId
) {
    public static UsuarioLogadoResponse from(Usuario usuario) {
        Long bibliotecaId = usuario.getBiblioteca() == null
                ? null
                : usuario.getBiblioteca().getId();

        return new UsuarioLogadoResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                bibliotecaId);
    }
}