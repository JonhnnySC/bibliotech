package br.com.senac.bibliotech.application.service;

import br.com.senac.bibliotech.application.dto.BibliotecaRequest;
import br.com.senac.bibliotech.application.dto.BibliotecaResponse;
import br.com.senac.bibliotech.application.dto.EnderecoCep;
import br.com.senac.bibliotech.application.exception.RequisicaoInvalidaException;
import br.com.senac.bibliotech.application.port.ConsultaCepPort;
import br.com.senac.bibliotech.domain.entities.Biblioteca;
import br.com.senac.bibliotech.domain.exception.RecursoNaoEncontradoException;
import br.com.senac.bibliotech.domain.repository.BibliotecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BibliotecaService {

    private final BibliotecaRepository bibliotecaRepository;
    private final BibliotecaLogado bibliotecaLogado;
    private final ConsultaCepPort consultaCep;

    @Transactional(readOnly = true)
    public BibliotecaResponse buscarMinhaBiblioteca() {
        return paraResponse(carregarBibliotecaLogada());
    }

    @Transactional
    public BibliotecaResponse atualizarMinhaBiblioteca(BibliotecaRequest request) {
        Biblioteca biblioteca = carregarBibliotecaLogada();
        EnderecoCep endereco = consultarOuErro(request.cep());

        biblioteca.setNome(request.nome().trim());
        biblioteca.setCep(request.cep());
        biblioteca.setNumero(request.numero());
        biblioteca.setComplemento(request.complemento());
        biblioteca.setLogradouro(endereco.logradouro());
        biblioteca.setBairro(endereco.bairro());
        biblioteca.setCidade(endereco.localidade());
        biblioteca.setUf(endereco.uf());

        return paraResponse(bibliotecaRepository.save(biblioteca));
    }

    public EnderecoCep consultarCep(String cep) {
        return consultarOuErro(cep);
    }

    private EnderecoCep consultarOuErro(String cep) {
        if (cep == null || !cep.matches("\\d{8}")) {
            throw new RequisicaoInvalidaException("O cep deve ter 8 digitos");
        }
        return consultaCep.consultar(cep)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Não foi possível consultar o cep informado"));
    }

    private Biblioteca carregarBibliotecaLogada() {
        return bibliotecaRepository.findById(bibliotecaLogado.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Não foi possível encontrar a biblioteca logada"));
    }

    private BibliotecaResponse paraResponse(Biblioteca b) {
        return new BibliotecaResponse(b.getId(), b.getNome(), b.getCnpj(), b.getCep(), b.getLogradouro(),
                b.getNumero(), b.getComplemento(), b.getBairro(), b.getCidade(), b.getUf());
    }
}