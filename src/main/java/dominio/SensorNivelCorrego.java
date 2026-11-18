// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package dominio;

import java.util.Random;

/** Sensor de nível de córrego simulado (cm). Oscila em torno de um nível normal. */
public class SensorNivelCorrego extends SensorSimulado implements Sensor {

    private static final double NIVEL_NORMAL = 120;

    public SensorNivelCorrego(String id, Random random) {
        this(id, random, 0.03);
    }

    public SensorNivelCorrego(String id, Random random, double probabilidadeFalha) {
        super(id, TipoSensor.NIVEL_CORREGO, random, probabilidadeFalha, 100);
    }

    @Override
    protected double proximoValor(double atual, Random random) {
        return atual + 0.15 * (NIVEL_NORMAL - atual) + random.nextGaussian() * 20;
    }
}
