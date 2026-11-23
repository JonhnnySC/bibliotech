package br.com.senac.bibliotech.application.port;

public interface SenhaPort {
    String codificar(String senha);
    boolean confere(String senha, String hash);
}
