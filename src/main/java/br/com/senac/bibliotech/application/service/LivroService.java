package br.com.senac.bibliotech.application.service;

import br.com.senac.bibliotech.application.dto.LivroRequest;
import br.com.senac.bibliotech.application.dto.LivroResponse;
import br.com.senac.bibliotech.domain.entities.Livro;
import br.com.senac.bibliotech.domain.exception.ConflitoException;
import br.com.senac.bibliotech.domain.exception.RecursoNaoEncontradoException;
import br.com.senac.bibliotech.domain.repository.BibliotecaRepository;
import br.com.senac.bibliotech.domain.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;
    private final BibliotecaRepository bibliotecaRepository;
    private final BibliotecaLogado bibliotecaLogado;

    @Transactional
    public LivroResponse criar(LivroRequest request) {
        Long bibliotecaId = bibliotecaLogado.id();
        validarIsbnLivre(bibliotecaId, request.isbn());

        Livro livro = Livro.builder()
                .volume(request.volume())
                .isbn(request.isbn())
                .descricao(request.descricao())
                .edicao(request.edicao())
                .tipo(request.tipo())
                .paginas(request.paginas())
                .dataLancamento(request.dataLancamento())
                .capaUrl(request.capaUrl())
                .biblioteca(bibliotecaRepository.referencia(bibliotecaId))
                .build();

        return LivroResponse.from(livroRepository.save(livro));
    }

    @Transactional
    public LivroResponse atualizar(Long id, LivroRequest dto) {
        Livro livro = buscarNoEscopo(id);

        if (!Objects.equals(livro.getIsbn(), dto.isbn())) {
            validarIsbnLivre(bibliotecaLogado.id(), dto.isbn());
        }

        livro.setVolume(dto.volume());
        livro.setIsbn(dto.isbn());
        livro.setDescricao(dto.descricao());
        livro.setEdicao(dto.edicao());
        livro.setTipo(dto.tipo());
        livro.setPaginas(dto.paginas());
        livro.setDataLancamento(dto.dataLancamento());
        livro.setCapaUrl(dto.capaUrl());

        return LivroResponse.from(livroRepository.save(livro));
    }

    @Transactional(readOnly = true)
    public List<LivroResponse> listarTodos() {
        return livroRepository.findAllByBibliotecaId(bibliotecaLogado.id()).stream()
                .map(LivroResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public LivroResponse buscarPorId(Long id) {
        return LivroResponse.from(buscarNoEscopo(id));
    }

    @Transactional
    public void deletar(Long id) {
        livroRepository.delete(buscarNoEscopo(id));
    }

    private Livro buscarNoEscopo(Long id) {
        return livroRepository.findByIdAndBibliotecaId(id, bibliotecaLogado.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro", id));
    }

    private void validarIsbnLivre(Long bibliotecaId, String isbn) {
        if (isbn != null && livroRepository.existsByBibliotecaIdAndIsbn(bibliotecaId, isbn)) {
            throw new ConflitoException("Já existe um livro com esse ISBN nesta biblioteca");
        }
    }
}
