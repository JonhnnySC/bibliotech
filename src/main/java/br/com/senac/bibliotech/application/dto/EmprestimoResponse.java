package br.com.senac.bibliotech.application.dto;

import br.com.senac.bibliotech.domain.enums.EnumStatusEmprestimo;

import java.time.LocalDate;

public record EmprestimoResponse(
        Long id,
        Long leitorId,
        Long exemplarId,
        LocalDate dataEmprestimo,
        LocalDate dataPrevistaDevolucao,
        EnumStatusEmprestimo statusEmprestimo
) {
}
