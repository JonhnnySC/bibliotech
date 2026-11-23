package br.com.senac.bibliotech.presentation;

import br.com.senac.bibliotech.domain.enums.EnumGeneroLiterario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/* Lista fixa do domínio: não depende de usuário logado nem de biblioteca.
   É o endpoint usado para demonstrar a segurança ligada/desligada. */
@RestController
@RequestMapping("/generos")
@Tag(name = "Gêneros", description = "Gêneros literários disponíveis")
public class GeneroController {

    @GetMapping
    @Operation(summary = "Listar gêneros literários")
    public List<EnumGeneroLiterario> listar() {
        return List.of(EnumGeneroLiterario.values());
    }
}