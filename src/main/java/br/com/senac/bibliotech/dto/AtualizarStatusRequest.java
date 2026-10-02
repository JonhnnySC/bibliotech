package br.com.senac.bibliotech.dto;

import br.com.senac.bibliotech.enums.EnumStatusUsuario;
import jakarta.validation.constraints.NotNull;


public record AtualizarStatusRequest(

        @NotNull(message = "O status é obrigatório")
        EnumStatusUsuario status
) {
}
