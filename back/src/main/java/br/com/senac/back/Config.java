package br.com.senac.back;

/*
 * Configuração lida de variáveis de ambiente, para não deixar segredo no código
 * (o repositório é público).
 *
 *   BIBLIOTECH_API_URL       -> URL da API (padrão: http://localhost:8080)
 *   BIBLIOTECH_ADMIN_KEY     -> "chave" pedida na tela de login do desktop
 *   BIBLIOTECH_BOOTSTRAP_KEY -> chave enviada no cadastro do admin (campo secretKey)
 *
 * No IntelliJ: Run > Edit Configurations > Environment variables.
 */
final class Config {

    private Config() {
    }

    static String apiUrl() {
        return env("BIBLIOTECH_API_URL", "http://localhost:8080");
    }

    static String adminKey() {
        return env("BIBLIOTECH_ADMIN_KEY", "");
    }

    static String bootstrapKey() {
        return env("BIBLIOTECH_BOOTSTRAP_KEY", "");
    }

    private static String env(String nome, String padrao) {
        String valor = System.getenv(nome);
        return (valor == null || valor.isBlank()) ? padrao : valor;
    }
}
