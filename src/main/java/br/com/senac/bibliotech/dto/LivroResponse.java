package br.com.senac.bibliotech.dto;

import br.com.senac.bibliotech.entities.Livro;

import java.time.LocalDate;

public record LivroResponse(
        Long id,
        String volume,
        String isbn,
        String descricao,
        String edicao,
        String tipo,
        Integer paginas,
        LocalDate dataLancamento
) {
    public static LivroResponse from (Livro livro) {
        return new LivroResponse(
                livro.getId(), livro.getVolume(), livro.getIsbn(),
                livro.getDescricao(), livro.getEdicao(), livro.getTipo(), livro.getPaginas(),
                livro.getDataLancamento()
        );
    }
}
