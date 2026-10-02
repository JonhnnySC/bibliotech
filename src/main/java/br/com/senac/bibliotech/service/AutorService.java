package br.com.senac.bibliotech.service;

import br.com.senac.bibliotech.dto.AutorRequest;
import br.com.senac.bibliotech.dto.AutorResponse;
import br.com.senac.bibliotech.entities.Autor;
import br.com.senac.bibliotech.entities.Livro;
import br.com.senac.bibliotech.exception.RecursoNaoEncontradoException;
import br.com.senac.bibliotech.repository.AutorRepository;
import br.com.senac.bibliotech.repository.LivroRepository;
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

    @Transactional
    public AutorResponse criar(AutorRequest request) {
        Autor autor = Autor.builder()
                .nome(request.nome())
                .nacionalidade(request.nacionalidade())
                .dataNascimento(request.dataNascimento())
                .fotoUrl(request.fotoUrl())
                .build();

        sincronizarLivros(autor, request.livroIds());

        return AutorResponse.from(autorRepository.save(autor));
    }

    // transação aberta: "livros" é lazy e é lido dentro do from()
    @Transactional(readOnly = true)
    public List<AutorResponse> listarTodos() {
        return autorRepository.findAll().stream()
                .map(AutorResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AutorResponse buscarPorId(Long id) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Autor", id));
        return AutorResponse.from(autor);
    }

    @Transactional
    public AutorResponse atualizar(Long id, AutorRequest request) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Autor", id));

        autor.setNome(request.nome());
        autor.setNacionalidade(request.nacionalidade());
        autor.setDataNascimento(request.dataNascimento());
        autor.setFotoUrl(request.fotoUrl());

        // null = não mexe nos vínculos (evita apagar tudo por engano)
        if (request.livroIds() != null) {
            sincronizarLivros(autor, request.livroIds());
        }

        return AutorResponse.from(autorRepository.save(autor));
    }

    @Transactional
    public void deletar(Long id) {
        if (!autorRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Autor", id);
        }
        // como Autor é o dono do @ManyToMany, o Hibernate remove as linhas de autor_livro
        autorRepository.deleteById(id);
    }

    // Deixa o autor com exatamente os livros de "ids" (tabela autor_livro).
    private void sincronizarLivros(Autor autor, List<Long> ids) {
        List<Long> desejados = ids == null ? List.of() : ids.stream().distinct().toList();

        List<Livro> encontrados = livroRepository.findAllById(desejados);
        if (encontrados.size() != desejados.size()) {
            Set<Long> achados = new HashSet<>();
            encontrados.forEach(l -> achados.add(l.getId()));
            Long faltando = desejados.stream()
                    .filter(i -> !achados.contains(i))
                    .findFirst()
                    .orElseThrow();
            throw new RecursoNaoEncontradoException("Livro", faltando);
        }

        autor.getLivros().clear();
        autor.getLivros().addAll(encontrados);
    }
}