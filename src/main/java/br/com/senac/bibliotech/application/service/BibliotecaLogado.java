package br.com.senac.bibliotech.application.service;

import br.com.senac.bibliotech.application.dto.UsuarioLogado;
import br.com.senac.bibliotech.application.exception.AcessoNegadoException;
import br.com.senac.bibliotech.application.port.UsuarioLogadoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/* Escopo do tenant: único ponto que responde "de qual biblioteca é quem está logado". */
@Component
@RequiredArgsConstructor
public class BibliotecaLogado {

    private final UsuarioLogadoPort usuarioLogado;

    public Long id() {
        return usuarioLogado.atual()
                .map(UsuarioLogado::bibliotecaId)   // vazio se não houver biblioteca
                .orElseThrow(() -> new AcessoNegadoException("Usuário sem biblioteca no contexto"));
    }
}