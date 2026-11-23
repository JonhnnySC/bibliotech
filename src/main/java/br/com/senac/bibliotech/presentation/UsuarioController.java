package br.com.senac.bibliotech.presentation;

import br.com.senac.bibliotech.application.dto.AlterarSenhaRequest;
import br.com.senac.bibliotech.application.dto.AtualizarPerfilRequest;
import br.com.senac.bibliotech.application.dto.AtualizarStatusRequest;
import br.com.senac.bibliotech.application.dto.AtualizarUsuarioRequest;
import br.com.senac.bibliotech.application.dto.UsuarioLogado;
import br.com.senac.bibliotech.application.dto.UsuarioRequest;
import br.com.senac.bibliotech.application.dto.UsuarioResponse;
import br.com.senac.bibliotech.application.exception.AcessoNegadoException;
import br.com.senac.bibliotech.application.port.UsuarioLogadoPort;
import br.com.senac.bibliotech.application.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/*
 * Controller FINO: valida o formato (@Valid), delega ao service e devolve o status.
 * As falhas viram excecao e o ApiExceptionHandler converte em 400/403/404/409.
 *
 * Quem esta logado vem da PORTA UsuarioLogadoPort. Antes usava auth.getName(),
 * mas o principal e o record UsuarioLogado: getName() devolve o toString() do
 * record, nunca o id — a comparacao com o id da URL nunca passava.
 *
 * PATCHes separados (senha, status, perfil) porque a PERMISSAO de cada intencao
 * e diferente. PUT substitui apenas os dados cadastrais.
 */
@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "Cadastro, permissões e ciclo de vida dos usuários")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioLogadoPort usuarioLogadoPort;

    public UsuarioController(UsuarioService usuarioService, UsuarioLogadoPort usuarioLogadoPort) {
        this.usuarioService = usuarioService;
        this.usuarioLogadoPort = usuarioLogadoPort;
    }

    private UsuarioLogado logado() {
        return usuarioLogadoPort.atual()
                .orElseThrow(() -> new AcessoNegadoException("Nenhum usuário logado"));
    }

    private boolean souAdministrador(UsuarioLogado eu) {
        return eu.authorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo usuário",
            description = "Cria usuário na biblioteca de quem está logado. Email e CPF são únicos. Nasce ATIVO e LEITOR.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado",
                    content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Requer ADMIN ou BIBLIOTECARIO"),
            @ApiResponse(responseCode = "409", description = "Email ou CPF já cadastrado")
    })
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse criado = usuarioService.cadastrar(request);
        return ResponseEntity.created(URI.create("/usuarios/" + criado.id())).body(criado);
    }

    @GetMapping
    @Operation(summary = "Listar usuários da minha biblioteca",
            description = "Todos os usuários não EXCLUIDO da biblioteca do token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID",
            description = "Fora da minha biblioteca ou EXCLUIDO responde 404.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Não encontrado, excluído ou de outra biblioteca")
    })
    public UsuarioResponse buscar(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long id) {
        return usuarioService.buscar(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados cadastrais",
            description = "Atualiza nome, email e CPF. ADMIN edita qualquer usuário da sua biblioteca; os demais só a si mesmos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados atualizados"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Tentando editar outro usuário sem ser ADMIN"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Email ou CPF em uso por outro usuário")
    })
    public UsuarioResponse atualizar(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AtualizarUsuarioRequest request) {

        UsuarioLogado eu = logado();
        if (!souAdministrador(eu) && !eu.id().equals(id)) {
            throw new AcessoNegadoException("Você só pode editar seus próprios dados");
        }
        return usuarioService.atualizar(id, request);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status (apenas ADMIN)",
            description = "ATIVO, BLOQUEADO ou INATIVO. Usuário não ATIVO não consegue logar nem usar o token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado"),
            @ApiResponse(responseCode = "400", description = "Status inválido"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Requer ADMINISTRADOR"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public UsuarioResponse atualizarStatus(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusRequest request) {
        return usuarioService.atualizarStatus(id, request.status());
    }

    @PatchMapping("/{id}/perfil")
    @Operation(summary = "Atualizar perfil (apenas ADMIN)",
            description = "Promove ou rebaixa. Promover a ADMINISTRADOR exige a senha especial do .env. Vale na próxima requisição: o perfil é lido do banco, não do JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil atualizado"),
            @ApiResponse(responseCode = "400", description = "Perfil inválido"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Requer ADMINISTRADOR ou senha especial incorreta"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public UsuarioResponse atualizarPerfil(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AtualizarPerfilRequest request) {
        return usuarioService.atualizarPerfil(id, request.perfil(), request.senhaEspecial());
    }

    @PatchMapping("/{id}/senha")
    @Operation(summary = "Alterar a própria senha",
            description = "Exige a senha atual. Só o próprio usuário pode alterá-la. 204 sem corpo: nunca devolve senha ou hash.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha alterada"),
            @ApiResponse(responseCode = "400", description = "Nova senha fora dos requisitos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Tentando alterar a senha de outro usuário"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Senha atual incorreta")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarSenha(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AlterarSenhaRequest request) {

        if (!logado().id().equals(id)) {
            throw new AcessoNegadoException("Você só pode alterar a sua própria senha");
        }
        usuarioService.alterarSenha(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário (soft delete, apenas ADMIN)",
            description = "Marca como EXCLUIDO. A linha continua no banco para preservar o histórico de empréstimos.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário excluído"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Requer ADMINISTRADOR"),
            @ApiResponse(responseCode = "404", description = "Não encontrado ou já excluído")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long id) {
        usuarioService.excluir(id);
    }
}