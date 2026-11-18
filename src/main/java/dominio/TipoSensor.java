// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package dominio;

/**
 * Tipos de sensor do recorte "Clima local: chuva intensa e risco de alagamento".
 * Cada tipo conhece sua unidade, sua faixa física válida e a ação sugerida
 * quando um alerta é emitido.
 */
public enum TipoSensor {

    CHUVA("Chuva", "mm/h", 0, 300,
            "acionar a Defesa Civil local e conferir bueiros e bocas de lobo da região"),
    NIVEL_CORREGO("Nível do córrego", "cm", 0, 500,
            "avisar moradores das margens e preparar a retirada de pessoas de áreas de risco"),
    UMIDADE_SOLO("Umidade do solo", "%", 0, 100,
            "vigiar encostas e evitar a circulação em áreas de deslizamento");

    private final String nome;
    private final String unidade;
    private final double minimo;
    private final double maximo;
    private final String acaoSugerida;

    TipoSensor(String nome, String unidade, double minimo, double maximo, String acaoSugerida) {
        this.nome = nome;
        this.unidade = unidade;
        this.minimo = minimo;
        this.maximo = maximo;
        this.acaoSugerida = acaoSugerida;
    }

    public String getNome() {
        return nome;
    }

    public String getUnidade() {
        return unidade;
    }

    public double getMinimo() {
        return minimo;
    }

    public double getMaximo() {
        return maximo;
    }

    public String getAcaoSugerida() {
        return acaoSugerida;
    }
}
