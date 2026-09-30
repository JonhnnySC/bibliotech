package br.com.senac.bibliotech.controllers;

import br.com.senac.bibliotech.dto.ExemplarRequest;
import br.com.senac.bibliotech.dto.ExemplarResponse;
import br.com.senac.bibliotech.enums.EnumStatusExemplar;
import br.com.senac.bibliotech.service.ExemplarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exemplares")
@RequiredArgsConstructor
public class ExemplarCOntroller {

    private final ExemplarService exemplarService;

    @PostMapping
    public ResponseEntity<ExemplarResponse> criar(@Valid @RequestBody ExemplarRequest exemplarRequest) {
        return ResponseEntity.ok(exemplarService.criar(exemplarRequest));
    }

    @GetMapping("/livro/{livroId}")
    public ResponseEntity<List<ExemplarResponse>> listarPorLivro(@PathVariable Long livroId) {
        return ResponseEntity.ok(exemplarService.listarPorLivro(livroId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ExemplarResponse> atualizarStatus(
            @PathVariable Long id,
            @RequestParam EnumStatusExemplar statusExemplar) {
        return ResponseEntity.ok(exemplarService.atualizarStatus(id, statusExemplar));
    }
}