package br.com.senac.bibliotech.repository;

import br.com.senac.bibliotech.entities.Usuario;
import br.com.senac.bibliotech.enums.EnumStatusUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Cada método é uma "derived query": o Spring lê o NOME e gera o SQL.
 * Se o nome citar uma propriedade que não existe em Usuario, a app nem sobe
 * (falha rápida, o que é bom).
 *
 * REMOVIDO: existsUsuarioByEmailAndSenha. Comparar senha dentro do SQL não funciona
 * com BCrypt (cada hash tem um salt diferente) e incentivava guardar senha em texto puro.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Login: devolve o usuário. A senha é conferida no AuthService, com
    // passwordEncoder.matches(...). Optional porque existe zero ou um usuário por email.
    Optional<Usuario> findByEmail(String email);

    // Cadastro: dá um erro amigável (409) antes de bater no UNIQUE do banco.
    boolean existsByEmail(String email);

    // Atualização: "existe OUTRO usuário com este email?" (o próprio não conta).
    // Sem o "IdNot", o usuário não conseguiria salvar o cadastro sem trocar o email.
    // Gera: where email = ? and id <> ?
    boolean existsByEmailAndIdNot(String email, Long id);

    // Todos exceto o status informado (ex.: listar tudo menos os EXCLUIDO).
    // List, e não Optional<List>: coleção vazia já significa "nada encontrado".
    List<Usuario> findByStatusNot(EnumStatusUsuario status);
}