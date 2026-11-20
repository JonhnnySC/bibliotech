package br.com.senac.back;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class UsuarioController {

    /*
     * ATENÇÃO: esta rota NÃO existe no back atual. O back tem POST /usuarios
     * (exige token de ADMINISTRADOR) e POST /auth/registro. Enquanto a decisão de como
     * criar o primeiro admin (banco vazio) não for tomada, é só aqui que a rota muda.
     */
    private static final String PATH_CADASTRO_ADMIN = "/usuarios/admin";

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtCpf;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private Button btnSalvar;

    @FXML
    public void salvarUsuario(ActionEvent event) {

        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String senha = txtSenha.getText();
        String cpf = txtCpf.getText().replaceAll("\\D", ""); // só dígitos, como a API exige

        // Mesmas regras do UsuarioRequest do back
        if (nome.isEmpty()) {
            showMessage(Alert.AlertType.WARNING, "Informe o nome.");
            return;
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            showMessage(Alert.AlertType.WARNING, "E-mail inválido.");
            return;
        }
        if (senha.length() < 8 || senha.length() > 72) {
            showMessage(Alert.AlertType.WARNING, "A senha deve ter entre 8 e 72 caracteres.");
            return;
        }
        if (cpf.length() != 11) {
            showMessage(Alert.AlertType.WARNING, "O CPF deve ter 11 dígitos.");
            return;
        }

        String json = montarJson(nome, email, senha, cpf);
        Node origem = (Node) event.getSource();

        // Chamada de rede fora da thread da interface, senão a janela congela.
        Task<HttpResponse<String>> tarefa = new Task<>() {
            @Override
            protected HttpResponse<String> call() throws Exception {
                HttpRequest request = HttpRequest.newBuilder(URI.create(Config.apiUrl() + PATH_CADASTRO_ADMIN))
                        .timeout(Duration.ofSeconds(10))
                        .header("Content-Type", "application/json; charset=UTF-8")
                        .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                        .build();
                return HTTP.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            }
        };

        tarefa.setOnRunning(e -> btnSalvar.setDisable(true));

        tarefa.setOnSucceeded(e -> {
            btnSalvar.setDisable(false);
            HttpResponse<String> resposta = tarefa.getValue();
            int status = resposta.statusCode();

            if (status >= 200 && status < 300) { // aceita 200 e 201
                showMessage(Alert.AlertType.INFORMATION, "Usuário salvo com sucesso!");
                try {
                    Telas.trocar(origem, Telas.MENU);
                } catch (IOException ex) {
                    showMessage(Alert.AlertType.ERROR, "Não foi possível voltar ao menu: " + ex.getMessage());
                }
            } else {
                showMessage(Alert.AlertType.ERROR,
                        "Erro ao salvar usuário (HTTP " + status + ").\n" + resumo(resposta.body()));
            }
        });

        tarefa.setOnFailed(e -> {
            btnSalvar.setDisable(false);
            showMessage(Alert.AlertType.ERROR,
                    "Não foi possível falar com a API em " + Config.apiUrl() + "\n"
                            + tarefa.getException().getMessage());
        });

        Thread thread = new Thread(tarefa, "chamada-api");
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void voltar(ActionEvent event) throws IOException {
        Telas.trocar((Node) event.getSource(), Telas.MENU);
    }

    private String montarJson(String nome, String email, String senha, String cpf) {
        StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"nome\":").append(aspas(nome)).append(",")
                .append("\"email\":").append(aspas(email)).append(",")
                .append("\"senha\":").append(aspas(senha)).append(",")
                .append("\"cpf\":").append(aspas(cpf));

        String chave = Config.bootstrapKey();
        if (!chave.isEmpty()) {
            sb.append(",\"secretKey\":").append(aspas(chave));
        }
        return sb.append("}").toString();
    }

    // Escapa o texto para JSON (aspas, barra invertida e caracteres de controle).
    private String aspas(String texto) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : texto.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.append("\"").toString();
    }

    private String resumo(String corpo) {
        if (corpo == null || corpo.isBlank()) {
            return "";
        }
        return corpo.length() > 300 ? corpo.substring(0, 300) + "..." : corpo;
    }

    private void showMessage(Alert.AlertType type, String msg) {
        Alert alerta = new Alert(type);
        alerta.setTitle("Mensagem do Sistema!");
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }
}
