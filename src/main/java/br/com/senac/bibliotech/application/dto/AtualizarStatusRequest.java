package br.com.senac.bibliotech.application.dto;

import br.com.senac.bibliotech.domain.enums.EnumStatusUsuario;
import jakarta.validation.constraints.NotNull;


public record AtualizarStatusRequest(

        @NotNull(message = "O status é obrigatório")
        EnumStatusUsuario status
) {
}
