package br.com.senac.bibliotech.presentation;

import br.com.senac.bibliotech.application.dto.ExemplarRequest;
import br.com.senac.bibliotech.application.dto.ExemplarResponse;
import br.com.senac.bibliotech.application.service.ExemplarService;
import br.com.senac.bibliotech.domain.enums.EnumStatusExemplar;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/exemplares")
@RequiredArgsConstructor
@Tag(name = "Exemplares", description = "Cópias físicas dos livros da biblioteca")
@SecurityRequirement(name = "bearerAuth")
public class ExemplarCOntroller {

    private final ExemplarService exemplarService;

    @PostMapping
    @Operation(summary = "Cadastrar exemplar",
            description = "Registra uma cópia física de um livro da minha biblioteca. Nasce DISPONIVEL.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Exemplar criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Requer ADMIN ou BIBLIOTECARIO"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado nesta biblioteca")
    })
    public ResponseEntity<ExemplarResponse> criar(@Valid @RequestBody ExemplarRequest request) {
        ExemplarResponse response = exemplarService.criar(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/livro/{livroId}")
    @Operation(summary = "Listar exemplares de um livro",
            description = "Todas as cópias do livro, desde que o livro seja da minha biblioteca.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de exemplares"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado nesta biblioteca")
    })
    public ResponseEntity<List<ExemplarResponse>> listarPorLivro(@PathVariable Long livroId) {
        return ResponseEntity.ok(exemplarService.listarPorLivro(livroId));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do exemplar",
            description = "DISPONIVEL, DANIFICADO ou PERDIDO. Exemplar EMPRESTADO só muda por devolução.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado"),
            @ApiResponse(responseCode = "400", description = "Status inválido"),
            @ApiResponse(responseCode = "403", description = "Requer ADMIN ou BIBLIOTECARIO"),
            @ApiResponse(responseCode = "404", description = "Exemplar não encontrado nesta biblioteca"),
            @ApiResponse(responseCode = "409", description = "Exemplar emprestado: faça a devolução primeiro")
    })
    public ResponseEntity<ExemplarResponse> atualizarStatus(
            @PathVariable Long id,
            @RequestParam EnumStatusExemplar statusExemplar) {
        return ResponseEntity.ok(exemplarService.atualizarStatus(id, statusExemplar));
    }
}