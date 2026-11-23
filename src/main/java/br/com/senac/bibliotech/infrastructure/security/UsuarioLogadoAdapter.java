package br.com.senac.bibliotech.infrastructure.security;

import br.com.senac.bibliotech.application.dto.UsuarioLogado;
import br.com.senac.bibliotech.application.port.UsuarioLogadoPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsuarioLogadoAdapter implements UsuarioLogadoPort {

    @Override
    public Optional<UsuarioLogado> atual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // o JwtFilter coloca o RECORD UsuarioLogado como principal (não a entidade Usuario)
        if (auth != null && auth.getPrincipal() instanceof UsuarioLogado logado) {
            return Optional.of(logado);
        }
        return Optional.empty();
    }
}