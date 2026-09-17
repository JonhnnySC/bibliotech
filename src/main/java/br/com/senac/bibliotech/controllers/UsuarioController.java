package br.com.senac.bibliotech.controllers;

import br.com.senac.bibliotech.entities.Usuario;
import br.com.senac.bibliotech.repository.UsuarioRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    //como construir um controller? basicamente http, pensar sempre que é questão de pedir, recber, levar, e tals
    //injecao de dependencia por que o repositorio que vai ser pego pelo controlador

    //um constructor é melhor que autowired, pq sim
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /* - getmapin sem path responde a get /usuarios
           - findAll traz todos os registros da tabela
           -ResponseEntity retorna 200 OK -<>- QUANDO HÁ RETORNO VERDADEIRO DO FINDALL
         */
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }
    /* {id na url é var de caminho. @pathvar long id captura esse valor e converte pra Long
    findBYid retorna Optional<Usuarios>.orElse(null extrai o valor ou retorna null
    se encontrado 200, se não 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);
        if (usuarioBanco != null) {
            return ResponseEntity.ok(usuarioBanco);
        }
        return ResponseEntity.notFound().build();
    }
}
