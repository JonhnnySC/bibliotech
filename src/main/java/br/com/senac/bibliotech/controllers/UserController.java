package br.com.senac.bibliotech.controllers;

import br.com.senac.bibliotech.entities.Usuario;
import br.com.senac.bibliotech.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    private UsuarioRepository usuarioRepository;

    public UserController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    @GetMapping
    public ResponseEntity<List<Usuario>> listAll() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }


}
