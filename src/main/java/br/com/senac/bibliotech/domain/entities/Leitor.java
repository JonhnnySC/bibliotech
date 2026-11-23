package br.com.senac.bibliotech.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/*
 * Heranca JOINED: usuario guarda os dados comuns, leitor so o que e do leitor.
 * perfil e status NAO sao redeclarados aqui — ja existem em Usuario.
 * Redecalhar criava coluna duplicada nas duas tabelas: o builder preenchia a do
 * filho e o setPerfil() a do pai, e a coluna de leitor ficava sempre LEITOR.
 *
 * @SuperBuilder nos dois lados: o builder do filho enxerga os campos herdados.
 */
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Leitor extends Usuario {

    private String nacionalidade;

    // Lado inverso de Emprestimo.leitor. Nao cria coluna; quem manda e o @ManyToOne.
    @OneToMany(mappedBy = "leitor")
    @Builder.Default
    private List<Emprestimo> emprestimos = new ArrayList<>();
}