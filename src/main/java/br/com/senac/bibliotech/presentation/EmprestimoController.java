package br.com.senac.bibliotech.presentation;

import br.com.senac.bibliotech.application.dto.EmprestimoRequest;
import br.com.senac.bibliotech.domain.entities.Emprestimo;
import br.com.senac.bibliotech.application.service.EmprestimoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emprestimos")
@RequiredArgsConstructor
@Tag(name = "Empréstimos", description = "Gerenciamento dos Empréstismos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping
    @Operation(summary = "Fazer um novo empréstimo",
            description = "Registro de novo empréstimo de um exemplar para o leitor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empréstimo realizado"),
            @ApiResponse(responseCode = "404", description = "Leitor ou Exemplar não encontrado"),
            @ApiResponse(responseCode = "409", description = "Exemplar não esta disponível para empréstimo")
    })


    public ResponseEntity<Emprestimo> criar(@RequestBody EmprestimoRequest emprestimoRequest) {
        Emprestimo novoEmprestimo = emprestimoService.realizarEmprestimo(
                emprestimoRequest.leitorId(),
                emprestimoRequest.exemplarId());

        return ResponseEntity.status(HttpStatus.CREATED).body(novoEmprestimo);
    }



    @GetMapping("/ativos")
    @Operation(summary = "Listagem dos empréstimos ativos",
            description = "Retornar todos os emprestimos que ainda não foram devolvidos")
    public ResponseEntity<List<Emprestimo>> listarAtivos() {
        return ResponseEntity.ok(emprestimoService.listarEmprestimos());
    }



    @PatchMapping("/{id}/devolver")
    @Operation(summary = "Devolução do empréstimo",
               description = "Marcando o emprestimo como devolvido e libera o exemplar para outro empréstimos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Devolução Realizada"),
            @ApiResponse(responseCode = "404", description = "Emprestimo não achado"),
            @ApiResponse(responseCode = "409", description = "Empréstimo já devolvido")
    })


    public ResponseEntity<Void> devolver(@PathVariable Long id) {
        emprestimoService.devolverEmprestimo(id);

        return ResponseEntity.noContent().build();
    }
}
