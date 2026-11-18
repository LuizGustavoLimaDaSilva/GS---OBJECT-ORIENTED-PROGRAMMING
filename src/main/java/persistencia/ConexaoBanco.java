// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package persistencia;

import excecao.FonteIndisponivelException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Abre conexões JDBC com o H2 e cria as tabelas na primeira execução.
 * Por padrão grava em arquivo (./data/territorio), então o histórico sobrevive ao reiniciar.
 * Para rodar sem arquivo (ex.: correção rápida): -Dterritorio.db=mem
 */
public final class ConexaoBanco {

    private static final String URL_ARQUIVO = "jdbc:h2:file:./data/territorio;DB_CLOSE_DELAY=-1";
    private static final String URL_MEMORIA = "jdbc:h2:mem:territorio;DB_CLOSE_DELAY=-1";

    private static final String CRIAR_MEDICAO =
            "CREATE TABLE IF NOT EXISTS medicao ("
                    + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                    + "sensor_id VARCHAR(30) NOT NULL, "
                    + "tipo VARCHAR(30) NOT NULL, "
                    + "valor DOUBLE NOT NULL, "
                    + "data_hora TIMESTAMP NOT NULL)";

    private static final String CRIAR_ALERTA =
            "CREATE TABLE IF NOT EXISTS alerta ("
                    + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                    + "sensor_id VARCHAR(30) NOT NULL, "
                    + "tipo VARCHAR(30) NOT NULL, "
                    + "nivel VARCHAR(20) NOT NULL, "
                    + "valor_previsto DOUBLE NOT NULL, "
                    + "limite DOUBLE NOT NULL, "
                    + "estrategia VARCHAR(80) NOT NULL, "
                    + "mensagem VARCHAR(600) NOT NULL, "
                    + "data_hora TIMESTAMP NOT NULL)";

    private static final String CRIAR_INDICE =
            "CREATE INDEX IF NOT EXISTS idx_medicao_sensor ON medicao (sensor_id)";

    private ConexaoBanco() {
    }

    private static String urlAtual() {
        String urlExplicita = System.getProperty("territorio.db.url");
        if (urlExplicita != null && !urlExplicita.isBlank()) {
            return urlExplicita;
        }
        return "mem".equalsIgnoreCase(System.getProperty("territorio.db")) ? URL_MEMORIA : URL_ARQUIVO;
    }

    public static Connection abrir() throws FonteIndisponivelException {
        try {
            return DriverManager.getConnection(urlAtual(), "sa", "");
        } catch (SQLException e) {
            throw new FonteIndisponivelException("Banco de dados indisponível: " + e.getMessage(), e);
        }
    }

    public static void inicializarEsquema() throws FonteIndisponivelException {
        try (Connection conexao = abrir(); Statement comando = conexao.createStatement()) {
            comando.execute(CRIAR_MEDICAO);
            comando.execute(CRIAR_ALERTA);
            comando.execute(CRIAR_INDICE);
        } catch (SQLException e) {
            throw new FonteIndisponivelException("Não foi possível criar as tabelas: " + e.getMessage(), e);
        }
    }
}
