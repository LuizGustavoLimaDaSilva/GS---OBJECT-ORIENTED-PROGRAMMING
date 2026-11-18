// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package dominio;

import excecao.DadosInvalidosException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Dados do território monitorado. Todos os atributos são privados e só mudam
 * por setters que validam o novo valor (população e limiares).
 * Nunca guarda dados de pessoas: o território é descrito por região e equipamento.
 */
public class DadosTerritorio {

    private String nome;
    private String regiao;
    private int populacao;
    private final Map<TipoSensor, Limiares> limiares = new EnumMap<>(TipoSensor.class);

    public DadosTerritorio(String nome, String regiao, int populacao) {
        setNome(nome);
        setRegiao(regiao);
        setPopulacao(populacao);
        restaurarLimiaresPadrao();
    }

    public final void restaurarLimiaresPadrao() {
        limiares.put(TipoSensor.CHUVA, new Limiares(25, 50, 80));
        limiares.put(TipoSensor.NIVEL_CORREGO, new Limiares(150, 250, 350));
        limiares.put(TipoSensor.UMIDADE_SOLO, new Limiares(70, 85, 95));
    }

    public String getNome() {
        return nome;
    }

    public final void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome do território não pode ser vazio.");
        }
        this.nome = nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public final void setRegiao(String regiao) {
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("A região do território não pode ser vazia.");
        }
        this.regiao = regiao;
    }

    public int getPopulacao() {
        return populacao;
    }

    public final void setPopulacao(int populacao) {
        if (populacao <= 0) {
            throw new DadosInvalidosException(
                    "A população estimada deve ser maior que zero (recebido " + populacao + ").");
        }
        this.populacao = populacao;
    }

    public Limiares getLimiares(TipoSensor tipo) {
        return limiares.get(tipo);
    }

    /** Altera os limiares de um tipo de sensor; valores incoerentes lançam DadosInvalidosException. */
    public void setLimiares(TipoSensor tipo, double atencao, double alerta, double critico) {
        Limiares novos = new Limiares(atencao, alerta, critico);
        if (critico > tipo.getMaximo()) {
            throw new DadosInvalidosException("O limiar crítico de " + tipo.getNome().toLowerCase()
                    + " não pode passar de " + Formato.numero(tipo.getMaximo()) + " " + tipo.getUnidade() + ".");
        }
        limiares.put(tipo, novos);
    }
}
