package br.com.senac.bibliotech.dto;


public record EmprestimoRequest(
        Long leitorId,
        Long exemplarId
) {
}
