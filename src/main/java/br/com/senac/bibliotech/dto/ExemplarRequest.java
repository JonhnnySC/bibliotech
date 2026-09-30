package br.com.senac.bibliotech.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ExemplarRequest(
        @NotNull Long livroId,
        LocalDate dataImpressao,
        LocalDate dataCompra,
        Boolean capaDura
) {
}
