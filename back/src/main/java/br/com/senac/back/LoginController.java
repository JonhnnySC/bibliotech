package br.com.senac.back;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    private String key = "asiudhakjshdkjahsdk09oq";

    @FXML
    private TextField txtKey;

    @FXML
    protected void onLoginButtonClick(actionEvent event) throws IOException {

        if (txtKey.getText().equals(key)) {
            showMessage(Alert.AlertType.INFORMATION, "Login Efetuado com sucesso!");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/senac/back/menu-view.fxml"));

            Scene scene = new Scene(loader.load());
            Stage stage = new Stage() ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);

        }else {
            showMessage(Alert.AlertType.ERROR, "Erro ao Efetuar login!");
        }
    }
    private void showMessage(Alert.AlertType type, String msg){
        Alert alerta =new Alert(type);
        alerta.setTitle("Mensagem do Sistema!");
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }
}
