package br.com.senac.bibliotech.application.service;

import br.com.senac.bibliotech.application.dto.EmprestimoResponse;
import br.com.senac.bibliotech.domain.entities.Emprestimo;
import br.com.senac.bibliotech.domain.entities.Exemplar;
import br.com.senac.bibliotech.domain.entities.Leitor;
import br.com.senac.bibliotech.domain.entities.Usuario;
import br.com.senac.bibliotech.domain.enums.EnumStatusEmprestimo;
import br.com.senac.bibliotech.domain.exception.RecursoNaoEncontradoException;
import br.com.senac.bibliotech.domain.repository.EmprestimoRepository;
import br.com.senac.bibliotech.domain.repository.ExemplarRepository;
import br.com.senac.bibliotech.domain.repository.LeitorRepository;
import br.com.senac.bibliotech.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Service = ORQUESTRAÇÃO (Aula 15.10): busca, chama a entidade e salva.
 * As regras (exemplar disponível, prazo, devolução) moram em Emprestimo e Exemplar.
 */
@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LeitorRepository leitorRepository;
    private final ExemplarRepository exemplarRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    @Transactional
    public EmprestimoResponse realizarEmprestimo(Long leitorId, Long exemplarId) {
        Leitor leitor = leitorRepository.findById(leitorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Leitor " + leitorId + " não encontrado"));
        Exemplar exemplar = exemplarRepository.findById(exemplarId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exemplar " + exemplarId + " não encontrado"));

        // Quem registra é o usuário logado (token), o front não informa (Aula 3)
        Usuario registradoPor = usuarioRepository.getReferenceById(usuarioService.getUsuarioLogado().id());

        Emprestimo emprestimo = Emprestimo.registrar(leitor, exemplar, registradoPor);

        exemplarRepository.save(exemplar);
        return EmprestimoResponse.from(emprestimoRepository.save(emprestimo));
    }

    @Transactional
    public void devolverEmprestimo(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Emprestimo: " + emprestimoId + " não encontrado"));

        emprestimo.registrarDevolucao();

        emprestimoRepository.save(emprestimo);
        exemplarRepository.save(emprestimo.getExemplar());
    }

    @Transactional(readOnly = true)
    public List<EmprestimoResponse> listarEmprestimos() {
        return emprestimoRepository.findByStatusEmprestimo(EnumStatusEmprestimo.ATIVO).stream()
                .map(EmprestimoResponse::from)
                .toList();
    }
}
