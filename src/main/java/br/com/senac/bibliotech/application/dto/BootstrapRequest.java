package br.com.senac.bibliotech.application.dto;

import jakarta.validation.constraints.*;

public record BootstrapRequest(
        @NotBlank @Size(max = 150) String empresaNome,
        @NotBlank @Pattern(regexp = "\\d{14}", message = "CNPJ deve ter 14 dígitos") String empresaCnpj,
        @NotBlank @Size(max = 150) String adminNome,
        @NotBlank @Email String adminEmail,
        @NotBlank @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos") String adminCpf,
        @NotBlank @Size(min = 8, max = 72) String adminSenha) {}