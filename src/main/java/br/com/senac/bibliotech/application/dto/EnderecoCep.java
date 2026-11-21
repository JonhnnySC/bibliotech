package br.com.senac.bibliotech.application.dto;

//

public record EnderecoCep(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf
) {
}
