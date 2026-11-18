// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
import static org.junit.jupiter.api.Assertions.*;

import dominio.Medicao;
import dominio.TipoSensor;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

class MedicaoTest {

    @Test
    void medicaoValidaGuardaOsDadosInformados() {
        // Arrange + Act
        Medicao medicao = new Medicao("CHUVA-01", TipoSensor.CHUVA, 42.5);

        // Assert
        assertEquals("CHUVA-01", medicao.getSensorId());
        assertEquals(42.5, medicao.getValor());
        assertNotNull(medicao.getDataHora());
    }

    @Test
    void medicaoDeChuvaNegativaLancaDadosInvalidos() {
        // Arrange
        double valorNegativo = -5.0;

        // Act
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> new Medicao("CHUVA-01", TipoSensor.CHUVA, valorNegativo));

        // Assert
        assertTrue(erro.getMessage().contains("chuva"));
    }

    @Test
    void umidadeAcimaDeCemPorCentoLancaDadosInvalidos() {
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao("SOLO-01", TipoSensor.UMIDADE_SOLO, 120));
    }

    @Test
    void valorNaoNumericoLancaDadosInvalidos() {
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao("NIVEL-01", TipoSensor.NIVEL_CORREGO, Double.NaN));
    }

    @Test
    void medicaoSemSensorLancaDadosInvalidos() {
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao(" ", TipoSensor.CHUVA, 10));
    }
}
