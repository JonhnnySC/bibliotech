package br.com.senac.bibliotech.domain.entities;

import br.com.senac.bibliotech.domain.enums.EnumPerfil;
import br.com.senac.bibliotech.domain.enums.EnumStatusUsuario;
import br.com.senac.bibliotech.domain.valueobject.CPF;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/*
 * Como no projeto do professor (Aula 3), o Usuario é um UserDetails: as autorizações
 * (roles) moram na entidade, em getAuthorities(), e o perfil vem SEMPRE do banco,
 * nunca de dentro do JWT.
 */
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)//
//@Data = Usuario com @Data gera equals e outras questões, melhor nao por motivos chatos de digitar aqui
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;

    // Value object: só existe CPF válido (a validação está no construtor do CPF)
    @Embedded
    private CPF cpf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EnumPerfil perfil = EnumPerfil.LEITOR;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EnumStatusUsuario status = EnumStatusUsuario.ATIVO;

    // Multibiblioteca (equivalente da empresa do professor). Nulo = acesso a todas.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "biblioteca_id", referencedColumnName = "id")
    private Biblioteca biblioteca;

    // Validação dentro da entidade (como o setEmail do professor)
    public void setEmail(String email) {
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Email Invalido!");
        }
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (perfil == null) {
            return List.of();
        }
        // hasRole("X") procura a authority "ROLE_X"
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
