package br.com.senac.bibliotech.exception;

/* Lançada no login. A mensagem é SEMPRE a mesma, seja email inexistente, senha erra
 ou usuário inativo. Se a API dissesse "email não encontrado", um atacante poderia
  descobrir quais emails existem no sistema numeração de usuários).
 */
public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException() {
        super("Credenciais inválidas");
    }
}
