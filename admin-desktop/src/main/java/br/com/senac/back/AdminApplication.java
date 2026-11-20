package br.com.senac.back;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AdminApplication.class.getResource(Telas.LOGIN));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("BiblioTech - Administração");
        stage.setScene(scene);
        stage.show();
    }
}
