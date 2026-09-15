package br.com.senac.bibliotech.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Editora")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Editora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String nacionalidade;
}
