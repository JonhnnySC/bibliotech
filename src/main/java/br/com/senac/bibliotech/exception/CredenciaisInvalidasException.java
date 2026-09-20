package br.com.senac.bibliotech.exception;

/**
 * Lançada no login. A mensagem é SEMPRE a mesma, seja email inexistente, senha errada
 * ou usuário inativo. Se a API dissesse "email não encontrado", um atacante poderia
 * descobrir quais emails existem no sistema (enumeração de usuários).
 */
public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException() {
        super("Credenciais inválidas");
    }
}
