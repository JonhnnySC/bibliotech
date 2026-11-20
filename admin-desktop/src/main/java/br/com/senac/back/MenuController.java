package br.com.senac.back;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;

import java.io.IOException;

public class MenuController {

    @FXML
    public void abrirCadastro(ActionEvent event) throws IOException {
        Telas.trocar((Node) event.getSource(), Telas.CADASTRO_USUARIO);
    }

    @FXML
    public void sair() {
        Platform.exit();
    }
}
