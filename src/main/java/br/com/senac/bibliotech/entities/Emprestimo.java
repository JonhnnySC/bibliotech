package br.com.senac.bibliotech.entities;

import br.com.senac.bibliotech.enums.EnumStatusEmprestimo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @ManyToOne(fetch = FetchType.LAZY) //muitos pra um por que um leitor poxder ter muitos emprestimos
    @JoinColumn(name = "leitor_id", nullable = false) // nomear pra que no service consiga achar
    private Leitor leitor;  //relacionamento necessario por que sim

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exemplar_id", nullable = false)
    private Exemplar exemplar;

    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EnumStatusEmprestimo statusEmprestimo = EnumStatusEmprestimo.ATIVO;

}
