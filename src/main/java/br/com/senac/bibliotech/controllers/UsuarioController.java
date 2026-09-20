package br.com.senac.bibliotech.controllers;

import br.com.senac.bibliotech.dto.AlterarSenhaRequest;
import br.com.senac.bibliotech.dto.AtualizarPerfilRequest;
import br.com.senac.bibliotech.dto.AtualizarStatusRequest;
import br.com.senac.bibliotech.dto.AtualizarUsuarioRequest;
import br.com.senac.bibliotech.dto.UsuarioRequest;
import br.com.senac.bibliotech.dto.UsuarioResponse;
import br.com.senac.bibliotech.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Controller FINO: recebe, valida o formato (@Valid), delega ao service e devolve o
 * status certo. Nenhum if de negócio, nenhum orElse(null), nenhum try/catch: as falhas
 * viram exceção e o ApiExceptionHandler converte em 404/409/400.
 *
 * *** SEGURANÇA: LEIA ANTES DE USAR ***
 * Enquanto o JwtFilter não existir, TODAS estas rotas estão abertas, inclusive
 * PATCH /perfil (qualquer um viraria ADMINISTRADOR). Use somente na sua máquina.
 * Quando o filtro entrar, cada rota ganha uma regra de quem pode chamar
 * (a tabela de permissões está na resposta do chat).
 *
 * POR QUE PATCHes separados (senha, status, perfil) em vez de um PUT gigante?
 * Porque a PERMISSÃO é diferente para cada intenção. Um PUT que aceita tudo obriga
 * a checar campo por campo quem pode mexer em quê, e é fácil errar.
 *
 * PUT x PATCH: PUT substitui o recurso inteiro (aqui: os dados cadastrais);
 * PATCH altera uma parte (uma "intenção").
 */
@RestController
@RequestMapping("/usuarios") // plural: a URL nomeia a coleção; o verbo HTTP é a ação
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // POST /usuarios -> 201 Created + header Location apontando para o recurso novo
    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse criado = usuarioService.cadastrar(request);
        return ResponseEntity.created(URI.create("/usuarios/" + criado.id())).body(criado);
    }

    // GET /usuarios -> 200 com a lista (sem os EXCLUIDOS). Devolver o DTO direto já
    // dá 200; só usamos ResponseEntity quando precisamos controlar status ou header.
    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    // GET /usuarios/5 -> 200, ou 404 (o service lança a exceção)
    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable Long id) {
        return usuarioService.buscar(id);
    }

    // PUT /usuarios/5 -> atualiza nome, email e cpf
    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable Long id,
                                     @Valid @RequestBody AtualizarUsuarioRequest request) {
        return usuarioService.atualizar(id, request);
    }

    // PATCH /usuarios/5/status -> ATIVO, BLOQUEADO, INATIVO...
    @PatchMapping("/{id}/status")
    public UsuarioResponse atualizarStatus(@PathVariable Long id,
                                           @Valid @RequestBody AtualizarStatusRequest request) {
        return usuarioService.atualizarStatus(id, request.status());
    }

    // PATCH /usuarios/5/perfil -> promover ou rebaixar (só ADMINISTRADOR, no futuro)
    @PatchMapping("/{id}/perfil")
    public UsuarioResponse atualizarPerfil(@PathVariable Long id,
                                           @Valid @RequestBody AtualizarPerfilRequest request) {
        return usuarioService.atualizarPerfil(id, request.perfil());
    }

    // PATCH /usuarios/5/senha -> 204 No Content: deu certo e não há nada a devolver.
    // (Nunca devolva a senha, nem o hash.)
    @PatchMapping("/{id}/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarSenha(@PathVariable Long id,
                             @Valid @RequestBody AlterarSenhaRequest request) {
        usuarioService.alterarSenha(id, request);
    }

    // DELETE /usuarios/5 -> 204. É um soft delete (status EXCLUIDO).
    // Antes era DELETE /{id}/excluir: o verbo HTTP já diz "excluir".
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
    }
}