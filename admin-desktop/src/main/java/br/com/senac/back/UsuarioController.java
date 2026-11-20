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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsuarioController {

    private static final String PATH_BOOTSTRAP = "/auth/bootstrap";
    private static final Pattern DETAIL =
            Pattern.compile("\"detail\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @FXML private TextField txtEmpresaNome;
    @FXML private TextField txtCnpj;
    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private TextField txtCpf;
    @FXML private PasswordField txtSenha;
    @FXML private Button btnSalvar;

    @FXML
    public void salvarUsuario(ActionEvent event) {

        String empresaNome = txtEmpresaNome.getText().trim();
        String cnpj = txtCnpj.getText().replaceAll("\\D", ""); // só dígitos
        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String cpf = txtCpf.getText().replaceAll("\\D", "");
        String senha = txtSenha.getText();

        if (empresaNome.isEmpty()) {
            showMessage(Alert.AlertType.WARNING, "Informe o nome da empresa.");
            return;
        }
        if (cnpj.length() != 14) {
            showMessage(Alert.AlertType.WARNING, "O CNPJ deve ter 14 dígitos.");
            return;
        }
        if (nome.isEmpty()) {
            showMessage(Alert.AlertType.WARNING, "Informe o nome do administrador.");
            return;
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            showMessage(Alert.AlertType.WARNING, "E-mail inválido.");
            return;
        }
        if (cpf.length() != 11) {
            showMessage(Alert.AlertType.WARNING, "O CPF deve ter 11 dígitos.");
            return;
        }
        if (senha.length() < 8 || senha.length() > 72) {
            showMessage(Alert.AlertType.WARNING, "A senha deve ter entre 8 e 72 caracteres.");
            return;
        }

        String json = "{"
                + "\"empresaNome\":" + aspas(empresaNome) + ","
                + "\"empresaCnpj\":" + aspas(cnpj) + ","
                + "\"adminNome\":" + aspas(nome) + ","
                + "\"adminEmail\":" + aspas(email) + ","
                + "\"adminCpf\":" + aspas(cpf) + ","
                + "\"adminSenha\":" + aspas(senha) + "}";

        // Chamada de rede fora da thread da interface, senão a janela congela.
        Task<HttpResponse<String>> tarefa = new Task<>() {
            @Override
            protected HttpResponse<String> call() throws Exception {
                HttpRequest request = HttpRequest.newBuilder(URI.create(Config.apiUrl() + PATH_BOOTSTRAP))
                        .timeout(Duration.ofSeconds(10))
                        .header("Content-Type", "application/json; charset=UTF-8")
                        .header("X-Bootstrap-Key", Sessao.chave())
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

            if (status == 201) {
                showMessage(Alert.AlertType.INFORMATION,
                        "Biblioteca " + empresaNome + " criada com o administrador " + nome + ".");
                limparFormulario(); // pronto para criar a 2ª empresa sem reabrir o app
            } else if (status == 403) {
                showMessage(Alert.AlertType.ERROR, "Chave inválida.");
            } else if (status == 409 || status == 400) {
                showMessage(Alert.AlertType.ERROR, detalhe(resposta.body()));
            } else {
                showMessage(Alert.AlertType.ERROR,
                        "Erro inesperado (HTTP " + status + ").\n" + detalhe(resposta.body()));
            }
        });

        tarefa.setOnFailed(e -> {
            btnSalvar.setDisable(false);
            showMessage(Alert.AlertType.ERROR,
                    "Não foi possível falar com a API em " + Config.apiUrl());
        });

        Thread thread = new Thread(tarefa, "chamada-api");
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void voltar(ActionEvent event) throws IOException {
        Telas.trocar((Node) event.getSource(), Telas.LOGIN);
    }

    private void limparFormulario() {
        txtEmpresaNome.clear();
        txtCnpj.clear();
        txtNome.clear();
        txtEmail.clear();
        txtCpf.clear();
        txtSenha.clear();
    }

    // Tira o campo "detail" do JSON de erro da API; se não achar, devolve um resumo do corpo.
    private String detalhe(String corpo) {
        if (corpo == null || corpo.isBlank()) {
            return "Sem detalhes da API.";
        }
        Matcher m = DETAIL.matcher(corpo);
        if (m.find()) {
            return m.group(1).replace("\\\"", "\"").replace("\\n", "\n").replace("\\\\", "\\");
        }
        return corpo.length() > 300 ? corpo.substring(0, 300) + "..." : corpo;
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

    private void showMessage(Alert.AlertType type, String msg) {
        Alert alerta = new Alert(type);
        alerta.setTitle("Mensagem do Sistema!");
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }
}