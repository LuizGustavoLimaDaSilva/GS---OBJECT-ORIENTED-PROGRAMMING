// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
import dominio.NivelAlerta;

/** Tema visual "natureza": tons de verde para monitoramento, âmbar e vermelho-terra para alertas. */
public final class Tema {

    private static final boolean COR = System.getenv("NO_COLOR") == null
            && !"true".equalsIgnoreCase(System.getProperty("sem.cor"));

    private static final String RESET = "\u001B[0m";
    private static final String NEGRITO = "\u001B[1m";
    private static final String VERDE_FOLHA = "\u001B[38;5;34m";
    private static final String VERDE_ESCURO = "\u001B[38;5;28m";
    private static final String VERDE_CLARO = "\u001B[38;5;120m";
    private static final String AMARELO_SOL = "\u001B[38;5;220m";
    private static final String LARANJA_TERRA = "\u001B[38;5;208m";
    private static final String VERMELHO_BARRO = "\u001B[38;5;160m";
    private static final String CINZA_PEDRA = "\u001B[38;5;245m";

    private Tema() {
    }

    private static String pintar(String cor, String texto) {
        return COR ? cor + texto + RESET : texto;
    }

    public static String titulo(String t) {
        return COR ? NEGRITO + VERDE_FOLHA + t + RESET : t;
    }

    public static String ok(String t) {
        return pintar(VERDE_FOLHA, t);
    }

    public static String destaque(String t) {
        return pintar(VERDE_CLARO, t);
    }

    public static String suave(String t) {
        return pintar(CINZA_PEDRA, t);
    }

    public static String aviso(String t) {
        return pintar(AMARELO_SOL, t);
    }

    public static String erro(String t) {
        return pintar(VERMELHO_BARRO, t);
    }

    public static String borda(String t) {
        return pintar(VERDE_ESCURO, t);
    }

    public static String nivel(NivelAlerta nivel, String t) {
        return switch (nivel) {
            case ATENCAO -> pintar(AMARELO_SOL, t);
            case ALERTA -> pintar(LARANJA_TERRA, t);
            case CRITICO -> pintar(VERMELHO_BARRO, t);
        };
    }

    public static String linha() {
        return borda("─".repeat(66));
    }
}
