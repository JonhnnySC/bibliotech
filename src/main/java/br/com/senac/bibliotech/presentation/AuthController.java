package br.com.senac.bibliotech.presentation;

import br.com.senac.bibliotech.application.dto.*;
import br.com.senac.bibliotech.application.service.AuthService;
import br.com.senac.bibliotech.application.service.BootstrapService;
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
    private final BootstrapService bootstrapService;

    public AuthController(AuthService authService, UsuarioService usuarioService, BootstrapService bootstrapService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
        this.bootstrapService = bootstrapService;
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

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT) //hm
            //oque é request header?????? r - le um cabecalho http da req, o xboottrapkey é o que o desktopmanda no  header. com required false, a fatoda do header vira null e cai no 4-3 enves do 40 altiomatico
    public void logout(@RequestHeader("Authorization") String cabecalho) {
        //oq é substriung? r - vearer tem 7 chars, contando o espaço, o subtring descarta o prefixo e deixa so o token
        authService.logout(cabecalho.substring("Bearer ".length())); // problema, se nao deixar espaço noo bearer, o findybytoken nao acha a linha por que bearewr sao 7 charachteres do jwtfdilter
                                                                    //header malformado sem bnearwer cai como anônimo e o Security devolve 401 antes de chegar no controler.

    }
    // adiioibnar boothservice pra ser redundante

    @PostMapping("/bootstrap")
    @ResponseStatus(HttpStatus.CREATED)
    public BootstrapResponse bootstrap(
            @RequestHeader(value = "X-Bootstrap-Key", required = false) String chave,
            @Valid @RequestBody BootstrapRequest request) {
        return bootstrapService.provisionar(chave, request);
    }


}
