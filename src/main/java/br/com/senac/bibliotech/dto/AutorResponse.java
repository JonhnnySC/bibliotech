package br.com.senac.bibliotech.dto;

import br.com.senac.bibliotech.entities.Autor;

import java.time.LocalDate;

/*
DTO DE SAIDA: QUE A API DEVOLVE

Escolho o campo que sai, numa entidade com relacionamentos, isso evita loop infinito de JSON e a LazyInitializationExceptions

o metodo from () faz o mapeamento entidade -> DTO na maoo.

o ruim é que o codiggo é repetitivo, quando houver muitos DTOs, a biblioteca MapStruct gera

 PESQUISAR SOBRE MAPSTRUCT
 */
public record AutorResponse(

        Long id,
        String nome,
        String nacionalidade,
        LocalDate dataNascimento
) {

    public static AutorResponse from(Autor autor) {
        return new AutorResponse(
                autor.getId(),
                autor.getNome(),
                autor.getNacionalidade(),
                autor.getDataNascimento()

        );
    }
}
