package br.com.senac.bibliotech.service;

import br.com.senac.bibliotech.dto.LivroRequest;
import br.com.senac.bibliotech.dto.LivroResponse;
import br.com.senac.bibliotech.entities.Livro;
import br.com.senac.bibliotech.exception.RecursoNaoEncontradoException;
import br.com.senac.bibliotech.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;

    @Transactional
    public LivroResponse criar(LivroRequest request) {
        Livro livro = Livro.builder()
                .volume(request.volume())
                .isbn(request.isbn())
                .descricao(request.descricao())
                .edicao(request.edicao())
                .tipo(request.tipo())
                .paginas(request.paginas())
                .dataLancamento(request.dataLancamento())
                .build();

        return LivroResponse.from(livroRepository.save(livro));
    }

    public List<LivroResponse> listarTodos() {
        return livroRepository.findAll().stream()
                .map(LivroResponse::from)
                .toList();
    }

    public LivroResponse buscarPorId(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro", id));
        return LivroResponse.from(livro);
    }

    @Transactional
    public void deletar(Long id) {
        if (!livroRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Livro", id);
        }
        livroRepository.deleteById(id);
    }
}