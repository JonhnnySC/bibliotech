package br.com.senac.bibliotech.domain.entities;

import br.com.senac.bibliotech.domain.enums.EnumPerfil;
import br.com.senac.bibliotech.domain.enums.EnumStatusUsuario;
import br.com.senac.bibliotech.domain.valueObject.CPF;
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

    //OBRIGO MEU CONSTRUTOR A UILDAR UM CPF
}
