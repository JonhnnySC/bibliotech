package br.com.senac.bibliotech.application.service;

import br.com.senac.bibliotech.domain.exception.RecursoNaoEncontradoException;
import br.com.senac.bibliotech.domain.repository.LivroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MultilocacaoTest {

    @Mock LivroRepository livroRepository;
    @Mock BibliotecaLogado bibliotecaLogado;
    // ⚠️ acrescente um @Mock para CADA outra dependência do construtor de LivroService
    // (autorRepository, editoraRepository, bibliotecaRepository...)

    @InjectMocks LivroService livroService;

    @Test
    void naoEncontraLivroDeOutraBiblioteca() {
        when(bibliotecaLogado.id()).thenReturn(1L);
        when(livroRepository.findByIdAndBiblioteca_Id(99L, 1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> livroService.buscarPorId(99L));
    }
}