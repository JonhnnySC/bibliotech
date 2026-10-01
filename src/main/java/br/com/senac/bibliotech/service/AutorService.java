package br.com.senac.bibliotech.service;

import br.com.senac.bibliotech.dto.AutorRequest;
import br.com.senac.bibliotech.dto.AutorResponse;
import br.com.senac.bibliotech.entities.Autor;
import br.com.senac.bibliotech.exception.RecursoNaoEncontradoException;
import br.com.senac.bibliotech.repository.AutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutorService {

    private final AutorRepository autorRepository;

    @Transactional
    public AutorResponse criar(AutorRequest request) {
        Autor autor = Autor.builder()
                .nome(request.nome())
                .nacionalidade(request.nacionalidade())
                .dataNascimento(request.dataNascimento())
                .build();

        return AutorResponse.from(autorRepository.save(autor));
    }

    public List<AutorResponse> listarTodos() {
        return autorRepository.findAll().stream()
                .map(AutorResponse::from)
                .toList();
    }

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

        return AutorResponse.from(autorRepository.save(autor));
    }

    @Transactional
    public void deletar(Long id) {
        if (!autorRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Autor", id);
        }
        autorRepository.deleteById(id);
    }
}