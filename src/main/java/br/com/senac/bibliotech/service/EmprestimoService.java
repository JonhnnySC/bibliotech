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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LeitorRepository leitorRepository;
    private final ExemplarRepository exemplarRepository;


    public EmprestimoService(EmprestimoRepository emprestimoRepository,
                             LeitorRepository leitorRepository,
                             ExemplarRepository exemplarRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.leitorRepository = leitorRepository;
        this.exemplarRepository = exemplarRepository;
    }

    /*
        por que usar transactional, que fazx com que as opreões de banco sejam únicas?
         - Fazer duas operacoes: salvar emprestimo e atualizar o status do livro
         - se o emprestimoo fort salvo mas o livro nao for marcado como indisponivel, da merda, dois caras com mesmo livro
         - se falhar, tudoo é desfeito
         */
    @Transactional
    public Emprestimo realizarEmprestimo(Long leitorId, Long exemplarId) {
        Leitor leitor = leitorRepository.findById(leitorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Leitor", leitorId));

        Exemplar exemplar = exemplarRepository.findById(exemplarId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exemplar", exemplarId));

        //preciso v erificar se o livro ta disponivel, mas ocmo eu faço isso?
        //r - mais simples do que eu pensava

        if (exemplar.getStatus() != EnumStatusExemplar.DISPONIVEL) {
            throw new ConflitoException("Este livro não está disponivel pra empresta");
        }

        //buildar emprestimo, é tipo, lego
        Emprestimo emprestimo = Emprestimo.builder()
                .leitor(leitor)
                .exemplar(exemplar)
                .dataEmprestimo(LocalDate.now())
                .dataDevolucao(LocalDate.now().plusDays(14))
                .statusEmprestimo(EnumStatusEmprestimo.ATIVO) //da pŕa usar assim tmb muito bom
                .build();


        //atualizxa o statuso do exemplar para emprestado
        exemplar.setStatus(EnumStatusExemplar.EMPRESTADO);
        exemplarRepository.save(exemplar);

        return emprestimoRepository.save(emprestimo);
    }

    //analise de se o emprestimo exziste e depois se foi devolviodo
    @Transactional
    public void devolverEmprestimo(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Este emprestimo não foi encontrado"));

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

    public List<Emprestimo> listarEmprestimos() {
        return emprestimoRepository.findByStatus(EnumStatusEmprestimo.ATIVO);
    }
}
