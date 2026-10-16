package br.com.senac.bibliotech.domain.valueObject;
//NÃO É UMA UMA TABELA, SPRING IGNORA
public class CPF {

    private String cpf;

    public CPF() {
        this.cpf = "";
    }

    public CPF(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public String toString() {
        return cpf.replaceAll("\\D", "")
                .replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}
