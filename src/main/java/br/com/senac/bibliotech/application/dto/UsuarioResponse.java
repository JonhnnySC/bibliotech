package br.com.senac.bibliotech.application.dto;

import br.com.senac.bibliotech.domain.entities.Usuario;
import br.com.senac.bibliotech.domain.enums.EnumPerfil;
import br.com.senac.bibliotech.domain.enums.EnumStatusUsuario;

/*
 * DTO de saida de usuario. Senha e hash NUNCA saem da API.
 * O cpf sai porque a tela de edicao precisa dele: sem o campo, o formulario
 * abre vazio e o PUT (que exige @NotBlank) devolve 400 ao salvar.
 */
public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String cpf,
        EnumPerfil perfil,
        EnumStatusUsuario status
) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCpf() == null ? null : usuario.getCpf().getCpf(),
                usuario.getPerfil(),
                usuario.getStatus()
        );
    }
}