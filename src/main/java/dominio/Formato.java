// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package dominio;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Utilitário de formatação (números em pt-BR e datas). */
public final class Formato {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    public static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private Formato() {
    }

    public static String numero(double valor) {
        return String.format(PT_BR, "%.1f", valor);
    }
}
