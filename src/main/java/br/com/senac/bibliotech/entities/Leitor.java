package br.com.senac.bibliotech.entities;

import br.com.senac.bibliotech.enums.EnumGeneroLiterario;
import br.com.senac.bibliotech.enums.EnumPerfil;
import br.com.senac.bibliotech.enums.EnumStatusUsuario;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
// @Table(name = "Leitor") = removido
@Getter
@Setter
@SuperBuilder //aparentemente isso é bom pra quando tem herança
/* ---explicacao---
o @Builder normal do Lombok não lida bem com superclasses.
Se "Leitor extends Usuario" e você usa @Builder comum em ambos,
o builder gerado pra Leitor só enxerga os campos da própria classe
(id, nome, etc.) — ele não sabe construir os campos herdados de Usuario
(tipo email).
 */
@NoArgsConstructor
@AllArgsConstructor
public class Leitor extends Usuario {

    private String  nacionalidade;
    // e o historico de livros pegos, e tals, onde fica?
    //r- por que leitor é 1-n com emprestimos
    // historico de emprestimos entra aaqui como @onetomany mapeado pelio leitor

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EnumPerfil perfil = EnumPerfil.LEITOR;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EnumStatusUsuario status = EnumStatusUsuario.ATIVO;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EnumGeneroLiterario genero;

}
