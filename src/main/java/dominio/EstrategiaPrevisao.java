// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package dominio;

import excecao.DadosInvalidosException;
import java.util.List;

/**
 * Estratégia de previsão sobre o histórico de um sensor.
 * A classe base valida o histórico (o sistema se cala com poucas leituras) e,
 * sozinha, aplica a previsão mais ingênua: repetir a última leitura.
 * As filhas sobrescrevem prever() chamando super.prever() para herdar a validação.
 */
public abstract class EstrategiaPrevisao {

    /** Menos que isso, o histórico é pouco para prever sem "chutar". */
    public static final int MIN_LEITURAS = 5;

    private final String nome;
    private final int janela;

    protected EstrategiaPrevisao(String nome, int janela) {
        if (janela < 2) {
            throw new DadosInvalidosException("A janela da previsão deve ter pelo menos 2 leituras (recebido " + janela + ").");
        }
        this.nome = nome;
        this.janela = janela;
    }

    /**
     * @param historico valores em ordem cronológica (do mais antigo para o mais recente)
     * @return valor previsto
     */
    public double prever(List<Double> historico) {
        if (historico == null || historico.size() < MIN_LEITURAS) {
            int qtd = historico == null ? 0 : historico.size();
            throw new DadosInvalidosException("Histórico insuficiente para prever: " + qtd
                    + " leitura(s), mínimo de " + MIN_LEITURAS + ". Colete mais leituras antes de prever.");
        }
        return historico.get(historico.size() - 1);
    }

    /** Explica em linguagem simples como a estratégia calcula. */
    public abstract String descrever();

    protected static double media(List<Double> valores, int inicio, int fim) {
        double soma = 0;
        for (int i = inicio; i < fim; i++) {
            soma += valores.get(i);
        }
        return soma / (fim - inicio);
    }

    public String getNome() {
        return nome;
    }

    public int getJanela() {
        return janela;
    }
}
