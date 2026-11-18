// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package dominio;

import excecao.DadosInvalidosException;
import java.time.LocalDateTime;

/** Alerta emitido quando a previsão ultrapassa um limiar. Sempre explica o porquê. */
public class Alerta {

    private Long id;
    private final String sensorId;
    private final TipoSensor tipo;
    private final NivelAlerta nivel;
    private final double valorPrevisto;
    private final double limite;
    private final String estrategia;
    private final String mensagem;
    private final LocalDateTime dataHora;

    public Alerta(Long id, String sensorId, TipoSensor tipo, NivelAlerta nivel, double valorPrevisto,
                  double limite, String estrategia, String mensagem, LocalDateTime dataHora) {
        if (sensorId == null || tipo == null || nivel == null || mensagem == null) {
            throw new DadosInvalidosException("Alerta incompleto: sensor, tipo, nível e mensagem são obrigatórios.");
        }
        this.id = id;
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.nivel = nivel;
        this.valorPrevisto = valorPrevisto;
        this.limite = limite;
        this.estrategia = estrategia;
        this.mensagem = mensagem;
        this.dataHora = dataHora;
    }

    /** Cria o alerta já com a mensagem explicando sensor, valor previsto, limite estourado e ação sugerida. */
    public static Alerta gerar(String sensorId, TipoSensor tipo, NivelAlerta nivel,
                               double valorPrevisto, double limite, String estrategia) {
        String mensagem = nivel.getRotulo() + " no sensor " + sensorId + " (" + tipo.getNome() + "): "
                + "valor previsto de " + Formato.numero(valorPrevisto) + " " + tipo.getUnidade()
                + " ultrapassa o limite de " + nivel.getRotulo() + " (" + Formato.numero(limite)
                + " " + tipo.getUnidade() + "), pela estratégia " + estrategia + ". "
                + "Sugestão para apoiar a decisão: " + tipo.getAcaoSugerida() + ".";
        return new Alerta(null, sensorId, tipo, nivel, valorPrevisto, limite, estrategia,
                mensagem, LocalDateTime.now());
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

    public NivelAlerta getNivel() {
        return nivel;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public double getLimite() {
        return limite;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
