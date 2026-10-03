package br.com.senac.bibliotech.application.dto;

import br.com.senac.bibliotech.domain.entities.Exemplar;
import br.com.senac.bibliotech.domain.enums.EnumStatusExemplar;

import java.time.LocalDate;

public record ExemplarResponse(
        Long id,
        Long livroId,
        LocalDate dataImpressao,
        LocalDate dataCompra,
        Boolean capaDura,
        EnumStatusExemplar statusExemplar
) {
    public static ExemplarResponse from(Exemplar exemplar) {
        return new ExemplarResponse(
                exemplar.getId(),
                exemplar.getLivro().getId(),
                exemplar.getDataImpressao(),
                exemplar.getDataCompra(),
                exemplar.getCapaDura(),
                exemplar.getStatus()
        );
    }
}
