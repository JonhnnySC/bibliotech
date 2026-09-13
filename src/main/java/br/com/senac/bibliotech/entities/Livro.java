package br.com.senac.bibliotech.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
//n usar @data por que inclui to strin e equaisl and has code, numa entidade que é relacionada bidericenialmente
//optei por colocar livro tendo yuma lista exemplar, e coimo so tem UMA referencia a livro, elas são bidirecionais = o toString() gerado pega os dois lados infinitamente, evitar @data
@Entity
@Table(name = "Livro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String volume;
    private String isbn;
    private String descricao;
    private String edicao;
    private String tipo; // kindle? epub?
    private boolean capaDura; //capa dura ou mole?
    private Integer paginas;
    private LocalDate dataLancamento;

    //String autor -- um autor pode ter vários livros
    //String editora -- uma editora pode ter vários livros

    /*Usar ENUM para genero, curso, categoria. por que o valores é fixo e conhecido, ficçao, romance etc, mas há muitos de cada um e vai ser necessario colocar mais
    ou uma entidade é melhor? tenho que ver, a cadela tá com bafo de carniça
    --tipo também como enum - fisico, ebook, audio book, e o tipo pŕecisa ser herança do ebook?????
    */

    /* Integer QUANTIDADE?? - Não se consegue saber quais exemplares estão emprestados, danificados, perdidos ou disponíveis
     não se sabe que tem 5 quebrando o sistema de biblioteca em controlar exemplares individualmente, substituir por
      String Exemplar*/

    //private LocalDate dataFabricacao;  melhor pertencer a entidade exemplar - por que exe plares diferentes do mesmo livro podem ter sido impressos em datas diferentes,
    //private LocalDate dataCompra;  mesma loogica do acima
}
