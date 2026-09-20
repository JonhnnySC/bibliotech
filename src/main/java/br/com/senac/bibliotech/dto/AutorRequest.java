package br.com.senac.bibliotech.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/*
    Dto de entrada = define exatamente oque o cliente pode enviar

    MELHOR QUE CEBER A ENTIDADE AUTOR NO REQUESTBPDY

    - Por que o lciente pode mandar id e sobrescrever registros - chamam isso de mass assingment
    - O ciontrato da api ficaria preso ao formato de tabela = renomar uma coluna quebraria todo muyndo que consiome a api

    ELA É RECOR PÓR QUER É IMUTASAVEL E NÃO PRECISA DE GET E SET
    as validações são executadas quanto o controler usa @valid
 */
public record AutorRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
        String nome,

        @Size(max = 255 ,message = "A nacionalidade pode ter no maximo 255rcaracteres")
        String nacionalidade,

        // data de nascimento é opcional e se vier tem que estar no passado
        @Past(message = "A data de nascimento deve estar no passado")
        LocalDate dataNascimento
) {
}
