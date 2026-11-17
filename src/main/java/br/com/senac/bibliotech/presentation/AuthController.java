package br.com.senac.bibliotech.presentation;

import br.com.senac.bibliotech.application.dto.LoginRequest;
import br.com.senac.bibliotech.application.dto.LoginResponse;
import br.com.senac.bibliotech.application.dto.UsuarioRequest;
import br.com.senac.bibliotech.application.dto.UsuarioResponse;
import br.com.senac.bibliotech.application.service.AuthService;
import br.com.senac.bibliotech.application.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT) //hm
            //oque é request header??????
    public void logout(@RequestHeader("Authorization") String cabecalho) {
        //oq é substriung?
        authService.logout(cabecalho.substring("Bearer ".length())); // problema, se nao deixar espaço noo bearer, o findybytoken nao acha a linha por que bearewr sao 7 charachteres do jwtfdilter
                                                                    //header malformado sem bnearwer cai como anônimo e o Security devolve 401 antes de chegar no controler.

    }


}
