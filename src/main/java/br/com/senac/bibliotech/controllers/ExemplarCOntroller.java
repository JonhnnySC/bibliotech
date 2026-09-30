package br.com.senac.bibliotech.controllers;

import br.com.senac.bibliotech.dto.ExemplarRequest;
import br.com.senac.bibliotech.dto.ExemplarResponse;
import br.com.senac.bibliotech.enums.EnumStatusExemplar;
import br.com.senac.bibliotech.service.ExemplarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exemplares")
@RequiredArgsConstructor
@Tag(name = "Exemplares", description = "Copias dos livros que estão registrados na biblioteca")
public class ExemplarCOntroller {

    private final ExemplarService exemplarService;

    @PostMapping
    @Operation(summary = "Cadastrar um novo exemplar", description = "Registro de uma cópia de um livro existente no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exemplar Criado"),
            @ApiResponse(responseCode = "404", description = "Exemplar não achado")
    })
    public ResponseEntity<ExemplarResponse> criar(@Valid @RequestBody ExemplarRequest exemplarRequest) {
        return ResponseEntity.ok(exemplarService.criar(exemplarRequest));
    }


    @GetMapping("/livro/{livroId}")
    @Operation(summary = "Listar exemplares de um livro", description = "Retorna todas as cópias do livro procurado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista dos exemplares do livro")
    })
    public ResponseEntity<List<ExemplarResponse>> listarPorLivro(@PathVariable Long livroId) {
        return ResponseEntity.ok(exemplarService.listarPorLivro(livroId));
    }


    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status de exemplar", description = "Muda o status: Disponivel, Emprestado, Danificado, Perdido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status Atualizado"),
            @ApiResponse(responseCode = "404", description = "Exemplar não encontrado"),
            @ApiResponse(responseCode = "409", description = "Exemplar emprestado não pode mudar de status")
    })
    public ResponseEntity<ExemplarResponse> atualizarStatus(
            @PathVariable Long id,
            @RequestParam EnumStatusExemplar statusExemplar) {
        return ResponseEntity.ok(exemplarService.atualizarStatus(id, statusExemplar));
    }
}