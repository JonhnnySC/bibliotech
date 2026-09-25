package br.com.senac.bibliotech.exception;

/*
 Lançada pelos services quando um registro não existe. O ApiExceptionHandler
 converte em HTTP 404.
 POR QUE estende RuntimeException não verificada?
 O @Transactional do Spring só faz ROLLBACK, por padrão, em exceções não verificadas.Com uma exceção "checked", a transação seria confirmada mesmo com erro.
 Não obriga todo método acima a declarar "throws".
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " com id " + id + " não foi encontrado");
    }
}
