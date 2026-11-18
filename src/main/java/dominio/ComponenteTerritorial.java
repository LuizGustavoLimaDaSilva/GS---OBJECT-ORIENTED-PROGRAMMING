// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package dominio;

import excecao.DadosInvalidosException;

/** Qualquer elemento monitorado do território (ponto vulnerável, equipamento público...). */
public abstract class ComponenteTerritorial {

    private final String id;
    private String nome;
    private String regiao;
    private StatusComponente status = StatusComponente.OPERANDO;

    protected ComponenteTerritorial(String id, String nome, String regiao) {
        if (id == null || id.isBlank()) {
            throw new DadosInvalidosException("O componente territorial precisa de um id.");
        }
        this.id = id;
        setNome(nome);
        setRegiao(regiao);
    }

    /** Recalcula e retorna a situação operacional do componente. */
    public abstract StatusComponente verificarStatus();

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public final void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome do componente não pode ser vazio.");
        }
        this.nome = nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public final void setRegiao(String regiao) {
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("A região do componente não pode ser vazia.");
        }
        this.regiao = regiao;
    }

    public StatusComponente getStatus() {
        return status;
    }

    protected void setStatus(StatusComponente status) {
        this.status = status;
    }
}
