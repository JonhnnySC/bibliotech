package br.com.senac.bibliotech.application.dto;

public record BibliotecaResponse(
        Long id,
        String nome,
        String cnpj,
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String uf
) {
}
