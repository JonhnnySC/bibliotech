package br.com.senac.bibliotech.application.exception;

public class RequisicaoInvalidaException extends RuntimeException {
    public RequisicaoInvalidaException(String message) {
        super(message);
    }
}
