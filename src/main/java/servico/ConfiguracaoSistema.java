// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package servico;

import dominio.DadosTerritorio;
import excecao.DadosInvalidosException;

/**
 * Padrão SINGLETON: existe uma única configuração em todo o sistema.
 * Guarda os dados do território (com os limiares) e os parâmetros de previsão.
 * Se cada parte do código criasse a sua própria cópia, o menu poderia alterar um limiar
 * e o serviço de alertas continuar usando o valor antigo.
 */
public final class ConfiguracaoSistema {

    private static ConfiguracaoSistema instancia;

    private final DadosTerritorio dadosTerritorio;
    private int janelaPrevisao = 3;
    private final int limiteHistorico = 30;

    private ConfiguracaoSistema() {
        this.dadosTerritorio = new DadosTerritorio("Bacia do Córrego Verde", "Zona Leste - São Paulo", 12000);
    }

    public static synchronized ConfiguracaoSistema getInstance() {
        if (instancia == null) {
            instancia = new ConfiguracaoSistema();
        }
        return instancia;
    }

    /** Volta limiares, população e janela aos valores iniciais (útil no menu e nos testes). */
    public void restaurarPadroes() {
        dadosTerritorio.restaurarLimiaresPadrao();
        dadosTerritorio.setPopulacao(12000);
        janelaPrevisao = 3;
    }

    public DadosTerritorio getDadosTerritorio() {
        return dadosTerritorio;
    }

    public int getJanelaPrevisao() {
        return janelaPrevisao;
    }

    public void setJanelaPrevisao(int janelaPrevisao) {
        if (janelaPrevisao < 2 || janelaPrevisao > 15) {
            throw new DadosInvalidosException(
                    "A janela de previsão deve ficar entre 2 e 15 leituras (recebido " + janelaPrevisao + ").");
        }
        this.janelaPrevisao = janelaPrevisao;
    }

    /** Quantas leituras recentes o sistema consulta no banco para prever. */
    public int getLimiteHistorico() {
        return limiteHistorico;
    }
}
