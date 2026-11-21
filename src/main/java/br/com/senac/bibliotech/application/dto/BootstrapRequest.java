package br.com.senac.bibliotech.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BootstrapRequest(

        @NotBlank(message = "O nome da empresa é obrigatório")
        @Size(max = 150, message = "O nome da empresa deve ter no máximo 150 caracteres")
        String empresaNome,

        @NotBlank(message = "O CNPJ é obrigatório")
        @Pattern(regexp = "\\d{14}", message = "O CNPJ deve ter 14 dígitos, somente números")
        String empresaCnpj,

        @NotBlank(message = "O nome do administrador é obrigatório")
        @Size(max = 150, message = "O nome do administrador deve ter no máximo 150 caracteres")
        String adminNome,

        @NotBlank(message = "O email do administrador é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 255, message = "O email deve ter no máximo 255 caracteres")
        String adminEmail,

        @NotBlank(message = "O CPF do administrador é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve ter 11 dígitos, somente números")
        String adminCpf,

        @NotBlank(message = "A senha do administrador é obrigatória")
        @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres")
        String adminSenha) {
}
