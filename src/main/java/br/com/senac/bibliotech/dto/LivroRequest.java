package br.com.senac.bibliotech.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record LivroRequest(
        @NotBlank String volume,
        @NotBlank String isbn,
        String descricao,
        String edicao,
        String tipo,
        Integer paginas,
        LocalDate dataLancamento,
        String capaUrl
) {
}