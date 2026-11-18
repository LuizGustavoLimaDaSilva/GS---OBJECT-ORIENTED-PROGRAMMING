// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package dominio;

import excecao.FonteIndisponivelException;

/** Contrato de qualquer sensor do território. */
public interface Sensor {

    /** Identificador único do sensor (ex.: CHUVA-01). */
    String getId();

    /** Lê o valor atual. Lança FonteIndisponivelException se o sensor falhar. */
    double lerValor() throws FonteIndisponivelException;

    /** Informa se o sensor está funcionando. */
    boolean estaFuncionando();

    /** Retorna o tipo de grandeza medida. */
    TipoSensor getTipo();
}
