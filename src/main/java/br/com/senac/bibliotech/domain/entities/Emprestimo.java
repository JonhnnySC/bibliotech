package br.com.senac.bibliotech.domain.entities;

import br.com.senac.bibliotech.domain.enums.EnumStatusEmprestimo;
import br.com.senac.bibliotech.domain.exception.ConflitoException;
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

    // Quem registrou o empréstimo: vem do usuário logado (token), nunca do front (Aula 3)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por_id")
    private Usuario registradoPor;

    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EnumStatusEmprestimo statusEmprestimo = EnumStatusEmprestimo.ATIVO;


    public static final int PRAZO_PADRAO_DIAS = 14;

    // REGRA DE NEGÓCIO no domínio: emprestar reserva o exemplar e define o prazo.
    public static Emprestimo registrar(Leitor leitor, Exemplar exemplar, Usuario registradoPor) {
        exemplar.marcarComoEmprestado(); // lança ConflitoException se não estiver disponível

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.leitor = leitor;
        emprestimo.exemplar = exemplar;
        emprestimo.registradoPor = registradoPor;
        emprestimo.dataEmprestimo = LocalDate.now();
        emprestimo.dataPrevistaDevolucao = LocalDate.now().plusDays(PRAZO_PADRAO_DIAS);
        emprestimo.statusEmprestimo = EnumStatusEmprestimo.ATIVO;
        return emprestimo;
    }

    // REGRA DE NEGÓCIO no domínio: devolver fecha o empréstimo e libera o exemplar.
    public void registrarDevolucao() {
        if (this.statusEmprestimo == EnumStatusEmprestimo.DEVOLVIDO) {
            throw new ConflitoException("Este emprestimo já foi devolvido");
        }
        this.statusEmprestimo = EnumStatusEmprestimo.DEVOLVIDO;
        this.dataDevolucao = LocalDate.now();
        this.exemplar.marcarComoDisponivel();
    }
}
