module br.com.senac.back {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http; // HttpClient usado no UsuarioController

    opens br.com.senac.back to javafx.fxml;
    exports br.com.senac.back;
}
