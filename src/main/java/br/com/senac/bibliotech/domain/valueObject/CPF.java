package br.com.senac.bibliotech.domain.valueObject;

import java.util.Objects;


public class CPF {

    private String cpf;

    public CPF() {
        this.cpf = "";
    }

    public CPF(String cpf) {
        if (!isValidCpf(cpf)) {
            throw new IllegalArgumentException("CPF Invalido!");
        }
        this.cpf = cpf.replaceAll("\\D", "");
    }

    public String getCpf() {
        return cpf;
    }

    public static boolean isValidCpf(String cpf) {
        if (cpf == null) {
            return false;
        }

        // Remove tudo o que não for dígito
        cpf = cpf.replaceAll("\\D", "");

        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int digito1 = 11 - (soma % 11);
        if (digito1 > 9) {
            digito1 = 0;
        }
        if (digito1 != Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int digito2 = 11 - (soma % 11);
        if (digito2 > 9) {
            digito2 = 0;
        }

        return digito2 == Character.getNumericValue(cpf.charAt(10));
    }

    @Override
    public String toString() {
        return cpf.replaceAll("\\D", "")
                .replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CPF outro)) return false;
        return Objects.equals(cpf, outro.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }
}
