package br.com.senac.bibliotech.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BibliotecaRequest(
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Pattern(regexp = "\\d{8}", message = "O cep deve ter 8 digitos") String cep,
        @Size(max = 20) String numero,
        @Size(max = 100) String compplemento

        // cnpj nao precisa pq o boottrap define, se o put pegasse o cnpj, uim adm muidaria oo id da prorpia bibliotexca
        // e nao tem outras info de endereço pq quem dá é o via cep


) {
}
