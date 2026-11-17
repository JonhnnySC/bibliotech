package br.com.senac.bibliotech.infrastructure.config;

import br.com.senac.bibliotech.domain.repository.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
// por que é só class, por que component e oque é aplicationrtunner? e por que?

@Component
@RequiredArgsConstructor
class TokenClearer implements ApplicationRunner {

    private final TokenRepository tokenRepository;

    @Override
    @Transactional
    public void run (ApplicationArguments args) {
        tokenRepository.deleteAllInBatch(); // oque é batch?
    }
}
