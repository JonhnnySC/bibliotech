package br.com.senac.bibliotech.dto;

import br.com.senac.bibliotech.enums.EnumStatusUsuario;

public record AtualizarStatusRequest(EnumStatusUsuario status) {
}
