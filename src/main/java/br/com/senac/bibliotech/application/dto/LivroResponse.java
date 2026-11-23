package br.com.senac.bibliotech.application.dto;

import br.com.senac.bibliotech.domain.entities.Livro;

import java.time.LocalDate;

/*
 * capaUrl precisa sair: o front usa a capa gravada no banco como fallback
 * quando o ISBN nao resolve no Open Library / Google Books.
 */
public record LivroResponse(
        Long id,
        String volume,
        String isbn,
        String descricao,
        String edicao,
        String tipo,
        Integer paginas,
        LocalDate dataLancamento,
        String capaUrl
) {
    public static LivroResponse from(Livro livro) {
        return new LivroResponse(
                livro.getId(),
                livro.getVolume(),
                livro.getIsbn(),
                livro.getDescricao(),
                livro.getEdicao(),
                livro.getTipo(),
                livro.getPaginas(),
                livro.getDataLancamento(),
                livro.getCapaUrl()
        );
    }
}