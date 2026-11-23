package br.com.senac.bibliotech.infrastructure.config;

import br.com.senac.bibliotech.application.port.TokenPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/*
 ApplicationRunner roda logo após o contexto subir. Aqui ele zera a tabela de tokens: odo token emitido antes deste boot deixa de valer
*/
@Component
@RequiredArgsConstructor
class TokenClearer implements ApplicationRunner {

    private final TokenPort tokenPort;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        tokenPort.revogarTodos();
    }
}