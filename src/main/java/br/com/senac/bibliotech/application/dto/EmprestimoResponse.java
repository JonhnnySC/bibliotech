package br.com.senac.bibliotech.application.dto;

import br.com.senac.bibliotech.domain.entities.Emprestimo;
import br.com.senac.bibliotech.domain.enums.EnumStatusEmprestimo;
import br.com.senac.bibliotech.domain.enums.EnumStatusExemplar;

import java.time.LocalDate;

/*
 * DTO de saída do empréstimo. Nenhuma entidade sai da API: o JSON da entidade
 * incluía o leitor inteiro (com o hash da senha). Aqui só sai o que a tela usa.
 */
public record EmprestimoResponse(
        Long id,
        UsuarioResponse leitor,
        ExemplarResumo exemplar,
        LocalDate dataEmprestimo,
        LocalDate dataPrevistaDevolucao,
        LocalDate dataDevolucao,
        EnumStatusEmprestimo statusEmprestimo,
        String registradoPor
) {

    public record ExemplarResumo(Long id, Long livroId, LivroResumo livro, EnumStatusExemplar statusExemplar) {
    }

    public record LivroResumo(Long id, String volume, String isbn, String capaUrl) {
    }

    public static EmprestimoResponse from(Emprestimo emprestimo) {
        var exemplar = emprestimo.getExemplar();
        var livro = exemplar.getLivro();

        return new EmprestimoResponse(
                emprestimo.getId(),
                UsuarioResponse.from(emprestimo.getLeitor()),
                new ExemplarResumo(
                        exemplar.getId(),
                        livro == null ? null : livro.getId(),
                        livro == null ? null
                                : new LivroResumo(livro.getId(), livro.getVolume(), livro.getIsbn(), livro.getCapaUrl()),
                        exemplar.getStatus()),
                emprestimo.getDataEmprestimo(),
                emprestimo.getDataPrevistaDevolucao(),
                emprestimo.getDataDevolucao(),
                emprestimo.getStatusEmprestimo(),
                emprestimo.getRegistradoPor() == null ? null : emprestimo.getRegistradoPor().getNome()
        );
    }
}
