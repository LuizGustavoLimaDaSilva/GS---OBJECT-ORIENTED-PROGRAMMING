// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package dominio;

import java.util.Random;

/** Sensor de umidade do solo simulado (%). Solo saturado aumenta o risco em encostas. */
public class SensorUmidadeSolo extends SensorSimulado implements Sensor {

    private static final double UMIDADE_NORMAL = 60;

    public SensorUmidadeSolo(String id, Random random) {
        this(id, random, 0.03);
    }

    public SensorUmidadeSolo(String id, Random random, double probabilidadeFalha) {
        super(id, TipoSensor.UMIDADE_SOLO, random, probabilidadeFalha, 55);
    }

    @Override
    protected double proximoValor(double atual, Random random) {
        return atual + 0.2 * (UMIDADE_NORMAL - atual) + random.nextGaussian() * 6;
    }
}
