package br.com.senac.bibliotech.application.dto;

public record BootstrapResponse(
        Long bibliotecaId,
        String bibliotecaNome,
        Long usuarioId,
        String email
) {
}
