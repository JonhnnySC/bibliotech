package br.com.senac.bibliotech.controllers;

import br.com.senac.bibliotech.dto.EmprestimoRequest;
import br.com.senac.bibliotech.entities.Emprestimo;
import br.com.senac.bibliotech.service.EmprestimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestBody("/emprestimos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping
    public ResponseEntity<Emprestimo> criar(@RequestBody EmprestimoRequest emprestimoRequest) {
        Emprestimo novoEmprestimo = emprestimoService.realizarEmprestimo(
                emprestimoRequest.leitorId(),
                emprestimoRequest.exemplarId());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoEmprestimo);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Emprestimo>> listarAtivos() {
        return ResponseEntity.ok(emprestimoService.listarEmprestimosAtivos());
    }

    @PatchMapping("/{id}/devolver")
    public ResponseEntity<Void> devolver(@PathVariable Long id) {
        emprestimoService.devolverEmprestimo(id);
        return ResponseEntity.noContent().build();
    }
}
