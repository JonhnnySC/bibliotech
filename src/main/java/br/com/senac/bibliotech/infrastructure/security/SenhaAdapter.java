package br.com.senac.bibliotech.infrastructure.security;

//Adaptador pra senha


import br.com.senac.bibliotech.application.port.SenhaPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SenhaAdapter implements SenhaPort {
    private final PasswordEncoder encoder;

    @Override public String codificar(String senha) { return encoder.encode(senha); }
    @Override public boolean confere(String senha, String hash) { return encoder.matches(senha, hash); }
}
