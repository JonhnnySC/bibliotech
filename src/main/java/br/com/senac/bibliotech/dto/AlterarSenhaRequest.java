package br.com.senac.bibliotech.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Corpo do PATCH /usuarios/{id}/senha.
 *
 * Exigimos a senha ATUAL: se alguém pegar uma sessão aberta (ou um token roubado),
 * não consegue trocar a senha e trancar o dono fora da conta.
 */
public record AlterarSenhaRequest(

        @NotBlank(message = "A senha atual é obrigatória")
        String senhaAtual,

        // max = 72: o BCrypt só considera os primeiros 72 bytes.
        @NotBlank(message = "A nova senha é obrigatória")
        @Size(min = 8, max = 72, message = "A nova senha deve ter entre 8 e 72 caracteres")
        String novaSenha
) {
}
