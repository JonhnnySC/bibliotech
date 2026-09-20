package br.com.senac.bibliotech.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

/*
        Dto de entrada para cadastra rusuariio

        nao tem perfuil, status e id = por que se o cliente pudesse enviar perfi, qualquer pessoa se cadastrartia como
        outra coisa com admin, quem decide O PERFIL É O SERVIDOR
 */
public record UsuarioRequest(

    @NotBlank(message = "O nome é obrigatorio")
    @Size(max = 255)
    String nome,

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 255)
    String email,

    @NotBlank(message = "Senha obrigatorio")
    @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres")
    String senha,

    @NotBlank(message = "O cpf é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve ter 11 dígitos, somente númertos")
    String cpf
){
}
