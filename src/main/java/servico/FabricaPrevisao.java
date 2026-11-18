// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package servico;

import dominio.EstrategiaPrevisao;
import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import dominio.TipoPrevisao;
import excecao.DadosInvalidosException;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.IntFunction;

/**
 * Padrão FACTORY: concentra a criação das estratégias de previsão.
 * O menu e o serviço pedem "uma previsão do tipo X" sem usar if/switch espalhado
 * nem conhecer as classes concretas. Para criar uma terceira estratégia basta
 * registrar uma linha aqui.
 */
public final class FabricaPrevisao {

    private static final Map<TipoPrevisao, IntFunction<EstrategiaPrevisao>> CRIADORES =
            new EnumMap<>(TipoPrevisao.class);

    static {
        CRIADORES.put(TipoPrevisao.MEDIA_MOVEL, PrevisaoMediaMovel::new);
        CRIADORES.put(TipoPrevisao.COMPARACAO, PrevisaoComparacao::new);
    }

    private FabricaPrevisao() {
    }

    public static EstrategiaPrevisao criar(TipoPrevisao tipo, int janela) {
        IntFunction<EstrategiaPrevisao> criador = tipo == null ? null : CRIADORES.get(tipo);
        if (criador == null) {
            throw new DadosInvalidosException("Tipo de previsão desconhecido: " + tipo);
        }
        return criador.apply(janela);
    }
}
