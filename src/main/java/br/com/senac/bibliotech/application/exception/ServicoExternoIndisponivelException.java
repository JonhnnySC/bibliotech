package br.com.senac.bibliotech.application.exception;


// exception assim pra saber extamente oque tratar e a msg ffica no gandler

// coloquei throwable causa pra poder passar a exception originale nao perder a causa original do erro
public class ServicoExternoIndisponivelException extends RuntimeException {
    public ServicoExternoIndisponivelException(String message, Throwable causa) {
        super(message, causa);
    }
}
