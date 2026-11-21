package br.com.senac.bibliotech.application.port;


import java.util.Optional;

//por que eu nao coloquei na pasta de interface? por que ele não é um repositoyry, ele integra extrenamente algo
public interface ConsultaCepPort {

    //pq opitional? é uma caixa vazia que pode ter um valor ou nao, mas nao ser null, por que null obriga a tgrtatar o caso vazio
    // mas por que aionda sim nesse caso?
    //r - cep nao existir é uma resposta noormal, e nao uma excecao.
    Optional<String> consultaCep(String cep);
}
