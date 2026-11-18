// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package servico;

import dominio.Alerta;
import dominio.TipoSensor;

/**
 * Resultado de uma previsão: o valor previsto e o que ele significa.
 * O campo alerta é nulo quando a previsão está abaixo do nível de atenção.
 */
public record ResultadoPrevisao(String sensorId, TipoSensor tipo, String estrategia, double valorPrevisto,
                                double ultimaLeitura, int leituras, String significado, Alerta alerta) {

    public boolean temAlerta() {
        return alerta != null;
    }
}
