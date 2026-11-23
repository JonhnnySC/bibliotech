package br.com.senac.bibliotech.infrastructure.security;

import br.com.senac.bibliotech.application.exception.AcessoNegadoException;
import br.com.senac.bibliotech.application.port.SenhaPort;
import br.com.senac.bibliotech.application.port.UsuarioLogadoPort;
import br.com.senac.bibliotech.domain.entities.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioLogadoAdapter implements UsuarioLogadoPort {

    @Override
    public Usuario usuario() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Usuario u) return u;
        throw new AcessoNegadoException("Nenhum usuário autenticado");
    }

    @Override
    public Long bibliotecaId() {
        Usuario u = usuario();
        if (u.getBiblioteca() == null) throw new AcessoNegadoException("Usuário sem biblioteca");
        return u.getBiblioteca().getId();
    }
}
