package br.com.senac.bibliotech.presentation;

import br.com.senac.bibliotech.application.dto.BibliotecaResponse;
import br.com.senac.bibliotech.application.dto.EnderecoCep;
import br.com.senac.bibliotech.application.service.BibliotecaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bibliotecas")
@RequiredArgsConstructor
@Tag(name = "Biblioteca", description = "Dados da biblioteca logada")
public class BibliotecaController {

    private final BibliotecaService service;

    @GetMapping("/minha-biblioteca")
    @Operation(summary = "Minha biblioteca", description = "Retorna os dados da biblioteca logada")
    public BibliotecaResponse minhaBiblioteca() {
        return service.buscarMinhaBiblioteca();
    }

    @GetMapping("/minha-biblioteca")
    @Operation(summary = "Atualizar minha biblioteca", description = "Atualiza os dados da biblioteca logada")
    public BibliotecaResponse atualizarMinhaBiblioteca(@Valid @RequestBody MinhaBibliotecaRequest request) {
        return service.atualizarMinhaBiblioteca(request);
    }

    @GetMapping("/cep/{cep}")
    @Operation(summary = "Consultar cep", description = "Consulta o endereço do cep informado")
    public EnderecoCep consultarCep(@PathVariable String cep) {
        return service.consultarCep(cep);
    }

    // nao esixte post e get por que a bilbioteca so nasce no bootstrap, e listar todas entregaria a lista de bibliotecas ou seja, vazam,ento, e como é multi empresas
    //o controller so tem 3 ends todos apenas da biblioteca logada


}
