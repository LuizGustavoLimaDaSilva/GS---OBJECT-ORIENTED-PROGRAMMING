// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package dominio;

import java.util.Random;

/** Pluviômetro simulado (mm/h). Chuva fraca na maior parte do tempo, com pancadas ocasionais. */
public class SensorChuva extends SensorSimulado implements Sensor {

    public SensorChuva(String id, Random random) {
        this(id, random, 0.03);
    }

    public SensorChuva(String id, Random random, double probabilidadeFalha) {
        super(id, TipoSensor.CHUVA, random, probabilidadeFalha, random.nextDouble() * 30);
    }

    @Override
    protected double proximoValor(double atual, Random random) {
        double novo = atual * 0.8 + Math.abs(random.nextGaussian()) * 8;
        if (random.nextDouble() < 0.08) {
            novo += 25 + random.nextDouble() * 30; // pancada de chuva
        }
        return novo;
    }
}
