package br.com.senac.bibliotech.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * MULTIBIBLIOTECA (equivalente da "Empresa" do projeto do professor).
 * Cada usuário pode pertencer a uma biblioteca; quem define essa ligação é só o
 * back-end (a partir do usuário logado), o front nunca informa o id.
 * Usuário SEM biblioteca enxerga tudo (mesma regra do professor com empresa_id nulo).
 */
@Entity
@Table(name = "biblioteca")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Biblioteca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String cnpj;
}
