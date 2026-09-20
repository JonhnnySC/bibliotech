package br.com.senac.bibliotech.dto;

import br.com.senac.bibliotech.enums.EnumStatusUsuario;
import jakarta.validation.constraints.NotNull;

/**
 * Corpo do PATCH /usuarios/{id}/status.
 * Igual ao seu, só com o @NotNull (que só tem efeito se o controller usar @Valid).
 */
public record AtualizarStatusRequest(

        @NotNull(message = "O status é obrigatório")
        EnumStatusUsuario status
) {
}
