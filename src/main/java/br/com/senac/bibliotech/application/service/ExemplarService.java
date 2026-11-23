package br.com.senac.bibliotech.application.service;

import br.com.senac.bibliotech.application.dto.ExemplarRequest;
import br.com.senac.bibliotech.application.dto.ExemplarResponse;
import br.com.senac.bibliotech.domain.entities.Exemplar;
import br.com.senac.bibliotech.domain.entities.Livro;
import br.com.senac.bibliotech.domain.enums.EnumStatusExemplar;
import br.com.senac.bibliotech.domain.exception.ConflitoException;
import br.com.senac.bibliotech.domain.exception.RecursoNaoEncontradoException;
import br.com.senac.bibliotech.domain.repository.ExemplarRepository;
import br.com.senac.bibliotech.domain.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * O exemplar nao tem biblioteca_id proprio: herda o dono pelo livro.
 * Por isso as consultas navegam Livro_Biblioteca_Id.
 */
@Service
@RequiredArgsConstructor
public class ExemplarService {

    private final ExemplarRepository exemplarRepository;
    private final LivroRepository livroRepository;
    private final BibliotecaLogado bibliotecaLogado;

    @Transactional
    public ExemplarResponse criar(ExemplarRequest request) {
        Livro livro = livroRepository.findByIdAndBibliotecaId(request.livroId(), bibliotecaLogado.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro", request.livroId()));

        Exemplar exemplar = Exemplar.builder()
                .livro(livro)
                .dataImpressao(request.dataImpressao())
                .dataCompra(request.dataCompra())
                .capaDura(request.capaDura())
                .status(EnumStatusExemplar.DISPONIVEL)
                .build();

        return ExemplarResponse.from(exemplarRepository.save(exemplar));
    }

    @Transactional(readOnly = true)
    public List<ExemplarResponse> listarPorLivro(Long livroId) {
        // Livro de outra biblioteca = 404, sem confirmar que ele existe.
        if (!livroRepository.existsByIdAndBibliotecaId(livroId, bibliotecaLogado.id())) {
            throw new RecursoNaoEncontradoException("Livro", livroId);
        }
        return exemplarRepository.findByLivroIdAndLivro_Biblioteca_Id(livroId, bibliotecaLogado.id())
                .stream()
                .map(ExemplarResponse::from)
                .toList();
    }

    @Transactional
    public ExemplarResponse atualizarStatus(Long exemplarId, EnumStatusExemplar novoStatus) {
        Exemplar exemplar = exemplarRepository
                .findByIdAndLivro_Biblioteca_Id(exemplarId, bibliotecaLogado.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exemplar", exemplarId));

        // Regra de circulacao: quem esta emprestado so volta por devolucao.
        if (exemplar.getStatus() == EnumStatusExemplar.EMPRESTADO
                && novoStatus != EnumStatusExemplar.DISPONIVEL) {
            throw new ConflitoException(
                    "Não é possível alterar o status de um exemplar atualmente emprestado. Faça a devolução primeiro.");
        }

        exemplar.setStatus(novoStatus);
        return ExemplarResponse.from(exemplarRepository.save(exemplar));
    }
}