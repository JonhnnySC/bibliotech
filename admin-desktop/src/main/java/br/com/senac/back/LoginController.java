package br.com.senac.back;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class LoginController {

    @FXML
    private PasswordField txtKey;

    @FXML
    protected void onLoginButtonClick(ActionEvent event) throws IOException {

        String esperada = Config.adminKey();

        if (esperada.isEmpty()) {
            showMessage(Alert.AlertType.ERROR,
                    "Defina a variável de ambiente BIBLIOTECH_ADMIN_KEY antes de abrir o app.");
            return;
        }

        if (chaveConfere(txtKey.getText(), esperada)) {
            Telas.trocar((Node) event.getSource(), Telas.MENU);
        } else {
            showMessage(Alert.AlertType.ERROR, "Chave inválida!");
        }
    }

    // Comparação em tempo constante (não vaza, pelo tempo de resposta, quantos caracteres bateram).
    private boolean chaveConfere(String digitada, String esperada) {
        byte[] a = digitada.getBytes(StandardCharsets.UTF_8);
        byte[] b = esperada.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(a, b);
    }

    private void showMessage(Alert.AlertType type, String msg) {
        Alert alerta = new Alert(type);
        alerta.setTitle("Mensagem do Sistema!");
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }
}
