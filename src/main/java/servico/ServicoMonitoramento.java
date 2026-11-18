// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package servico;

import dominio.Alerta;
import dominio.ComponenteTerritorial;
import dominio.EstrategiaPrevisao;
import dominio.Formato;
import dominio.Limiares;
import dominio.Medicao;
import dominio.NivelAlerta;
import dominio.Sensor;
import dominio.TipoSensor;
import excecao.FonteIndisponivelException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import persistencia.AlertaDAO;
import persistencia.MedicaoDAO;

/**
 * Regras de negócio: ler sensores, gravar histórico, prever e emitir alertas.
 * Recebe os DAOs pelo construtor, então os testes podem trocá-los por mocks (sem banco).
 */
public class ServicoMonitoramento {

    private final MedicaoDAO medicaoDAO;
    private final AlertaDAO alertaDAO;
    private final ConfiguracaoSistema configuracao;
    private final List<Sensor> sensores;

    public ServicoMonitoramento(MedicaoDAO medicaoDAO, AlertaDAO alertaDAO,
                                ConfiguracaoSistema configuracao, List<Sensor> sensores) {
        this.medicaoDAO = medicaoDAO;
        this.alertaDAO = alertaDAO;
        this.configuracao = configuracao;
        this.sensores = new ArrayList<>(sensores);
    }

    public List<Sensor> getSensores() {
        return Collections.unmodifiableList(sensores);
    }

    /**
     * Lê o sensor, valida a medição (DadosInvalidosException se estiver fora da faixa)
     * e grava no banco apenas se for válida.
     */
    public Medicao registrarLeitura(Sensor sensor) throws FonteIndisponivelException {
        double valor = sensor.lerValor();
        Medicao medicao = new Medicao(sensor.getId(), sensor.getTipo(), valor);
        medicaoDAO.salvar(medicao);
        return medicao;
    }

    /** Classifica um valor com os limiares atuais; vazio = abaixo do nível de atenção. */
    public Optional<NivelAlerta> classificar(TipoSensor tipo, double valor) {
        return configuracao.getDadosTerritorio().getLimiares(tipo).classificar(valor);
    }

    /**
     * Roda uma estratégia sobre o histórico do banco. Se o valor previsto passar de um limiar,
     * emite e grava um alerta que explica sensor, valor previsto e limite estourado.
     */
    public ResultadoPrevisao prever(Sensor sensor, EstrategiaPrevisao estrategia) throws FonteIndisponivelException {
        TipoSensor tipo = sensor.getTipo();
        List<Medicao> historico = medicaoDAO.buscarUltimasPorSensor(sensor.getId(), configuracao.getLimiteHistorico());
        List<Double> valores = historico.stream().map(Medicao::getValor).toList();

        double bruto = estrategia.prever(valores); // lança DadosInvalidosException se houver poucas leituras
        double previsto = Math.max(tipo.getMinimo(), Math.min(tipo.getMaximo(), bruto));
        double ultima = valores.get(valores.size() - 1);

        Alerta alerta = null;
        Optional<NivelAlerta> nivel = classificar(tipo, previsto);
        if (nivel.isPresent()) {
            Limiares limiares = configuracao.getDadosTerritorio().getLimiares(tipo);
            alerta = Alerta.gerar(sensor.getId(), tipo, nivel.get(), previsto,
                    limiares.limiteDe(nivel.get()), estrategia.getNome());
            alertaDAO.salvar(alerta);
        }
        String significado = interpretar(tipo, previsto, ultima, valores.size(), nivel);
        return new ResultadoPrevisao(sensor.getId(), tipo, estrategia.getNome(), previsto, ultima,
                valores.size(), significado, alerta);
    }

    private String interpretar(TipoSensor tipo, double previsto, double ultima, int leituras,
                               Optional<NivelAlerta> nivel) {
        String tendencia = previsto > ultima + 0.5 ? "em alta"
                : previsto < ultima - 0.5 ? "em queda" : "estável";
        String situacao = nivel.map(n -> "acima do limite de " + n.getRotulo())
                .orElse("abaixo do nível de atenção");
        return "previsão de " + Formato.numero(previsto) + " " + tipo.getUnidade() + " ("
                + tipo.getNome().toLowerCase() + " " + tendencia + " em relação à última leitura de "
                + Formato.numero(ultima) + "), " + situacao + ", com base em " + leituras + " leituras.";
    }

    public List<Medicao> historicoPorSensor(String sensorId) throws FonteIndisponivelException {
        return medicaoDAO.buscarPorSensor(sensorId);
    }

    public List<Alerta> alertasEmitidos() throws FonteIndisponivelException {
        return alertaDAO.listarTodos();
    }

    /** Monta o status completo usando o padrão Builder. */
    public String gerarRelatorio(List<ComponenteTerritorial> componentes) throws FonteIndisponivelException {
        List<Medicao> ultimas = new ArrayList<>();
        for (Sensor sensor : sensores) {
            List<Medicao> ultima = medicaoDAO.buscarUltimasPorSensor(sensor.getId(), 1);
            ultimas.addAll(ultima);
        }
        return new RelatorioBuilder()
                .comCabecalho(configuracao.getDadosTerritorio())
                .comComponentes(componentes)
                .comUltimasLeituras(ultimas)
                .comAlertasRecentes(alertaDAO.listarTodos(), 5)
                .build();
    }

    /** Bônus: exporta o histórico de um sensor para CSV. */
    public int exportarHistoricoCsv(String sensorId, Path destino) throws FonteIndisponivelException {
        List<Medicao> historico = medicaoDAO.buscarPorSensor(sensorId);
        try (BufferedWriter escritor = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
            escritor.write("id,sensor,tipo,valor,unidade,data_hora");
            escritor.newLine();
            for (int i = historico.size() - 1; i >= 0; i--) { // do mais antigo para o mais recente
                Medicao m = historico.get(i);
                escritor.write(m.getId() + "," + m.getSensorId() + "," + m.getTipo().name() + ","
                        + m.getValor() + "," + m.getTipo().getUnidade() + "," + m.getDataHora());
                escritor.newLine();
            }
        } catch (IOException e) {
            throw new FonteIndisponivelException("Não foi possível gravar o arquivo " + destino
                    + ": " + e.getMessage(), e);
        }
        return historico.size();
    }
}
