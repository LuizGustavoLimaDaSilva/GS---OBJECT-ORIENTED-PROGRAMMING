// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package excecao;

/**
 * Lançada quando uma fonte de dados falha: sensor fora do ar, gerador de dados
 * indisponível, banco de dados inacessível ou arquivo que não pôde ser gravado.
 * É verificada: quem chama é obrigado a decidir o que fazer com a falha.
 */
public class FonteIndisponivelException extends Exception {

    public FonteIndisponivelException(String mensagem) {
        super(mensagem);
    }

    public FonteIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
