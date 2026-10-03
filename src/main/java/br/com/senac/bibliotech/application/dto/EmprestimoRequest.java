package br.com.senac.bibliotech.application.dto;


public record EmprestimoRequest(
        Long leitorId,
        Long exemplarId
) {
}
