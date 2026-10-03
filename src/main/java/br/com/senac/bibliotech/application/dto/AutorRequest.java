package br.com.senac.bibliotech.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record AutorRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
        String nome,

        @Size(max = 255, message = "A nacionalidade pode ter no máximo 255 caracteres")
        String nacionalidade,

        @Past(message = "A data de nascimento deve estar no passado")
        LocalDate dataNascimento,

        @Size(max = 500, message = "A URL da foto pode ter no máximo 500 caracteres")
        String fotoUrl,

        // opcional: se vier null, os vínculos atuais NÃO são alterados
        List<Long> livroIds
) {
}