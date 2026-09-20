package br.com.senac.bibliotech.exception;

/**
 * Lançada quando a requisição é válida, mas conflita com o estado atual do sistema.
 * Vira HTTP 409. Exemplos: email já cadastrado; futuramente, tentar emprestar
 * um exemplar que já está EMPRESTADO.
 */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
