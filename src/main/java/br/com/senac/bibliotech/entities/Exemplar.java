package br.com.senac.bibliotech.entities;

import br.com.senac.bibliotech.enums.EnumStatusExemplar;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "exemplar")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exemplar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(name = "data_impressao") //spriong ja converte dataImpressao para data_impressao
    private LocalDate dataImpressao;
    private LocalDate dataCompra;
    // private String quantidadeDisponivel; como cada exemplar é uma copia fisica então tem um status disponivel ou não.
    private Boolean capaDura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EnumStatusExemplar status = EnumStatusExemplar.DISPONIVEL;
}
