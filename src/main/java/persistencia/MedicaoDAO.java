// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package persistencia;

import dominio.Medicao;
import dominio.TipoSensor;
import excecao.FonteIndisponivelException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Acesso ao histórico de medições. Só PreparedStatement e try-with-resources. */
public class MedicaoDAO {

    private static final String INSERIR =
            "INSERT INTO medicao (sensor_id, tipo, valor, data_hora) VALUES (?, ?, ?, ?)";
    private static final String POR_SENSOR =
            "SELECT id, sensor_id, tipo, valor, data_hora FROM medicao WHERE sensor_id = ? ORDER BY id DESC";
    private static final String ULTIMAS =
            "SELECT id, sensor_id, tipo, valor, data_hora FROM medicao WHERE sensor_id = ? ORDER BY id DESC LIMIT ?";

    public void salvar(Medicao medicao) throws FonteIndisponivelException {
        try (Connection conexao = ConexaoBanco.abrir();
             PreparedStatement ps = conexao.prepareStatement(INSERIR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, medicao.getSensorId());
            ps.setString(2, medicao.getTipo().name());
            ps.setDouble(3, medicao.getValor());
            ps.setTimestamp(4, Timestamp.valueOf(medicao.getDataHora()));
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    medicao.setId(chaves.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new FonteIndisponivelException("Falha ao gravar a medição no banco: " + e.getMessage(), e);
        }
    }

    /** Histórico completo de um sensor, da leitura mais recente para a mais antiga. */
    public List<Medicao> buscarPorSensor(String sensorId) throws FonteIndisponivelException {
        try (Connection conexao = ConexaoBanco.abrir();
             PreparedStatement ps = conexao.prepareStatement(POR_SENSOR)) {
            ps.setString(1, sensorId);
            return lerResultados(ps);
        } catch (SQLException e) {
            throw new FonteIndisponivelException("Falha ao consultar o histórico do sensor " + sensorId
                    + ": " + e.getMessage(), e);
        }
    }

    /** As últimas N leituras, em ordem cronológica (da mais antiga para a mais recente). */
    public List<Medicao> buscarUltimasPorSensor(String sensorId, int limite) throws FonteIndisponivelException {
        try (Connection conexao = ConexaoBanco.abrir();
             PreparedStatement ps = conexao.prepareStatement(ULTIMAS)) {
            ps.setString(1, sensorId);
            ps.setInt(2, limite);
            List<Medicao> medicoes = lerResultados(ps);
            Collections.reverse(medicoes);
            return medicoes;
        } catch (SQLException e) {
            throw new FonteIndisponivelException("Falha ao consultar as últimas leituras do sensor "
                    + sensorId + ": " + e.getMessage(), e);
        }
    }

    private List<Medicao> lerResultados(PreparedStatement ps) throws SQLException {
        List<Medicao> medicoes = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                medicoes.add(new Medicao(
                        rs.getLong("id"),
                        rs.getString("sensor_id"),
                        TipoSensor.valueOf(rs.getString("tipo")),
                        rs.getDouble("valor"),
                        rs.getTimestamp("data_hora").toLocalDateTime()));
            }
        }
        return medicoes;
    }
}
