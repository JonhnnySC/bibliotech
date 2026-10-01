package br.com.senac.bibliotech.controllers;

import br.com.senac.bibliotech.dto.LoginRequest;
import br.com.senac.bibliotech.dto.LoginResponse;
import br.com.senac.bibliotech.dto.UsuarioRequest;
import br.com.senac.bibliotech.dto.UsuarioResponse;
import br.com.senac.bibliotech.service.AuthService;
import br.com.senac.bibliotech.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Login e obtencao do token JWT")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Validação das credenciais e retorno")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login Realizado e token retornado"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody UsuarioRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastrar(req));
    }


}
