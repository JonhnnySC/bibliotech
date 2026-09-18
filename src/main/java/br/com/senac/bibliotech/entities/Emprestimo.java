package br.com.senac.bibliotech.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "emprestimo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Emprestimo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // questão o emprestimo pŕecisa fazer arte com todoas as entidades
    //

}
