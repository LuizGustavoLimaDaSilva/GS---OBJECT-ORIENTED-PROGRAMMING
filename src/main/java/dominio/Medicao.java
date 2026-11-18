// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package dominio;

import excecao.DadosInvalidosException;
import java.time.LocalDateTime;

/** Uma leitura de sensor. Toda medição é validada ao ser criada: valor fora da faixa não existe. */
public class Medicao {

    private Long id;
    private final String sensorId;
    private final TipoSensor tipo;
    private final double valor;
    private final LocalDateTime dataHora;

    public Medicao(String sensorId, TipoSensor tipo, double valor) {
        this(null, sensorId, tipo, valor, LocalDateTime.now());
    }

    public Medicao(Long id, String sensorId, TipoSensor tipo, double valor, LocalDateTime dataHora) {
        if (sensorId == null || sensorId.isBlank()) {
            throw new DadosInvalidosException("A medição precisa do identificador do sensor.");
        }
        if (tipo == null) {
            throw new DadosInvalidosException("A medição precisa do tipo de sensor.");
        }
        if (Double.isNaN(valor) || Double.isInfinite(valor)
                || valor < tipo.getMinimo() || valor > tipo.getMaximo()) {
            throw new DadosInvalidosException("Medição de " + tipo.getNome().toLowerCase()
                    + " fora da faixa válida (" + Formato.numero(tipo.getMinimo()) + " a "
                    + Formato.numero(tipo.getMaximo()) + " " + tipo.getUnidade() + "): " + valor);
        }
        this.id = id;
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public TipoSensor getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
