// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package dominio;

import java.util.List;

/** Previsão = média das últimas N medições do histórico. */
public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    public PrevisaoMediaMovel(int janela) {
        super("Média móvel", janela);
    }

    @Override
    public double prever(List<Double> historico) {
        super.prever(historico); // valida o histórico (herdado)
        int n = Math.min(getJanela(), historico.size());
        return media(historico, historico.size() - n, historico.size());
    }

    @Override
    public String descrever() {
        return "média das últimas " + getJanela() + " leituras (suaviza oscilações)";
    }
}
