package br.com.senac.bibliotech.application.dto;

import br.com.senac.bibliotech.domain.entities.Autor;
import br.com.senac.bibliotech.domain.entities.Livro;

import java.time.LocalDate;
import java.util.List;

public record AutorResponse(
        Long id,
        String nome,
        String nacionalidade,
        LocalDate dataNascimento,
        String fotoUrl,
        List<LivroResumo> livros
) {

    public record LivroResumo(Long id, String volume, String isbn) {
        static LivroResumo from(Livro l) {
            return new LivroResumo(l.getId(), l.getVolume(), l.getIsbn());
        }
    }

    public static AutorResponse from(Autor autor) {
        return new AutorResponse(
                autor.getId(),
                autor.getNome(),
                autor.getNacionalidade(),
                autor.getDataNascimento(),
                autor.getFotoUrl(),
                autor.getLivros().stream().map(LivroResumo::from).toList()
        );
    }
}