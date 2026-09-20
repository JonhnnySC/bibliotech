package br.com.senac.bibliotech.dto;


import java.time.Instant;

/*
rESPOSTA DO LOGIN

"TIPO" = "BEARER" diaz ao cliente como eviar o token nas opróximas chamadas:
    Authorizxation: Bearer <token>

    "expiraEm" deixa o frontsaber quanto pedir um noovo login, sem precisar decodigficar o JWT
 */
public record LoginResponse(
        String token,
        String tipo,
        Instant expiraEm
) {
}
