// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Ponto do território sujeito a alagamento ou deslizamento, equipado com sensores. */
public class PontoVulneravel extends ComponenteTerritorial {

    private final List<Sensor> sensores = new ArrayList<>();

    public PontoVulneravel(String id, String nome, String regiao) {
        super(id, nome, regiao);
    }

    public void adicionarSensor(Sensor sensor) {
        sensores.add(sensor);
    }

    public List<Sensor> getSensores() {
        return Collections.unmodifiableList(sensores);
    }

    @Override
    public StatusComponente verificarStatus() {
        long ativos = sensores.stream().filter(Sensor::estaFuncionando).count();
        StatusComponente novo;
        if (sensores.isEmpty() || ativos == 0) {
            novo = StatusComponente.OFFLINE;
        } else if (ativos < sensores.size()) {
            novo = StatusComponente.DEGRADADO;
        } else {
            novo = StatusComponente.OPERANDO;
        }
        setStatus(novo);
        return novo;
    }
}
