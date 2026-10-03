package br.com.senac.bibliotech.presentation;

import br.com.senac.bibliotech.application.dto.AutorRequest;
import br.com.senac.bibliotech.application.dto.AutorResponse;
import br.com.senac.bibliotech.application.service.AutorService;
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
@RequestMapping("/autores")
@RequiredArgsConstructor
@Tag(name = "Autores", description = "Gerenciamento de autores da biblioteca")
public class AutorController {

    private final AutorService autorService;

    @PostMapping
    @Operation(summary = "Cadastrar um novo autor", description = "Registar um novo autor")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Autor Registrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<AutorResponse> criar(@Valid @RequestBody AutorRequest request) {
        AutorResponse response = autorService.criar(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }


    @GetMapping
    @Operation(summary = "Listar todos os Autores")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de autores cadastrados")
    })
    public ResponseEntity<List<AutorResponse>> listarTodos() {
        return ResponseEntity.ok(autorService.listarTodos());
    }


    @GetMapping("/{id}")
    @Operation(summary = "Busca autor por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autor encontrado"),
            @ApiResponse(responseCode = "404", description = "Autor não encontrado")
    })
    public ResponseEntity<AutorResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(autorService.buscarPorId(id));
    }



    @PutMapping("/{id}")
    @Operation(summary = "Atualização de Autor", description = "Substituição dos dados dos autores")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autor atualizado"),
            @ApiResponse(responseCode = "404", description = "Autor não encontrado")
    })
    public ResponseEntity<AutorResponse> atualizar(@PathVariable Long id,
                                                   @Valid @RequestBody AutorRequest request) {
        return ResponseEntity.ok(autorService.atualizar(id, request));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar autor")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Autor removido"),
            @ApiResponse(responseCode = "404", description = "Autor não encontrado")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        autorService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}