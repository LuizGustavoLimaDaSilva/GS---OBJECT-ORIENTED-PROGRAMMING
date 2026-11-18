// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package servico;

import dominio.Alerta;
import dominio.ComponenteTerritorial;
import dominio.DadosTerritorio;
import dominio.Formato;
import dominio.Medicao;
import java.util.List;

/**
 * Padrão BUILDER: monta o relatório de status completo passo a passo.
 * Cada parte (cabeçalho, componentes, últimas leituras, alertas) é opcional e
 * adicionada só quando necessária, sem um construtor gigante com vários parâmetros.
 */
public class RelatorioBuilder {

    private final StringBuilder texto = new StringBuilder();

    public RelatorioBuilder comCabecalho(DadosTerritorio dados) {
        texto.append("TERRITÓRIO: ").append(dados.getNome()).append(" - ").append(dados.getRegiao()).append('\n');
        texto.append("População estimada: ").append(dados.getPopulacao()).append(" habitantes\n\n");
        return this;
    }

    public RelatorioBuilder comComponentes(List<ComponenteTerritorial> componentes) {
        texto.append("COMPONENTES MONITORADOS\n");
        for (ComponenteTerritorial c : componentes) {
            texto.append("  - ").append(c.getNome()).append(" [").append(c.getId()).append("]: ")
                    .append(c.verificarStatus()).append('\n');
        }
        texto.append('\n');
        return this;
    }

    public RelatorioBuilder comUltimasLeituras(List<Medicao> ultimas) {
        texto.append("ÚLTIMA LEITURA DE CADA SENSOR\n");
        if (ultimas.isEmpty()) {
            texto.append("  (nenhuma leitura registrada ainda)\n");
        }
        for (Medicao m : ultimas) {
            texto.append("  - ").append(m.getSensorId()).append(": ")
                    .append(Formato.numero(m.getValor())).append(' ').append(m.getTipo().getUnidade())
                    .append(" em ").append(m.getDataHora().format(Formato.DATA_HORA)).append('\n');
        }
        texto.append('\n');
        return this;
    }

    public RelatorioBuilder comAlertasRecentes(List<Alerta> alertas, int maximo) {
        texto.append("ALERTAS RECENTES\n");
        if (alertas.isEmpty()) {
            texto.append("  (nenhum alerta emitido)\n");
        }
        alertas.stream().limit(maximo).forEach(a -> texto.append("  - ")
                .append(a.getDataHora().format(Formato.DATA_HORA)).append(" | ")
                .append(a.getNivel().getRotulo()).append(" | ").append(a.getSensorId()).append(" | previsto ")
                .append(Formato.numero(a.getValorPrevisto())).append(' ').append(a.getTipo().getUnidade())
                .append('\n'));
        return this;
    }

    public String build() {
        return texto.toString();
    }
}
