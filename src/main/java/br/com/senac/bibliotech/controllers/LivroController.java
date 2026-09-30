package br.com.senac.bibliotech.controllers;

import br.com.senac.bibliotech.dto.LivroRequest;
import br.com.senac.bibliotech.dto.LivroResponse;
import br.com.senac.bibliotech.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
@Tag(name = "Livros", description = "Gerenciamento dos livros")
public class LivroController {

    private final LivroService livroService;

    @PostMapping
    @Operation(summary = "Adicionar um novo livro",
            description = "Registrar um novo livro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Livro registrado com sucesso")
    })
    public ResponseEntity<LivroResponse> criar(@Valid @RequestBody LivroRequest livroRequest) {
        LivroResponse response = livroService.criar(livroRequest);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os livros", description = "Retorna todos os livros")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro encontrado"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    public ResponseEntity<List<LivroResponse>> listarTodos() {
        return ResponseEntity.ok(livroService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar livro por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro encontrado"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    public ResponseEntity<LivroResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar livro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Livro removido"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        livroService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}