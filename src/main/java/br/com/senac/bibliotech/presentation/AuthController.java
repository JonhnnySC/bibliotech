package br.com.senac.bibliotech.presentation;

import br.com.senac.bibliotech.application.dto.BootstrapRequest;
import br.com.senac.bibliotech.application.dto.BootstrapResponse;
import br.com.senac.bibliotech.application.dto.LoginRequest;
import br.com.senac.bibliotech.application.dto.LoginResponse;
import br.com.senac.bibliotech.application.service.AuthService;
import br.com.senac.bibliotech.application.service.BootstrapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Login, logout e primeira configuração do sistema")
public class AuthController {

    private final AuthService authService;
    private final BootstrapService bootstrapService;

    public AuthController(AuthService authService, BootstrapService bootstrapService) {
        this.authService = authService;
        this.bootstrapService = bootstrapService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login",
            description = "Valida as credenciais e devolve token, vencimento e os dados públicos do usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado e token retornado"),
            @ApiResponse(responseCode = "400", description = "Corpo inválido"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout",
            description = "Revoga no banco o token usado na requisição. Rota protegida: exige o próprio token.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Token revogado"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader("Authorization") String cabecalho) {
        // "Bearer " tem 7 caracteres, contando o espaco: sem o espaco sobraria
        // um espaco antes do token e o findTokenByToken nao acharia a linha.
        authService.logout(cabecalho.substring("Bearer ".length()));
    }

    @PostMapping("/bootstrap")
    @Operation(summary = "Primeira configuração (app JavaFX)",
            description = "Cria a biblioteca e o primeiro ADMINISTRADOR. Público, mas exige o cabeçalho X-Bootstrap-Key com a chave do .env. Chave vazia no servidor = endpoint desligado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Biblioteca e administrador criados"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Chave de bootstrap inválida ou não configurada"),
            @ApiResponse(responseCode = "409", description = "Email, CPF ou CNPJ já cadastrado")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public BootstrapResponse bootstrap(
            @RequestHeader(value = "X-Bootstrap-Key", required = false) String chave,
            @Valid @RequestBody BootstrapRequest request) {
        // required = false de proposito: sem o cabecalho a resposta e 403 (chave invalida),
        // nao 400 — quem nao conhece a chave nao descobre nada sobre o sistema.
        return bootstrapService.provisionar(chave, request);
    }



}