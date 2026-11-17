package br.com.senac.bibliotech.application.dto;


import java.time.Instant;

/*
rESPOSTA DO LOGIN
    [
    tipo = bearwer, isso dis ao client como enviar o token nas chasmadas
    expira em deixa o feront saber quandfo pedir noovo login sem decodigfciar o jwt
    usuario sao os dados publiocos que o redux vai guardar
 */
public record LoginResponse(
        String token,
        String tipo,
        Instant expiraEm,
        UsuarioLogadoResponse usuario
) {
}

