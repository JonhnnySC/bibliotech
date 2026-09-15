package br.com.senac.bibliotech.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "Leitor")
@Getter
@Setter
@SuperBuilder //aparentemente isso é bom pra quando tem herança
/* ---explicacao---
o @Builder normal do Lombok não lida bem com superclasses.
Se "Leitor extends Usuario" e você usa @Builder comum em ambos,
o builder gerado pra Leitor só enxerga os campos da própria classe
(id, nome, etc.) — ele não sabe construir os campos herdados de Usuario
(tipo email).
 */
@NoArgsConstructor
@AllArgsConstructor
public class Leitor extends Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String  nacionalidade;
    // e o historico de livros pegos, e tals, onde fica?
    //r- por que leitor é 1-n com emprestimos
}
