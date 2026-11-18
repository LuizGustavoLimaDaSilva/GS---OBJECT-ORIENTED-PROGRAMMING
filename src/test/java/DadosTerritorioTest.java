// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
import static org.junit.jupiter.api.Assertions.*;

import dominio.DadosTerritorio;
import dominio.NivelAlerta;
import dominio.TipoSensor;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

class DadosTerritorioTest {

    @Test
    void populacaoNegativaLancaDadosInvalidos() {
        DadosTerritorio dados = new DadosTerritorio("Bacia", "Zona Leste", 1000);

        assertThrows(DadosInvalidosException.class, () -> dados.setPopulacao(-10));
        assertEquals(1000, dados.getPopulacao());
    }

    @Test
    void limiaresForaDeOrdemLancamDadosInvalidos() {
        DadosTerritorio dados = new DadosTerritorio("Bacia", "Zona Leste", 1000);

        assertThrows(DadosInvalidosException.class,
                () -> dados.setLimiares(TipoSensor.CHUVA, 60, 40, 80));
    }

    @Test
    void limiaresValidosPassamAClassificarOsValores() {
        // Arrange
        DadosTerritorio dados = new DadosTerritorio("Bacia", "Zona Leste", 1000);

        // Act
        dados.setLimiares(TipoSensor.CHUVA, 10, 20, 30);

        // Assert
        assertEquals(NivelAlerta.ALERTA, dados.getLimiares(TipoSensor.CHUVA).classificar(25).orElseThrow());
        assertTrue(dados.getLimiares(TipoSensor.CHUVA).classificar(5).isEmpty());
    }
}
