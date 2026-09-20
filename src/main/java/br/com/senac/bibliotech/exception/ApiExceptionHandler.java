package br.com.senac.bibliotech.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ponto ÚNICO onde exceções viram respostas HTTP.
 *
 * POR QUE centralizar? Sem isso, cada método de cada controller teria seu próprio
 * if/else de 404, e cada um responderia num formato diferente.
 *
 * ProblemDetail é o formato padrão RFC 9457 ("problem+json") que o Spring já suporta:
 * { "status": 404, "detail": "Autor com id 5 não foi encontrado", ... }
 *
 * ATENÇÃO: NÃO crie um @ExceptionHandler(Exception.class) genérico aqui.
 * Ele também capturaria as exceções internas do Spring MVC (rota inexistente, método
 * HTTP errado, JSON malformado) e transformaria 404/405/400 em 500.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(br.com.senac.bibliotech.exception.RecursoNaoEncontradoException.class)
    public ProblemDetail tratarNaoEncontrado(br.com.senac.bibliotech.exception.RecursoNaoEncontradoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(br.com.senac.bibliotech.exception.ConflitoException.class)
    public ProblemDetail tratarConflito(br.com.senac.bibliotech.exception.ConflitoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Rede de segurança do banco: UNIQUE violado por duas requisições simultâneas,
    // ou DELETE de um autor que ainda tem livros (FK). A mensagem é genérica DE PROPÓSITO:
    // o texto original da exceção traz nomes de tabela/constraint, que não devem vazar.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail tratarIntegridade(DataIntegrityViolationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "A operação viola uma regra de integridade (registro duplicado ou em uso)");
    }

    @ExceptionHandler(br.com.senac.bibliotech.exception.CredenciaisInvalidasException.class)
    public ProblemDetail tratarCredenciais(br.com.senac.bibliotech.exception.CredenciaisInvalidasException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    // Disparada quando o @Valid do controller reprova o DTO.
    // Devolvemos um mapa campo -> mensagem para o front destacar cada campo com erro.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(erro -> campos.putIfAbsent(erro.getField(), erro.getDefaultMessage()));

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Dados inválidos");
        problema.setProperty("campos", campos);
        return problema;
    }
}
