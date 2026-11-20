package br.com.senac.back;

/** Guarda a chave digitada na tela 1 até o envio na tela 2. */
final class Sessao {

    private static String chave = "";

    private Sessao() {
    }

    static void setChave(String valor) {
        chave = valor == null ? "" : valor;
    }

    static String chave() {
        return chave;
    }
}