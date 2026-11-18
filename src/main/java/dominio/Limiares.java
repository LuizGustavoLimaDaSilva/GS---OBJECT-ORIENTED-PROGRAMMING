// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package dominio;

import excecao.DadosInvalidosException;
import java.util.Optional;

/** Limiares de atenção, alerta e crítico de um tipo de sensor (imutável e sempre coerente). */
public record Limiares(double atencao, double alerta, double critico) {

    public Limiares {
        if (Double.isNaN(atencao) || Double.isNaN(alerta) || Double.isNaN(critico) || atencao <= 0) {
            throw new DadosInvalidosException("Limiar inválido: os valores devem ser números maiores que zero.");
        }
        if (!(atencao < alerta && alerta < critico)) {
            throw new DadosInvalidosException(
                    "Limiares incoerentes: é preciso ATENÇÃO < ALERTA < CRÍTICO (recebido "
                            + Formato.numero(atencao) + " / " + Formato.numero(alerta)
                            + " / " + Formato.numero(critico) + ").");
        }
    }

    /** Classifica um valor; vazio significa abaixo do nível de atenção. */
    public Optional<NivelAlerta> classificar(double valor) {
        if (valor >= critico) {
            return Optional.of(NivelAlerta.CRITICO);
        }
        if (valor >= alerta) {
            return Optional.of(NivelAlerta.ALERTA);
        }
        if (valor >= atencao) {
            return Optional.of(NivelAlerta.ATENCAO);
        }
        return Optional.empty();
    }

    /** Retorna o valor do limite que corresponde ao nível informado. */
    public double limiteDe(NivelAlerta nivel) {
        return switch (nivel) {
            case ATENCAO -> atencao;
            case ALERTA -> alerta;
            case CRITICO -> critico;
        };
    }
}
