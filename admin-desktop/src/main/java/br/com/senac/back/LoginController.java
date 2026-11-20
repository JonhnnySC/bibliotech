package br.com.senac.back;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;

import java.io.IOException;

public class LoginController {

    @FXML
    private PasswordField txtKey;

    @FXML
    protected void onLoginButtonClick(ActionEvent event) throws IOException {
        if (txtKey.getText().isBlank()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Mensagem do Sistema!");
            alerta.setHeaderText(null);
            alerta.setContentText("Informe a chave.");
            alerta.showAndWait();
            return;
        }
        Sessao.setChave(txtKey.getText());
        Telas.trocar((Node) event.getSource(), Telas.CADASTRO_USUARIO);
    }
}