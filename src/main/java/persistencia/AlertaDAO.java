// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
package persistencia;

import dominio.Alerta;
import dominio.NivelAlerta;
import dominio.TipoSensor;
import excecao.FonteIndisponivelException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Acesso aos alertas emitidos. Só PreparedStatement e try-with-resources. */
public class AlertaDAO {

    private static final String INSERIR =
            "INSERT INTO alerta (sensor_id, tipo, nivel, valor_previsto, limite, estrategia, mensagem, data_hora) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String LISTAR =
            "SELECT id, sensor_id, tipo, nivel, valor_previsto, limite, estrategia, mensagem, data_hora "
                    + "FROM alerta ORDER BY id DESC";

    public void salvar(Alerta alerta) throws FonteIndisponivelException {
        try (Connection conexao = ConexaoBanco.abrir();
             PreparedStatement ps = conexao.prepareStatement(INSERIR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, alerta.getSensorId());
            ps.setString(2, alerta.getTipo().name());
            ps.setString(3, alerta.getNivel().name());
            ps.setDouble(4, alerta.getValorPrevisto());
            ps.setDouble(5, alerta.getLimite());
            ps.setString(6, alerta.getEstrategia());
            ps.setString(7, alerta.getMensagem());
            ps.setTimestamp(8, Timestamp.valueOf(alerta.getDataHora()));
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    alerta.setId(chaves.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new FonteIndisponivelException("Falha ao gravar o alerta no banco: " + e.getMessage(), e);
        }
    }

    /** Todos os alertas já emitidos, do mais recente para o mais antigo. */
    public List<Alerta> listarTodos() throws FonteIndisponivelException {
        List<Alerta> alertas = new ArrayList<>();
        try (Connection conexao = ConexaoBanco.abrir();
             PreparedStatement ps = conexao.prepareStatement(LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                alertas.add(new Alerta(
                        rs.getLong("id"),
                        rs.getString("sensor_id"),
                        TipoSensor.valueOf(rs.getString("tipo")),
                        NivelAlerta.valueOf(rs.getString("nivel")),
                        rs.getDouble("valor_previsto"),
                        rs.getDouble("limite"),
                        rs.getString("estrategia"),
                        rs.getString("mensagem"),
                        rs.getTimestamp("data_hora").toLocalDateTime()));
            }
        } catch (SQLException e) {
            throw new FonteIndisponivelException("Falha ao listar os alertas: " + e.getMessage(), e);
        }
        return alertas;
    }
}
