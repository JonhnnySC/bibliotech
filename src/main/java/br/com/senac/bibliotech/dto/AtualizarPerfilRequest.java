package br.com.senac.bibliotech.dto;

import br.com.senac.bibliotech.enums.EnumPerfil;
import jakarta.validation.constraints.NotNull;

/**
 * Corpo do PATCH /usuarios/{id}/perfil (ex.: promover LEITOR a BIBLIOTECARIO).
 *
 * @NotNull: sem ele, um JSON vazio "{}" chegaria com perfil nulo e estouraria
 * no banco (coluna NOT NULL) como erro 500, em vez de um 400 claro.
 * Se o JSON trouxer um valor que não existe no enum ("SUPERUSER"), o Spring
 * já responde 400 sozinho.
 */
public record AtualizarPerfilRequest(

        @NotNull(message = "O perfil é obrigatório")
        EnumPerfil perfil
) {
}
