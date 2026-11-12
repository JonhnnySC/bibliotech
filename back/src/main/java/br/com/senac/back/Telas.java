package br.com.senac.back;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/*
 * Troca de tela num lugar só. Antes cada controller repetia o caminho do FXML
 * (e dois deles apontavam para o pacote antigo com.example.back).
 */
final class Telas {

    static final String LOGIN = "login-view.fxml";
    static final String MENU = "menu-view.fxml";
    static final String CADASTRO_USUARIO = "cadastro-usuario-view.fxml";

    private Telas() {
    }

    /** Troca a cena da janela que contém o nó de origem (ex.: o botão clicado). */
    static void trocar(Node origem, String fxml) throws IOException {
        // getResource relativo à classe Telas -> mesma pasta br/com/senac/back/
        FXMLLoader loader = new FXMLLoader(Telas.class.getResource(fxml));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) origem.getScene().getWindow();
        stage.setScene(scene);
        stage.sizeToScene();
    }
}
