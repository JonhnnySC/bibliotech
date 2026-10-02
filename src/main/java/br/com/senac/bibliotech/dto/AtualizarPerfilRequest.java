package br.com.senac.bibliotech.dto;

import br.com.senac.bibliotech.enums.EnumPerfil;
import jakarta.validation.constraints.NotNull;

public record AtualizarPerfilRequest(
        @NotNull(message = "O perfil é obrigatório")
        EnumPerfil perfil,

        String senhaEspecial
) {
}