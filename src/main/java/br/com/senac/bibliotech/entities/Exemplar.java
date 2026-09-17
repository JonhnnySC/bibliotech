package br.com.senac.bibliotech.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Exemplar")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exemplar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_impressao")
    private LocalDate dataImpressão;
    private LocalDate dataCompra;
    private String quantidadeDisponivel;
    private Boolean capaDura;
}
