package br.com.senac.bibliotech.application.service;

import br.com.senac.bibliotech.application.dto.ExemplarRequest;
import br.com.senac.bibliotech.application.dto.ExemplarResponse;
import br.com.senac.bibliotech.domain.entities.Exemplar;
import br.com.senac.bibliotech.domain.entities.Livro;
import br.com.senac.bibliotech.domain.enums.EnumStatusExemplar;
import br.com.senac.bibliotech.exception.ConflitoException;
import br.com.senac.bibliotech.exception.RecursoNaoEncontradoException;
import br.com.senac.bibliotech.domain.repository.ExemplarRepository;
import br.com.senac.bibliotech.domain.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExemplarService {

    private final ExemplarRepository exemplarRepository;
    private final LivroRepository livroRepository;

    @Transactional
    public ExemplarResponse criar(ExemplarRequest request) {
        Livro livro = livroRepository.findById(request.livroId())
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

    public List<ExemplarResponse> listarPorLivro(Long livroId) {
        return exemplarRepository.findByLivroId(livroId).stream()
                .map(ExemplarResponse::from)
                .toList();
    }

    @Transactional
    public ExemplarResponse atualizarStatus(Long exemplarId, EnumStatusExemplar novoStatus) {
        Exemplar exemplar = exemplarRepository.findById(exemplarId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exemplar", exemplarId));

        if (exemplar.getStatus() == EnumStatusExemplar.EMPRESTADO && novoStatus != EnumStatusExemplar.DISPONIVEL) {
            throw new ConflitoException("Não é possível alterar o status de um exemplar atualmente emprestado. Faça a devolução primeiro.");
        }

        exemplar.setStatus(novoStatus);
        return ExemplarResponse.from(exemplarRepository.save(exemplar));
    }
}