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

    // biblioteca nasce no bootstrap só com nome e CNPJ

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    //por quer collum? o hibernate ve o tamanho da coluna, com ddl update, esses campos sao ccolocados na subida serm apagar

    @Column(length = 8)
    private String cep;
    @Column(length = 150)
    private String logradouro;
    @Column(length = 20)
    private String numero;
    @Column(length = 100)
    private String complemento;
    @Column(length = 100)
    private String bairro;
    @Column(length = 100)
    private String cidade;
    @Column(length = 2)
    private String uf;





}
