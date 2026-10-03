package br.com.senac.bibliotech.application.dto;

import br.com.senac.bibliotech.domain.enums.EnumPerfil;
import jakarta.validation.constraints.NotNull;

public record AtualizarPerfilRequest(
        @NotNull(message = "O perfil é obrigatório")
        EnumPerfil perfil,

        String senhaEspecial
) {
}