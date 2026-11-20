package br.com.senac.back;

/*
 *   BIBLIOTECH_API_URL -> URL da API (padrão: http://localhost:8080)
 */
final class Config {

    private Config() {
    }

    static String apiUrl() {
        String valor = System.getenv("BIBLIOTECH_API_URL");
        return (valor == null || valor.isBlank()) ? "http://localhost:8080" : valor;
    }
}