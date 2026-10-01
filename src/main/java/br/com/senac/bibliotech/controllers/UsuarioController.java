package br.com.senac.bibliotech.controllers;

import br.com.senac.bibliotech.dto.AlterarSenhaRequest;
import br.com.senac.bibliotech.dto.AtualizarPerfilRequest;
import br.com.senac.bibliotech.dto.AtualizarStatusRequest;
import br.com.senac.bibliotech.dto.AtualizarUsuarioRequest;
import br.com.senac.bibliotech.dto.UsuarioRequest;
import br.com.senac.bibliotech.dto.UsuarioResponse;
import br.com.senac.bibliotech.service.UsuarioService;
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
 *Controller FINO: recebe, valida o formato (@Valid), delega ao service e devolve o
  status certo. Nenhum if de negócio, nenhum orElse(null), nenhum try/catch: as falhas
  viram exceção e o ApiExceptionHandler converte em 404/409/400.

  POR QUE PATCHes separados senha, status, perfil em vez de um PUT gigante?

  Porque a PERMISSÃO é diferente para cada intenção.
  *
  * Um PUT que aceita tudo obriga
 a checar campo por campo quem pode mexer em quê, e é fácil errar.
 PUT x PATCH: PUT substitui o recurso inteiro (aqui: os dados cadastrais);
 PATCH altera uma parte (uma "intenção").
 */
@RestController
@RequestMapping("/usuarios") // plural: a URL nomeia a coleção; o verbo HTTP é a ação
@Tag(name = "Usuários", description = "Gerenciamento completo de usuários: cadastro, autenticação, permissões e ciclo de vida")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @Operation(
            summary = "Cadastrar novo usuário",
            description = "Cria um novo usuário no sistema. O email e CPF devem ser únicos. O usuário é criado com status ATIVO e perfil LEITOR por padrão."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(schema = @Schema(implementation = UsuarioResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos: email mal formatado, senha fraca, campos obrigatórios faltando"
            ),
            @ApiResponse(responseCode = "409", description = "Conflito: email ou CPF já cadastrado no sistema"
            )
    })
    public ResponseEntity<UsuarioResponse> cadastrar(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do novo usuário",
                    required = true
            )
            UsuarioRequest request
    ) {
        UsuarioResponse criado = usuarioService.cadastrar(request);
        return ResponseEntity.created(URI.create("/usuarios/" + criado.id())).body(criado);
    }

    @GetMapping
    @Operation(
            summary = "Listar todos os usuários ativos",
            description = "Retorna lista de todos os usuários que não estão com status EXCLUIDO. Requer autenticação."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = UsuarioResponse[].class))
            ),
            @ApiResponse(responseCode = "401", description = "Não autenticado - token JWT ausente ou inválido")
    })
    @SecurityRequirement(name = "bearerAuth")
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar usuário por ID",
            description = "Retorna os dados completos de um usuário específico pelo seu ID único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado e retornado",
                    content = @Content(schema = @Schema(implementation = UsuarioResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado ou excluído")
    })
    @SecurityRequirement(name = "bearerAuth")
    public UsuarioResponse buscar(
            @Parameter(description = "ID único do usuário", required = true, example = "1")
            @PathVariable Long id
    ) {
        return usuarioService.buscar(id);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar dados cadastrais do usuário",
            description = "Atualiza nome, email e CPF do usuário. Substitui os dados cadastrais completos (PUT = substituição total). O usuário só pode atualizar seus próprios dados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados atualizados com sucesso",
                    content = @Content(schema = @Schema(implementation = UsuarioResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos no corpo da requisição"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Proibido - tentando atualizar usuário que não é o seu"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Email ou CPF já está em uso por outro usuário")
    })
    @SecurityRequirement(name = "bearerAuth")
    public UsuarioResponse atualizar(
            @Parameter(description = "ID do usuário a ser atualizado", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AtualizarUsuarioRequest request
    ) {
        return usuarioService.atualizar(id, request);
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Atualizar status do usuário (apenas ADMIN)",
            description = "Altera o status do usuário para ATIVO, BLOQUEADO ou INATIVO. Usuário bloqueado não consegue fazer login. Requer perfil de ADMINISTRADOR."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = UsuarioResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Status inválido (deve ser ATIVO, BLOQUEADO ou INATIVO)"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Proibido - requer perfil ADMIN"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public UsuarioResponse atualizarStatus(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusRequest request
    ) {
        return usuarioService.atualizarStatus(id, request.status());
    }

    @PatchMapping("/{id}/perfil")
    @Operation(summary = "Atualizar perfil/permissões do usuário (apenas ADMIN)", description = "Promove ou rebaixa o usuário alterando seu perfil (LEITOR, BIBLIOTECARIO, ADMINISTRADOR). Esta é a operação mais sensível do sistema pois controla permissões. Requer perfil de ADMINISTRADOR."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = UsuarioResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Perfil inválido"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Proibido - requer perfil ADMIN"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public UsuarioResponse atualizarPerfil(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AtualizarPerfilRequest request
    ) {
        return usuarioService.atualizarPerfil(id, request.perfil());
    }

    @PatchMapping("/{id}/senha")
    @Operation(
            summary = "Alterar senha do usuário",
            description = "Altera a senha do usuário. Requer informar a senha atual corretamente e a nova senha. O usuário só pode alterar sua própria senha. A resposta é 204 No Content por segurança (nunca devolve senha ou hash)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso (sem conteúdo na resposta)"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou nova senha não atende requisitos de segurança"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Proibido - tentando alterar senha de outro usuário"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarSenha(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AlterarSenhaRequest request
    ) {
        usuarioService.alterarSenha(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Excluir usuário (soft delete)",
            description = "Marca o usuário como EXCLUIDO (soft delete). O usuário não é removido do banco, apenas fica invisível nas listagens e não pode mais fazer login. Requer autenticação."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso (sem conteúdo)"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Proibido - sem permissão para excluir"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado ou já excluído")
    })
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(
            @Parameter(description = "ID do usuário a ser excluído", required = true, example = "1")
            @PathVariable Long id
    ) {
        usuarioService.excluir(id);
    }
}