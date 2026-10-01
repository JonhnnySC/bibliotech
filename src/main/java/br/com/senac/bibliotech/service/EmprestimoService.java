package br.com.senac.bibliotech.service;

import br.com.senac.bibliotech.entities.Emprestimo;
import br.com.senac.bibliotech.entities.Exemplar;
import br.com.senac.bibliotech.entities.Leitor;
import br.com.senac.bibliotech.entities.Livro;
import br.com.senac.bibliotech.enums.EnumStatusEmprestimo;
import br.com.senac.bibliotech.enums.EnumStatusExemplar;
import br.com.senac.bibliotech.exception.ConflitoException;
import br.com.senac.bibliotech.exception.RecursoNaoEncontradoException;
import br.com.senac.bibliotech.repository.EmprestimoRepository;
import br.com.senac.bibliotech.repository.ExemplarRepository;
import br.com.senac.bibliotech.repository.LeitorRepository;
import br.com.senac.bibliotech.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor // aparente que é bom pra gerar construtor com todos os campos final
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LeitorRepository leitorRepository;
    private final ExemplarRepository exemplarRepository;

    @Transactional
    public Emprestimo realizarEmprestimo(Long leitorId, Long exemplarId) {
        Leitor leitor = leitorRepository.findById(leitorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Leitor " + leitorId + " não encontrado"));
        Exemplar exemplar = exemplarRepository.findById(exemplarId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exemplar " + exemplarId + " não encontrado"));

        if (exemplar.getStatus() != EnumStatusExemplar.DISPONIVEL) {
            throw new ConflitoException("Este exemplar não está disponível");
        }

        Emprestimo emprestimo = Emprestimo.builder()
                .leitor(leitor)
                .exemplar(exemplar)
                .dataEmprestimo(LocalDate.now())
                .dataPrevistaDevolucao(LocalDate.now().plusDays(14)) // ✅ nunca dataDevolucao aqui
                .statusEmprestimo(EnumStatusEmprestimo.ATIVO)
                .build();

        exemplar.setStatus(EnumStatusExemplar.EMPRESTADO);
        exemplarRepository.save(exemplar);
        Emprestimo salvo = emprestimoRepository.save(emprestimo);

        salvo.getExemplar().getLivro(); // ✅ inicializa o proxy lazy dentro da transação
        return salvo;
    }

    //analise de se o emprestimo exziste e depois se foi devolviodo, se devolvido ok
    @Transactional
    public void devolverEmprestimo(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Emprestimo: " + emprestimoId + " não encontrado"));

        if (emprestimo.getStatusEmprestimo() == EnumStatusEmprestimo.DEVOLVIDO) {
            throw new ConflitoException("Este emprestimo já foi devolvido");
        }

        //atualização de emprestimo e liberação do negocio denvoc
        emprestimo.setStatusEmprestimo(EnumStatusEmprestimo.DEVOLVIDO);
        emprestimo.setDataDevolucao(LocalDate.now());
        emprestimoRepository.save(emprestimo);

        Exemplar exemplar = emprestimo.getExemplar();
        exemplar.setStatus(EnumStatusExemplar.DISPONIVEL);
        exemplarRepository.save(exemplar);
    }
    @Transactional(readOnly = true)
    public List<Emprestimo> listarEmprestimos() {
        return emprestimoRepository.findByStatusEmprestimo(EnumStatusEmprestimo.ATIVO);
    }
}
