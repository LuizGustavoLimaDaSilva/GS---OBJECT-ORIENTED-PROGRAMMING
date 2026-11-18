// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package dominio;

import java.util.List;

/**
 * Previsão por comparação com o período anterior: compara a média das últimas N leituras
 * com a média das N anteriores e projeta a mesma variação para o próximo período.
 */
public class PrevisaoComparacao extends EstrategiaPrevisao {

    public PrevisaoComparacao(int janela) {
        super("Comparação com período anterior", janela);
    }

    @Override
    public double prever(List<Double> historico) {
        super.prever(historico); // valida o histórico (herdado)
        int tamanho = historico.size();
        int n = Math.min(getJanela(), tamanho / 2);
        double recente = media(historico, tamanho - n, tamanho);
        double anterior = media(historico, tamanho - 2 * n, tamanho - n);
        double tendencia = recente - anterior;
        return Math.max(0, recente + tendencia);
    }

    @Override
    public String descrever() {
        return "projeta a variação entre as últimas " + getJanela()
                + " leituras e as " + getJanela() + " anteriores (captura tendência de alta ou queda)";
    }
}
