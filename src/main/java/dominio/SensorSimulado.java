// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package dominio;

import excecao.FonteIndisponivelException;
import java.util.Random;

/**
 * Base dos sensores simulados. Em vez de sortear valores totalmente independentes,
 * cada leitura parte da anterior (passeio aleatório com tendência de retorno à média),
 * o que gera séries parecidas com dados reais e dá sentido à previsão.
 */
public abstract class SensorSimulado implements Sensor {

    private final String id;
    private final TipoSensor tipo;
    private final Random random;
    private final double probabilidadeFalha;
    private boolean funcionando = true;
    private double valorAtual;

    protected SensorSimulado(String id, TipoSensor tipo, Random random,
                             double probabilidadeFalha, double valorInicial) {
        this.id = id;
        this.tipo = tipo;
        this.random = random;
        this.probabilidadeFalha = probabilidadeFalha;
        this.valorAtual = valorInicial;
    }

    @Override
    public double lerValor() throws FonteIndisponivelException {
        if (!funcionando || random.nextDouble() < probabilidadeFalha) {
            throw new FonteIndisponivelException(
                    "Sensor " + id + " (" + tipo.getNome() + ") não respondeu à leitura.");
        }
        double proximo = proximoValor(valorAtual, random);
        valorAtual = Math.max(tipo.getMinimo(), Math.min(tipo.getMaximo(), proximo));
        return Math.round(valorAtual * 10.0) / 10.0;
    }

    /** Cada tipo de sensor define como a grandeza evolui de uma leitura para a outra. */
    protected abstract double proximoValor(double atual, Random random);

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean estaFuncionando() {
        return funcionando;
    }

    public void setFuncionando(boolean funcionando) {
        this.funcionando = funcionando;
    }

    @Override
    public TipoSensor getTipo() {
        return tipo;
    }
}
