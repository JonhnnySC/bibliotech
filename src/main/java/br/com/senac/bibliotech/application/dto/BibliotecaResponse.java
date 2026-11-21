package br.com.senac.bibliotech.application.dto;

public record BibliotecaResponse(
        Long id, String nome, String cnpj, String email, String telefone, String cep, String logradouro, String bairro, String cidade, String uf
) {
}
