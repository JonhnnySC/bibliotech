package br.com.senac.bibliotech.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
 * Token gerado pelo servidor e SALVO no banco (Aula 3): na volta, além de conferir
 * a assinatura do JWT, conferimos se foi mesmo a gente que gerou este token.
 */
@Entity
@Table(name = "token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 1000)
    private String token;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false)
    private Usuario usuario;

    public Token(String token, Usuario usuario) {
        this.token = token;
        this.usuario = usuario;
        this.dataCadastro = LocalDateTime.now();
    }
}
