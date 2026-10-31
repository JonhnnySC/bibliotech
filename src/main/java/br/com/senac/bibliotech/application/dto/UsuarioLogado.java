package br.com.senac.bibliotech.application.dto;

import br.com.senac.bibliotech.domain.entities.Usuario;
import br.com.senac.bibliotech.domain.valueObject.CPF;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/*
 * Quem está logado (Aula 3). É o "principal" do SecurityContext: o front nunca
 * informa quem está agindo, o back descobre pelo token.
 */
public record UsuarioLogado(Long id,
                            String email,
                            CPF cpf,
                            Long bibliotecaId,
                            Collection<? extends GrantedAuthority> authorities) {

    public UsuarioLogado(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getCpf(),
                usuario.getBiblioteca() != null ? usuario.getBiblioteca().getId() : null,
                usuario.getAuthorities()
        );
    }
}
