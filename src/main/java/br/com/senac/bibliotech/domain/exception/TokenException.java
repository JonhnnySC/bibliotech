package br.com.senac.bibliotech.domain.exception;

public class TokenException extends RuntimeException {
    public TokenException(String message) {
        super(message);
    }
}
