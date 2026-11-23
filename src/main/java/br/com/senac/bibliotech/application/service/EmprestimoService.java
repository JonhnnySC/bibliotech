package br.com.senac.bibliotech.application.service;

import br.com.senac.bibliotech.application.dto.EmprestimoResponse;
import br.com.senac.bibliotech.domain.entities.Emprestimo;
import br.com.senac.bibliotech.domain.entities.Exemplar;
import br.com.senac.bibliotech.domain.entities.Leitor;
import br.com.senac.bibliotech.domain.entities.Usuario;
import br.com.senac.bibliotech.domain.enums.EnumStatusEmprestimo;
import br.com.senac.bibliotech.domain.exception.RecursoNaoEncontradoException;
import br.com.senac.bibliotech.domain.repository.BibliotecaRepository;
import br.com.senac.bibliotech.domain.repository.EmprestimoRepository;
import br.com.senac.bibliotech.domain.repository.ExemplarRepository;
import br.com.senac.bibliotech.domain.repository.LeitorRepository;
import br.com.senac.bibliotech.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 Service = ORQUESTRAÇÃO: busca NO ESCOPO, chama a entidade e salva.
 As regras (exemplar disponível, prazo, devolução) moram em Emprestimo e Exemplar.
*/
@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LeitorRepository leitorRepository;
    private final ExemplarRepository exemplarRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final BibliotecaRepository bibliotecaRepository;
    private final BibliotecaLogado bibliotecaLogado;

    @Transactional
    public EmprestimoResponse realizarEmprestimo(Long leitorId, Long exemplarId) {
        Long bibliotecaId = bibliotecaLogado.id();

        Leitor leitor = leitorRepository.findByIdAndBiblioteca_Id(leitorId, bibliotecaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Leitor", leitorId));

        Exemplar exemplar = exemplarRepository.findByIdAndLivro_Biblioteca_Id(exemplarId, bibliotecaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exemplar", exemplarId));

        // Quem registra é o usuário logado (token); o front não informa.
        Usuario registradoPor = usuarioRepository.getReferenceById(usuarioService.getUsuarioLogado().id());

        Emprestimo emprestimo = Emprestimo.registrar(leitor, exemplar, registradoPor);
        emprestimo.setBiblioteca(bibliotecaRepository.referencia(bibliotecaId)); // dono (NOT NULL)

        exemplarRepository.save(exemplar);
        return EmprestimoResponse.from(emprestimoRepository.save(emprestimo));
    }

    @Transactional
    public void devolverEmprestimo(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository
                .findByIdAndBiblioteca_Id(emprestimoId, bibliotecaLogado.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Emprestimo", emprestimoId));

        emprestimo.registrarDevolucao();

        emprestimoRepository.save(emprestimo);
        exemplarRepository.save(emprestimo.getExemplar());
    }

    @Transactional(readOnly = true)
    public List<EmprestimoResponse> listarEmprestimos() {
        return emprestimoRepository
                .findByStatusEmprestimoAndBibliotecaId(EnumStatusEmprestimo.ATIVO, bibliotecaLogado.id())
                .stream()
                .map(EmprestimoResponse::from)
                .toList();
    }
}