package br.com.senac.bibliotech.entities;

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
}
