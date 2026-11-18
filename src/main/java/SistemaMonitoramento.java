// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
import dominio.Alerta;
import dominio.ComponenteTerritorial;
import dominio.DadosTerritorio;
import dominio.EstrategiaPrevisao;
import dominio.Formato;
import dominio.Medicao;
import dominio.PontoVulneravel;
import dominio.Sensor;
import dominio.SensorChuva;
import dominio.SensorNivelCorrego;
import dominio.SensorUmidadeSolo;
import dominio.TipoPrevisao;
import dominio.TipoSensor;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import persistencia.AlertaDAO;
import persistencia.ConexaoBanco;
import persistencia.MedicaoDAO;
import servico.ConfiguracaoSistema;
import servico.FabricaPrevisao;
import servico.ResultadoPrevisao;
import servico.ServicoMonitoramento;

/** Menu principal (console) do Territory Predictive Insights. */
public class SistemaMonitoramento {

    private final Scanner entrada = new Scanner(System.in);
    private final ConfiguracaoSistema configuracao = ConfiguracaoSistema.getInstance();
    private final List<ComponenteTerritorial> componentes = new ArrayList<>();
    private final ServicoMonitoramento servico;

    public SistemaMonitoramento() {
        List<Sensor> sensores = montarTerritorio();
        this.servico = new ServicoMonitoramento(new MedicaoDAO(), new AlertaDAO(), configuracao, sensores);
    }

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        try {
            ConexaoBanco.inicializarEsquema();
        } catch (FonteIndisponivelException e) {
            System.out.println(Tema.erro("Não foi possível iniciar o banco de dados: " + e.getMessage()));
            return;
        }
        new SistemaMonitoramento().executar();
    }

    /** Cria os pontos vulneráveis e seus sensores simulados; devolve todos os sensores. */
    private List<Sensor> montarTerritorio() {
        Random random = new Random();
        PontoVulneravel varzea = new PontoVulneravel("PT-01", "Praça da Várzea (ponto de alagamento)", "Zona Leste");
        PontoVulneravel ponte = new PontoVulneravel("PT-02", "Ponte do Córrego Verde", "Zona Leste");
        PontoVulneravel encosta = new PontoVulneravel("PT-03", "Encosta do Bosque Alto", "Zona Leste");

        Sensor chuva = new SensorChuva("CHUVA-01", random);
        Sensor nivel = new SensorNivelCorrego("NIVEL-01", random);
        Sensor solo = new SensorUmidadeSolo("SOLO-01", random);
        varzea.adicionarSensor(chuva);
        ponte.adicionarSensor(nivel);
        encosta.adicionarSensor(solo);

        componentes.add(varzea);
        componentes.add(ponte);
        componentes.add(encosta);
        return List.of(chuva, nivel, solo);
    }

    private void executar() {
        banner();
        boolean rodando = true;
        while (rodando) {
            exibirMenu();
            String opcao = entrada.nextLine().trim();
            System.out.println();
            switch (opcao) {
                case "1" -> verificarSensores();
                case "2" -> rodarPrevisoes();
                case "3" -> verHistorico();
                case "4" -> verAlertas();
                case "5" -> gerenciarDados();
                case "6" -> statusCompleto();
                case "7" -> exportarCsv();
                case "0" -> rodando = false;
                default -> System.out.println(Tema.aviso("Opção inválida. Digite um número do menu."));
            }
        }
        System.out.println(Tema.ok("Até logo! Que os alertas cheguem a tempo."));
    }

    private void banner() {
        System.out.println();
        System.out.println(Tema.linha());
        System.out.println(Tema.titulo("  TERRITORY PREDICTIVE INSIGHTS  -  CitizenTech"));
        System.out.println(Tema.destaque("  Monitoramento de chuva e risco de alagamento no território"));
        System.out.println(Tema.suave("  Global Solution 2º/2026 - POO | Turma de Fevereiro"));
        System.out.println(Tema.suave("  Desenvolvido por: João Pedro Tomas Dominguito (RM 562166),"));
        System.out.println(Tema.suave("  Luiz Gustavo Lima da Silva (RM 563554) e Vicente Casellato Rodriguez (RM 563865)"));
        System.out.println(Tema.linha());
        DadosTerritorio dados = configuracao.getDadosTerritorio();
        System.out.println(Tema.ok("  Território: ") + dados.getNome() + " - " + dados.getRegiao());
    }

    private void exibirMenu() {
        System.out.println();
        System.out.println(Tema.titulo("MENU"));
        System.out.println(Tema.ok("  1") + " - Verificar sensores (ler e gravar medições)");
        System.out.println(Tema.ok("  2") + " - Rodar previsões e alertas");
        System.out.println(Tema.ok("  3") + " - Ver histórico de medições por sensor");
        System.out.println(Tema.ok("  4") + " - Ver alertas já emitidos");
        System.out.println(Tema.ok("  5") + " - Gerenciar dados do território");
        System.out.println(Tema.ok("  6") + " - Status completo");
        System.out.println(Tema.ok("  7") + " - Exportar histórico para CSV (bônus)");
        System.out.println(Tema.ok("  0") + " - Sair");
        System.out.print(Tema.destaque("Escolha: "));
    }

    // ---------- 1. Verificar sensores ----------

    private void verificarSensores() {
        System.out.println(Tema.titulo("VERIFICAÇÃO DE SENSORES"));
        for (ComponenteTerritorial c : componentes) {
            System.out.println("  " + c.getNome() + ": " + c.verificarStatus());
        }
        Integer rodadas = lerInteiro("Quantas rodadas de leitura deseja fazer (1 a 50)? ");
        if (rodadas == null) {
            return;
        }
        if (rodadas < 1 || rodadas > 50) {
            System.out.println(Tema.aviso("Informe um número de rodadas entre 1 e 50."));
            return;
        }
        int gravadas = 0;
        int falhas = 0;
        for (int rodada = 1; rodada <= rodadas; rodada++) {
            for (Sensor sensor : servico.getSensores()) {
                try {
                    Medicao m = servico.registrarLeitura(sensor);
                    gravadas++;
                    if (rodadas <= 3) {
                        System.out.println(Tema.ok("  ✔ ") + sensor.getId() + ": "
                                + Formato.numero(m.getValor()) + " " + m.getTipo().getUnidade());
                    }
                } catch (FonteIndisponivelException e) {
                    falhas++;
                    System.out.println(Tema.erro("  ✘ Falha de leitura: " + e.getMessage()));
                } catch (DadosInvalidosException e) {
                    falhas++;
                    System.out.println(Tema.erro("  ✘ Medição descartada: " + e.getMessage()));
                }
            }
        }
        System.out.println(Tema.ok("Medições gravadas no banco: " + gravadas) + Tema.suave(" | falhas: " + falhas));
    }

    // ---------- 2. Previsões e alertas ----------

    private void rodarPrevisoes() {
        System.out.println(Tema.titulo("PREVISÕES E ALERTAS"));
        for (Sensor sensor : servico.getSensores()) {
            System.out.println(Tema.destaque("\n" + sensor.getId() + " - " + sensor.getTipo().getNome()));
            for (TipoPrevisao tipo : TipoPrevisao.values()) {
                EstrategiaPrevisao estrategia = FabricaPrevisao.criar(tipo, configuracao.getJanelaPrevisao());
                try {
                    ResultadoPrevisao r = servico.prever(sensor, estrategia);
                    System.out.println("  " + Tema.ok("[" + estrategia.getNome() + "] ") + r.significado());
                    if (r.temAlerta()) {
                        Alerta a = r.alerta();
                        System.out.println("    " + Tema.nivel(a.getNivel(), "⚠ " + a.getMensagem()));
                    }
                } catch (DadosInvalidosException e) {
                    System.out.println("  " + Tema.aviso("[" + estrategia.getNome() + "] Sem previsão: " + e.getMessage()));
                } catch (FonteIndisponivelException e) {
                    System.out.println("  " + Tema.erro("[" + estrategia.getNome() + "] Falha: " + e.getMessage()));
                }
            }
        }
    }

    // ---------- 3 e 4. Histórico e alertas ----------

    private Sensor escolherSensor() {
        List<Sensor> sensores = servico.getSensores();
        for (int i = 0; i < sensores.size(); i++) {
            System.out.println(Tema.ok("  " + (i + 1)) + " - " + sensores.get(i).getId()
                    + " (" + sensores.get(i).getTipo().getNome() + ")");
        }
        Integer escolha = lerInteiro("Sensor: ");
        if (escolha == null) {
            return null;
        }
        if (escolha < 1 || escolha > sensores.size()) {
            System.out.println(Tema.aviso("Sensor inexistente."));
            return null;
        }
        return sensores.get(escolha - 1);
    }

    private void verHistorico() {
        System.out.println(Tema.titulo("HISTÓRICO DE MEDIÇÕES"));
        Sensor sensor = escolherSensor();
        if (sensor == null) {
            return;
        }
        try {
            List<Medicao> historico = servico.historicoPorSensor(sensor.getId());
            if (historico.isEmpty()) {
                System.out.println(Tema.aviso("Ainda não há medições para " + sensor.getId() + ". Use a opção 1."));
                return;
            }
            System.out.println(Tema.suave("  (mais recentes primeiro - total: " + historico.size() + ")"));
            int limite = Math.min(historico.size(), 20);
            for (int i = 0; i < limite; i++) {
                Medicao m = historico.get(i);
                System.out.println("  " + m.getDataHora().format(Formato.DATA_HORA) + "  "
                        + Formato.numero(m.getValor()) + " " + m.getTipo().getUnidade());
            }
            if (historico.size() > limite) {
                System.out.println(Tema.suave("  ... e mais " + (historico.size() - limite)
                        + " medições (exporte para CSV na opção 7)."));
            }
        } catch (FonteIndisponivelException e) {
            System.out.println(Tema.erro("Não foi possível consultar o histórico: " + e.getMessage()));
        }
    }

    private void verAlertas() {
        System.out.println(Tema.titulo("ALERTAS JÁ EMITIDOS"));
        try {
            List<Alerta> alertas = servico.alertasEmitidos();
            if (alertas.isEmpty()) {
                System.out.println(Tema.ok("Nenhum alerta emitido até agora."));
                return;
            }
            for (Alerta a : alertas) {
                System.out.println(Tema.suave("  " + a.getDataHora().format(Formato.DATA_HORA)) + "  "
                        + Tema.nivel(a.getNivel(), a.getMensagem()));
            }
        } catch (FonteIndisponivelException e) {
            System.out.println(Tema.erro("Não foi possível listar os alertas: " + e.getMessage()));
        }
    }

    // ---------- 5. Gerenciar dados do território ----------

    private void gerenciarDados() {
        boolean voltar = false;
        while (!voltar) {
            DadosTerritorio dados = configuracao.getDadosTerritorio();
            System.out.println(Tema.titulo("DADOS DO TERRITÓRIO"));
            System.out.println("  " + dados.getNome() + " - " + dados.getRegiao()
                    + " | população estimada: " + dados.getPopulacao()
                    + " | janela de previsão: " + configuracao.getJanelaPrevisao());
            for (TipoSensor tipo : TipoSensor.values()) {
                var l = dados.getLimiares(tipo);
                System.out.println("  " + tipo.getNome() + " (" + tipo.getUnidade() + "): atenção "
                        + Formato.numero(l.atencao()) + " | alerta " + Formato.numero(l.alerta())
                        + " | crítico " + Formato.numero(l.critico()));
            }
            System.out.println(Tema.ok("  1") + " - Alterar população estimada");
            System.out.println(Tema.ok("  2") + " - Alterar limiares de um sensor");
            System.out.println(Tema.ok("  3") + " - Alterar janela de previsão");
            System.out.println(Tema.ok("  4") + " - Restaurar valores padrão");
            System.out.println(Tema.ok("  0") + " - Voltar");
            System.out.print(Tema.destaque("Escolha: "));
            String opcao = entrada.nextLine().trim();
            try {
                switch (opcao) {
                    case "1" -> {
                        Integer pop = lerInteiro("Nova população estimada: ");
                        if (pop != null) {
                            dados.setPopulacao(pop);
                            System.out.println(Tema.ok("População atualizada."));
                        }
                    }
                    case "2" -> alterarLimiares(dados);
                    case "3" -> {
                        Integer janela = lerInteiro("Nova janela (2 a 15 leituras): ");
                        if (janela != null) {
                            configuracao.setJanelaPrevisao(janela);
                            System.out.println(Tema.ok("Janela atualizada."));
                        }
                    }
                    case "4" -> {
                        configuracao.restaurarPadroes();
                        System.out.println(Tema.ok("Valores padrão restaurados."));
                    }
                    case "0" -> voltar = true;
                    default -> System.out.println(Tema.aviso("Opção inválida."));
                }
            } catch (DadosInvalidosException e) {
                System.out.println(Tema.erro("Dado recusado: " + e.getMessage()));
            }
            System.out.println();
        }
    }

    private void alterarLimiares(DadosTerritorio dados) {
        TipoSensor[] tipos = TipoSensor.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println(Tema.ok("  " + (i + 1)) + " - " + tipos[i].getNome() + " (" + tipos[i].getUnidade() + ")");
        }
        Integer escolha = lerInteiro("Tipo de sensor: ");
        if (escolha == null) {
            return;
        }
        if (escolha < 1 || escolha > tipos.length) {
            System.out.println(Tema.aviso("Tipo inexistente."));
            return;
        }
        Double atencao = lerDecimal("Novo limiar de ATENÇÃO: ");
        Double alerta = atencao == null ? null : lerDecimal("Novo limiar de ALERTA: ");
        Double critico = alerta == null ? null : lerDecimal("Novo limiar CRÍTICO: ");
        if (critico != null) {
            dados.setLimiares(tipos[escolha - 1], atencao, alerta, critico);
            System.out.println(Tema.ok("Limiares atualizados."));
        }
    }

    // ---------- 6. Status completo ----------

    private void statusCompleto() {
        System.out.println(Tema.titulo("STATUS COMPLETO"));
        try {
            System.out.println(servico.gerarRelatorio(componentes));
        } catch (FonteIndisponivelException e) {
            System.out.println(Tema.erro("Não foi possível montar o relatório: " + e.getMessage()));
        }
    }

    // ---------- 7. Bônus: CSV ----------

    private void exportarCsv() {
        System.out.println(Tema.titulo("EXPORTAR HISTÓRICO PARA CSV"));
        Sensor sensor = escolherSensor();
        if (sensor == null) {
            return;
        }
        Path destino = Path.of("historico_" + sensor.getId() + ".csv");
        try {
            int total = servico.exportarHistoricoCsv(sensor.getId(), destino);
            System.out.println(Tema.ok("Arquivo gerado: " + destino.toAbsolutePath() + " (" + total + " medições)"));
        } catch (FonteIndisponivelException e) {
            System.out.println(Tema.erro("Falha na exportação: " + e.getMessage()));
        }
    }

    // ---------- Leitura segura do console ----------

    private Integer lerInteiro(String pergunta) {
        System.out.print(Tema.destaque(pergunta));
        String texto = entrada.nextLine().trim();
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            System.out.println(Tema.aviso("\"" + texto + "\" não é um número inteiro válido."));
            return null;
        }
    }

    private Double lerDecimal(String pergunta) {
        System.out.print(Tema.destaque(pergunta));
        String texto = entrada.nextLine().trim().replace(',', '.');
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            System.out.println(Tema.aviso("\"" + texto + "\" não é um número válido."));
            return null;
        }
    }
}
