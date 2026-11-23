package br.com.senac.bibliotech.application.service;

import br.com.senac.bibliotech.application.dto.AutorRequest;
import br.com.senac.bibliotech.application.dto.AutorResponse;
import br.com.senac.bibliotech.domain.entities.Autor;
import br.com.senac.bibliotech.domain.entities.Livro;
import br.com.senac.bibliotech.domain.exception.RecursoNaoEncontradoException;
import br.com.senac.bibliotech.domain.repository.AutorRepository;
import br.com.senac.bibliotech.domain.repository.BibliotecaRepository;
import br.com.senac.bibliotech.domain.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AutorService {

    private final AutorRepository autorRepository;
    private final LivroRepository livroRepository;
    private final BibliotecaRepository bibliotecaRepository;
    private final BibliotecaLogado bibliotecaLogado;

    @Transactional
    public AutorResponse criar(AutorRequest request) {
        Autor autor = Autor.builder()
                .nome(request.nome())
                .nacionalidade(request.nacionalidade())
                .dataNascimento(request.dataNascimento())
                .fotoUrl(request.fotoUrl())
                .biblioteca(bibliotecaRepository.referencia(bibliotecaLogado.id())) // dono do registro
                .build();

        sincronizarLivros(autor, request.livroIds());
        return AutorResponse.from(autorRepository.save(autor));
    }

    @Transactional(readOnly = true)
    public List<AutorResponse> listarTodos() {
        return autorRepository.findAllByBiblioteca_Id(bibliotecaLogado.id()).stream()
                .map(AutorResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AutorResponse buscarPorId(Long id) {
        return AutorResponse.from(buscarNoEscopo(id));
    }

    @Transactional
    public AutorResponse atualizar(Long id, AutorRequest request) {
        Autor autor = buscarNoEscopo(id);

        autor.setNome(request.nome());
        autor.setNacionalidade(request.nacionalidade());
        autor.setDataNascimento(request.dataNascimento());
        autor.setFotoUrl(request.fotoUrl());

        if (request.livroIds() != null) {
            sincronizarLivros(autor, request.livroIds());
        }

        return AutorResponse.from(autorRepository.save(autor));
    }

    @Transactional
    public void deletar(Long id) {
        if (!autorRepository.existsByIdAndBiblioteca_Id(id, bibliotecaLogado.id())) {
            throw new RecursoNaoEncontradoException("Autor", id);
        }
        autorRepository.deleteById(id);
    }

    // Fora da minha biblioteca = "não existe" (404).
    private Autor buscarNoEscopo(Long id) {
        return autorRepository.findByIdAndBiblioteca_Id(id, bibliotecaLogado.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Autor", id));
    }

    // Deixa o autor com exatamente os livros de "ids" (tabela autor_livro), todos da minha biblioteca.
    private void sincronizarLivros(Autor autor, List<Long> ids) {
        List<Long> desejados = ids == null ? List.of() : ids.stream().distinct().toList();

        List<Livro> encontrados = livroRepository.findAllByIdInAndBibliotecaId(desejados, bibliotecaLogado.id());

        Set<Long> achados = new HashSet<>();
        encontrados.forEach(l -> achados.add(l.getId()));

        for (Long id : desejados) {
            if (!achados.contains(id)) {
                throw new RecursoNaoEncontradoException("Livro", id);
            }
        }

        autor.getLivros().clear();
        autor.getLivros().addAll(encontrados);
    }
}