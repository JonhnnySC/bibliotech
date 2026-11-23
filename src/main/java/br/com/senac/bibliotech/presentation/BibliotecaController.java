package br.com.senac.bibliotech.presentation;

import br.com.senac.bibliotech.application.dto.BibliotecaRequest;
import br.com.senac.bibliotech.application.dto.BibliotecaResponse;
import br.com.senac.bibliotech.application.dto.EnderecoCep;
import br.com.senac.bibliotech.application.service.BibliotecaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Nao existe POST nem GET de colecao: a biblioteca so nasce no bootstrap, e
 * listar todas as bibliotecas vazaria os outros tenants. Os tres endpoints
 * trabalham apenas sobre a biblioteca do token.
 */
@RestController
@RequestMapping("/bibliotecas")
@RequiredArgsConstructor
@Tag(name = "Biblioteca", description = "Dados e endereço da biblioteca logada")
@SecurityRequirement(name = "bearerAuth")
public class BibliotecaController {

    private final BibliotecaService service;

    @GetMapping("/minha-biblioteca")
    @Operation(summary = "Minha biblioteca", description = "Dados da biblioteca do usuário logado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados retornados"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem biblioteca no contexto")
    })
    public BibliotecaResponse minhaBiblioteca() {
        return service.buscarMinhaBiblioteca();
    }

    @PutMapping("/minha-biblioteca")
    @Operation(summary = "Atualizar minha biblioteca (apenas ADMIN)",
            description = "Atualiza nome, número e complemento. O endereço é reconsultado no ViaCEP pelo CEP informado — o cliente nunca grava logradouro, bairro, cidade ou UF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Biblioteca atualizada"),
            @ApiResponse(responseCode = "400", description = "CEP malformado ou dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Requer ADMINISTRADOR"),
            @ApiResponse(responseCode = "404", description = "CEP não encontrado"),
            @ApiResponse(responseCode = "503", description = "ViaCEP indisponível")
    })
    public BibliotecaResponse atualizarMinhaBiblioteca(@Valid @RequestBody BibliotecaRequest request) {
        return service.atualizarMinhaBiblioteca(request);
    }

    @GetMapping("/cep/{cep}")
    @Operation(summary = "Consultar CEP",
            description = "Prévia do endereço via ViaCEP, atrás da porta ConsultaCepPort. O front chama ao sair do campo de CEP.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Endereço encontrado"),
            @ApiResponse(responseCode = "400", description = "CEP não tem 8 dígitos"),
            @ApiResponse(responseCode = "404", description = "CEP bem formado mas inexistente"),
            @ApiResponse(responseCode = "503", description = "ViaCEP fora do ar ou timeout")
    })
    public EnderecoCep consultarCep(@PathVariable String cep) {
        return service.consultarCep(cep);
    }
}