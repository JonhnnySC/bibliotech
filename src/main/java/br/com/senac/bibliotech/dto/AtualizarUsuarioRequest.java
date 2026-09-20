package br.com.senac.bibliotech.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Corpo do PUT /usuarios/{id}: só os dados CADASTRAIS.
 *
 * Ficam de fora, de propósito: senha, perfil e status. Cada um tem endpoint próprio,
 * porque quem pode mudar cada coisa é diferente (o próprio usuário troca a senha;
 * só um administrador muda o perfil). Se o PUT aceitasse tudo, qualquer pessoa que
 * pudesse editar o próprio cadastro poderia se promover a administrador.
 */
public record AtualizarUsuarioRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 255)
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 255)
        String email,

        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve ter 11 dígitos, somente números")
        String cpf
) {
}
