// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dominio.Alerta;
import dominio.Medicao;
import dominio.NivelAlerta;
import dominio.PrevisaoMediaMovel;
import dominio.Sensor;
import dominio.TipoSensor;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistencia.AlertaDAO;
import persistencia.MedicaoDAO;
import servico.ConfiguracaoSistema;
import servico.ResultadoPrevisao;
import servico.ServicoMonitoramento;

/** Testa a lógica de alerta sem banco: os DAOs são mocks do Mockito. */
class ServicoMonitoramentoTest {

    private MedicaoDAO medicaoDAO;
    private AlertaDAO alertaDAO;
    private Sensor sensor;
    private ServicoMonitoramento servico;

    @BeforeEach
    void preparar() {
        ConfiguracaoSistema.getInstance().restaurarPadroes();
        medicaoDAO = mock(MedicaoDAO.class);
        alertaDAO = mock(AlertaDAO.class);
        sensor = mock(Sensor.class);
        when(sensor.getId()).thenReturn("CHUVA-01");
        when(sensor.getTipo()).thenReturn(TipoSensor.CHUVA);
        servico = new ServicoMonitoramento(medicaoDAO, alertaDAO, ConfiguracaoSistema.getInstance(), List.of(sensor));
    }

    private List<Medicao> historico(double... valores) {
        List<Medicao> lista = new ArrayList<>();
        for (double v : valores) {
            lista.add(new Medicao(null, "CHUVA-01", TipoSensor.CHUVA, v, LocalDateTime.now()));
        }
        return lista;
    }

    @Test
    void previsaoAcimaDoLimiteCriticoEmiteEGravaAlerta() throws Exception {
        // Arrange: média das 3 últimas = 95 mm/h, acima do crítico (80)
        when(medicaoDAO.buscarUltimasPorSensor(eq("CHUVA-01"), anyInt()))
                .thenReturn(historico(90, 95, 100, 95, 90));

        // Act
        ResultadoPrevisao resultado = servico.prever(sensor, new PrevisaoMediaMovel(3));

        // Assert
        assertTrue(resultado.temAlerta());
        assertEquals(NivelAlerta.CRITICO, resultado.alerta().getNivel());
        assertEquals(95.0, resultado.valorPrevisto(), 0.0001);
        assertTrue(resultado.alerta().getMensagem().contains("CHUVA-01"));
        verify(alertaDAO, times(1)).salvar(any(Alerta.class));
    }

    @Test
    void previsaoBaixaNaoEmiteAlerta() throws Exception {
        when(medicaoDAO.buscarUltimasPorSensor(eq("CHUVA-01"), anyInt()))
                .thenReturn(historico(1, 2, 1, 2, 1));

        ResultadoPrevisao resultado = servico.prever(sensor, new PrevisaoMediaMovel(3));

        assertFalse(resultado.temAlerta());
        verify(alertaDAO, never()).salvar(any(Alerta.class));
    }

    @Test
    void limiaresAlteradosMudamONivelDoAlerta() throws Exception {
        // Arrange: com os limiares padrão, 12 mm/h não alertaria
        ConfiguracaoSistema.getInstance().getDadosTerritorio().setLimiares(TipoSensor.CHUVA, 5, 10, 15);
        when(medicaoDAO.buscarUltimasPorSensor(eq("CHUVA-01"), anyInt()))
                .thenReturn(historico(12, 12, 12, 12, 12));

        // Act
        ResultadoPrevisao resultado = servico.prever(sensor, new PrevisaoMediaMovel(3));

        // Assert
        assertEquals(NivelAlerta.ALERTA, resultado.alerta().getNivel());
    }

    @Test
    void historicoInsuficienteLancaExcecaoENaoGravaAlerta() throws Exception {
        when(medicaoDAO.buscarUltimasPorSensor(eq("CHUVA-01"), anyInt()))
                .thenReturn(historico(90, 95, 100));

        assertThrows(DadosInvalidosException.class, () -> servico.prever(sensor, new PrevisaoMediaMovel(3)));
        verify(alertaDAO, never()).salvar(any(Alerta.class));
    }

    @Test
    void leituraValidaEhGravadaNoBanco() throws Exception {
        when(sensor.lerValor()).thenReturn(12.5);

        Medicao medicao = servico.registrarLeitura(sensor);

        assertEquals(12.5, medicao.getValor(), 0.0001);
        verify(medicaoDAO, times(1)).salvar(medicao);
    }

    @Test
    void leituraInvalidaNaoEhGravada() throws Exception {
        when(sensor.lerValor()).thenReturn(-3.0);

        assertThrows(DadosInvalidosException.class, () -> servico.registrarLeitura(sensor));
        verify(medicaoDAO, never()).salvar(any(Medicao.class));
    }

    @Test
    void sensorIndisponivelPropagaFonteIndisponivelException() throws Exception {
        when(sensor.lerValor()).thenThrow(new FonteIndisponivelException("sensor fora do ar"));

        assertThrows(FonteIndisponivelException.class, () -> servico.registrarLeitura(sensor));
        verify(medicaoDAO, never()).salvar(any(Medicao.class));
    }
}
