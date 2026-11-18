// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
import static org.junit.jupiter.api.Assertions.*;

import dominio.EstrategiaPrevisao;
import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import excecao.DadosInvalidosException;
import java.util.List;
import org.junit.jupiter.api.Test;

class PrevisaoMediaMovelTest {

    @Test
    void calculaMediaDasUltimasTresLeituras() {
        // Arrange
        EstrategiaPrevisao estrategia = new PrevisaoMediaMovel(3);
        List<Double> historico = List.of(10.0, 20.0, 30.0, 40.0, 50.0);

        // Act
        double previsto = estrategia.prever(historico);

        // Assert (30 + 40 + 50) / 3
        assertEquals(40.0, previsto, 0.0001);
    }

    @Test
    void janelaMaiorQueOHistoricoUsaTodasAsLeituras() {
        EstrategiaPrevisao estrategia = new PrevisaoMediaMovel(10);

        double previsto = estrategia.prever(List.of(2.0, 4.0, 6.0, 8.0, 10.0));

        assertEquals(6.0, previsto, 0.0001);
    }

    @Test
    void historicoComMenosDeCincoLeiturasLancaDadosInvalidos() {
        EstrategiaPrevisao estrategia = new PrevisaoMediaMovel(3);

        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> estrategia.prever(List.of(1.0, 2.0, 3.0)));

        assertTrue(erro.getMessage().contains("insuficiente"));
    }

    @Test
    void comparacaoProjetaTendenciaDeAlta() {
        // Arrange: média anterior 10, média recente 20 -> projeta 30
        EstrategiaPrevisao estrategia = new PrevisaoComparacao(3);

        double previsto = estrategia.prever(List.of(10.0, 10.0, 10.0, 20.0, 20.0, 20.0));

        assertEquals(30.0, previsto, 0.0001);
    }

    @Test
    void comparacaoNaoPrevaValorNegativoEmQueda() {
        EstrategiaPrevisao estrategia = new PrevisaoComparacao(3);

        double previsto = estrategia.prever(List.of(30.0, 30.0, 30.0, 10.0, 10.0, 10.0));

        assertEquals(0.0, previsto, 0.0001);
    }

    @Test
    void estrategiasDiferentesDaoResultadosDiferentesParaOMesmoHistorico() {
        // Arrange: polimorfismo - mesma chamada, comportamentos distintos
        List<Double> historico = List.of(10.0, 10.0, 10.0, 20.0, 20.0, 20.0);
        List<EstrategiaPrevisao> estrategias = List.of(new PrevisaoMediaMovel(3), new PrevisaoComparacao(3));

        // Act
        double primeira = estrategias.get(0).prever(historico);
        double segunda = estrategias.get(1).prever(historico);

        // Assert
        assertNotEquals(primeira, segunda);
    }
}
