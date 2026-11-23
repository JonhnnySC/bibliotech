package br.com.senac.bibliotech.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/*
 * Os tamanhos batem com as colunas da entidade Livro.
 * isbn: @Column(length = 20) — aceitar 255 aqui deixava o INSERT estourar.
 */
public record LivroRequest(
        @NotBlank(message = "O volume é obrigatório")
        @Size(max = 255, message = "O volume deve ter no máximo 255 caracteres")
        String volume,

        @NotBlank(message = "O ISBN é obrigatório")
        @Size(max = 20, message = "O ISBN deve ter no máximo 20 caracteres")
        String isbn,

        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        String descricao,

        @Size(max = 255, message = "A edição deve ter no máximo 255 caracteres")
        String edicao,

        @Size(max = 255, message = "O tipo deve ter no máximo 255 caracteres")
        String tipo,

        @Positive(message = "O número de páginas deve ser maior que zero")
        Integer paginas,

        LocalDate dataLancamento,

        @Size(max = 500, message = "A URL da capa pode ter no máximo 500 caracteres")
        String capaUrl
) {
}