package br.com.senac.bibliotech.controllers;

import br.com.senac.bibliotech.dto.AtualizarStatusRequest;
import br.com.senac.bibliotech.entities.Usuario;
import br.com.senac.bibliotech.enums.EnumStatusUsuario;
import br.com.senac.bibliotech.repository.UsuarioRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
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

    /*
    {id na url é var de caminho. @pathvar long id captura esse valor e converte pra Long
    findBYid retorna Optional<Usuarios>.orElse(null extrai o valor ou retorna null
    se encontrado 200, se não 404

    GET devolve a entidade inteira, senha e cpf.
    Preciso de um DTO de resposta.

    Quando vierem os relacionamentos, sem DTo, vou ter loop infinito de json e LazyInitializationException.
     */

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);
        if (usuarioBanco != null) {
            return ResponseEntity.ok(usuarioBanco);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@RequestBody Usuario usuario) {
        var usuarioBanco = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioBanco);
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody AtualizarStatusRequest statusRequest) {
        Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);
        if(usuarioBanco!= null) {
            usuarioBanco.setStatus(statusRequest.status());
            usuarioRepository.save(usuarioBanco);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /*
    Por que copiar campo a campo em usuarioBanco, e não save(usuario) direto?

    O id da URL é a fonte da verdade. Se o JSON mandar outro id, você não troca o registro.
    Você atualiza a linha que já existe, não insere outra.
    Campos que você não copiar (ex.: relacionamentos) não são zerados.

    O try/catch que relança new RuntimeException(e) não resolve nada. Só embrulha o erro. Pode tirar: o Spring já devolve 500 se explodir.
     */

    @PutMapping("/{id}") // atualizar cadastro de usuario, PUT é pra atualizar o recurso inteiro, PATCH é pra atualizar parcialmente
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {

        try {
            Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);
            if (usuarioBanco != null) {
                usuarioBanco.setStatus(usuario.getStatus());
                usuarioBanco.setNome(usuario.getNome());
                usuarioBanco.setEmail(usuario.getEmail());
                usuarioBanco.setSenha(usuario.getSenha());
                usuarioBanco.setCpf(usuario.getCpf());
                usuarioRepository.save(usuarioBanco);
            }
            return ResponseEntity.ok(usuarioBanco);
        }catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
    @DeleteMapping("/{id}/excluir")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Long id) {
        Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);
        if (usuarioBanco != null) {
            usuarioBanco.setStatus(EnumStatusUsuario.INATIVO);
            usuarioRepository.save(usuarioBanco);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }
}
