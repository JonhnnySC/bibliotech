package br.com.senac.bibliotech.entities;

import br.com.senac.bibliotech.enums.EnumGeneroLiterario;
import br.com.senac.bibliotech.enums.EnumPerfil;
import br.com.senac.bibliotech.enums.EnumStatusUsuario;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)//
//@Data = Usuario com @Data gera equals e outras questões, melhor nao por motivos chatos de digitar aqui
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;
    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EnumPerfil perfil = EnumPerfil.LEITOR;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EnumStatusUsuario status = EnumStatusUsuario.ATIVO;
}
