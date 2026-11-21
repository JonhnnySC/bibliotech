package br.com.senac.bibliotech.infrastructure.external;


import br.com.senac.bibliotech.application.dto.EnderecoCep;
import br.com.senac.bibliotech.application.exception.ServicoExternoIndisponivelException;
import br.com.senac.bibliotech.application.port.ConsultaCepPort;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;

@Component
public class ViaCepClient implements ConsultaCepPort {

    private static final Logger log = LoggerFactory.getLogger(ViaCepClient.class);
    //axios do java, sincronoo e devolve o obj em json
    private final RestClient restClient;

    public ViaCepClient(@Value("${app.viacep.url:https://viacep.com.br/ws/}") String baseUrl) {
        var http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2)) //tempo pra estabelecer o conections
                .build();

        var factory = new JdkClientHttpRequestFactory(http);
        factory.setReadTimeout(Duration.ofSeconds(3)); //tempo pra ler a resposta do servidor
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    @Override
    public Optional<EnderecoCep> consultar(String cep) {   // nome igual ao da porta
        try {
            ViaCepResposta resposta = restClient.get()
                    .uri("/{cep}/json/", cep)
                    .retrieve()
                    .body(ViaCepResposta.class);

            if (resposta == null || "true".equals(String.valueOf(resposta.erro()))) {
                return Optional.empty();
            }

            log.info("ViaCEP respondeu para o CEP {}", cep);
            return Optional.of(new EnderecoCep(cep,
                    resposta.logradouro(),
                    resposta.complemento(),
                    resposta.bairro(),
                    resposta.localidade(),
                    resposta.uf()));

        } catch (RestClientException exception) {
            log.warn("ViaCEP indisponível para o CEP {}: {}", cep, exception.getMessage());
            throw new ServicoExternoIndisponivelException("Serviço externo de CEP não disponível agora", exception);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ViaCepResposta(String logradouro, String complemento, String bairro,
                                  String localidade, String uf, Object erro) {}
}
