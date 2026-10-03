package br.com.senac.bibliotech.application.dto;

import br.com.senac.bibliotech.domain.entities.Usuario;
import br.com.senac.bibliotech.domain.enums.EnumPerfil;
import br.com.senac.bibliotech.domain.enums.EnumStatusUsuario;

/*
            DTO  DE SAIDA DE USUARIO

                isso ẃe para o que dicoi de fora - "Senha" nem o hash sai da api
                e o cpf só devlver se a tela realmente precisar
 */
public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        EnumPerfil perfil,
        EnumStatusUsuario status
) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),// pode mascarar email, cpf etc e utilizar a regra de negocio no dominio
                usuario.getStatus()
        );
    }

}
