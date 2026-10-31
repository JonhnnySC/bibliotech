package br.com.senac.bibliotech.domain;

import br.com.senac.bibliotech.domain.valueobject.CPF;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CPFTest {

    @Test
    void aceitaCpfValidoComOuSemMascara() {
        assertEquals("52998224725", new CPF("529.982.247-25").getCpf());
        assertEquals("12345678909", new CPF("12345678909").getCpf());
    }

    @Test
    void recusaDigitosRepetidosDigitoVerificadorErradoENulo() {
        assertThrows(IllegalArgumentException.class, () -> new CPF("11111111111"));
        assertThrows(IllegalArgumentException.class, () -> new CPF("52998224724"));
        assertThrows(IllegalArgumentException.class, () -> new CPF(null));
    }

    @Test
    void formataComMascaraNoToString() {
        assertEquals("529.982.247-25", new CPF("52998224725").toString());
    }
}
