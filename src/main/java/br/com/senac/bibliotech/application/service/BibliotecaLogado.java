package br.com.senac.bibliotech.application.service;


import br.com.senac.bibliotech.application.dto.UsuarioLogado;
import br.com.senac.bibliotech.application.exception.AcessoNegadoException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

//serve apenas para doizer qal é a biblioteca de quekm esta logado, com isso a logica fica aqui
@Component
public class BibliotecaLogado {

    public Long id() {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UsuarioLogado logado
            && logado.bibliotecaId() != null) {
                return logado.bibliotecaId();
            }
            throw new AcessoNegadoException("Usuario sem biblioteca no contexto");
    }
}
