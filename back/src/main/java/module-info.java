module br.com.senac.back {
    requires javafx.controls;
    requires javafx.fxml;


    opens br.com.senac.back to javafx.fxml;
    exports br.com.senac.back;
}