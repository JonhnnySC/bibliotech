package br.com.senac.bibliotech.application.service;

import br.com.senac.bibliotech.application.dto.LivroRequest;
import br.com.senac.bibliotech.application.dto.LivroResponse;
import br.com.senac.bibliotech.domain.entities.Livro;
import br.com.senac.bibliotech.exception.RecursoNaoEncontradoException;
import br.com.senac.bibliotech.domain.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    @Transactional
    public LivroResponse atualizar(Long id, LivroRequest dto) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado"));

        livro.setVolume(dto.volume());
        livro.setIsbn(dto.isbn());
        livro.setDescricao(dto.descricao());
        livro.setEdicao(dto.edicao());
        livro.setTipo(dto.tipo());
        livro.setPaginas(dto.paginas());
        livro.setDataLancamento(dto.dataLancamento());
        livro.setCapaUrl(dto.capaUrl());   // <- a capa

        return LivroResponse.from(livroRepository.save(livro));    }

    @Transactional(readOnly = true)
    public List<LivroResponse> listarTodos() {
        return livroRepository.findAll().stream()
                .map(LivroResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
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