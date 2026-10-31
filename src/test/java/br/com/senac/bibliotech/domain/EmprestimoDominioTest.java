package br.com.senac.bibliotech.domain;

import br.com.senac.bibliotech.domain.entities.Emprestimo;
import br.com.senac.bibliotech.domain.entities.Exemplar;
import br.com.senac.bibliotech.domain.entities.Leitor;
import br.com.senac.bibliotech.domain.enums.EnumStatusEmprestimo;
import br.com.senac.bibliotech.domain.enums.EnumStatusExemplar;
import br.com.senac.bibliotech.domain.exception.ConflitoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Teste do domínio puro: sem Spring e sem banco (a vantagem de a regra morar na entidade)
class EmprestimoDominioTest {

    private Exemplar exemplar(EnumStatusExemplar status) {
        Exemplar exemplar = new Exemplar();
        exemplar.setStatus(status);
        return exemplar;
    }

    @Test
    void emprestarReservaOExemplarEDefineOPrazo() {
        Exemplar exemplar = exemplar(EnumStatusExemplar.DISPONIVEL);

        Emprestimo emprestimo = Emprestimo.registrar(new Leitor(), exemplar, new Leitor());

        assertEquals(EnumStatusExemplar.EMPRESTADO, exemplar.getStatus());
        assertEquals(EnumStatusEmprestimo.ATIVO, emprestimo.getStatusEmprestimo());
        assertEquals(emprestimo.getDataEmprestimo().plusDays(Emprestimo.PRAZO_PADRAO_DIAS),
                emprestimo.getDataPrevistaDevolucao());
    }

    @Test
    void naoEmprestaExemplarIndisponivel() {
        Exemplar exemplar = exemplar(EnumStatusExemplar.EMPRESTADO);

        assertThrows(ConflitoException.class,
                () -> Emprestimo.registrar(new Leitor(), exemplar, new Leitor()));
    }

    @Test
    void devolverLiberaOExemplarENaoPermiteDevolverDuasVezes() {
        Exemplar exemplar = exemplar(EnumStatusExemplar.DISPONIVEL);
        Emprestimo emprestimo = Emprestimo.registrar(new Leitor(), exemplar, new Leitor());

        emprestimo.registrarDevolucao();

        assertEquals(EnumStatusEmprestimo.DEVOLVIDO, emprestimo.getStatusEmprestimo());
        assertNotNull(emprestimo.getDataDevolucao());
        assertEquals(EnumStatusExemplar.DISPONIVEL, exemplar.getStatus());
        assertThrows(ConflitoException.class, emprestimo::registrarDevolucao);
    }
}
