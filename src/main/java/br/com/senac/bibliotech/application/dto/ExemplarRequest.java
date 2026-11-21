package br.com.senac.bibliotech.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record ExemplarRequest(

        @NotNull(message = "O livro é obrigatório")
        @Positive(message = "O id do livro deve ser positivo")
        Long livroId,

        @PastOrPresent(message = "A data de impressão não pode estar no futuro")
        LocalDate dataImpressao,

        @PastOrPresent(message = "A data de compra não pode estar no futuro")
        LocalDate dataCompra,

        // opcional
        Boolean capaDura
) {
}
