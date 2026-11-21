package br.com.senac.bibliotech.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EmprestimoRequest(

        @NotNull(message = "O leitor é obrigatório")
        @Positive(message = "O id do leitor deve ser positivo")
        Long leitorId,

        @NotNull(message = "O exemplar é obrigatório")
        @Positive(message = "O id do exemplar deve ser positivo")
        Long exemplarId
) {
}
