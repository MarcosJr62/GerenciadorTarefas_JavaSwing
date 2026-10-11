package Gerenciador.dao;

import Gerenciador.entity.Tarefa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarefaDAO {

    public List<Tarefa> listar(int idUsuario) throws SQLException {
        List<Tarefa> lista = new ArrayList<>();
        String sql = "SELECT id, titulo, descricao, prioridade_id, status_id, data_criacao, data_vencimento, usuario_id, categoria_id " +
                "FROM tarefa WHERE usuario_id = ? ORDER BY id";

        try (Connection conn = ConexaoBD.abrir();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearTarefa(rs));
                }
            }
        }
        return lista;
    }

    public void inserir(Tarefa tarefa) throws SQLException {
        String sql = "INSERT INTO tarefa (titulo, descricao, prioridade_id, status_id, data_vencimento, usuario_id, categoria_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBD.abrir();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, tarefa.getTitulo());
            ps.setString(2, tarefa.getDescricao());
            ps.setInt(3, parseId(tarefa.getPrioridade(), 1));
            ps.setInt(4, parseId(tarefa.getStatus(), 1));

            if (tarefa.getDataVencimento() != null) {
                ps.setDate(5, new java.sql.Date(tarefa.getDataVencimento().getTime()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setInt(6, tarefa.getIdUsuario());

            if (tarefa.getIdCategoria() > 0) {
                ps.setInt(7, tarefa.getIdCategoria());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    tarefa.setIdTarefa(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Tarefa tarefa) throws SQLException {
        String sql = "UPDATE tarefa SET titulo = ?, descricao = ?, prioridade_id = ?, status_id = ?, data_vencimento = ?, categoria_id = ? " +
                "WHERE id = ? AND usuario_id = ?";
        try (Connection conn = ConexaoBD.abrir();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tarefa.getTitulo());
            ps.setString(2, tarefa.getDescricao());
            ps.setInt(3, parseId(tarefa.getPrioridade(), 1));
            ps.setInt(4, parseId(tarefa.getStatus(), 1));

            if (tarefa.getDataVencimento() != null) {
                ps.setDate(5, new java.sql.Date(tarefa.getDataVencimento().getTime()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            if (tarefa.getIdCategoria() > 0) {
                ps.setInt(6, tarefa.getIdCategoria());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setInt(7, tarefa.getIdTarefa());
            ps.setInt(8, tarefa.getIdUsuario());
            ps.executeUpdate();
        }
    }

    public void concluir(int idTarefa, int idUsuario) throws SQLException {
        String sql = "UPDATE tarefa SET status_id = 3 WHERE id = ? AND usuario_id = ?";
        try (Connection conn = ConexaoBD.abrir();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTarefa);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }

    public void excluir(int idTarefa, int idUsuario) throws SQLException {
        String sql = "DELETE FROM tarefa WHERE id = ? AND usuario_id = ?";
        try (Connection conn = ConexaoBD.abrir();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTarefa);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }

    private int parseId(String valor, int padrao) {
        if (valor == null || valor.trim().isEmpty()) {
            return padrao;
        }
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            switch (valor.trim().toLowerCase()) {
                case "baixa": return 1;
                case "média":
                case "media": return 2;
                case "alta": return 3;
                case "pendente": return 1;
                case "em andamento": return 2;
                case "concluída":
                case "concluida": return 3;
                default: return padrao;
            }
        }
    }

    private Tarefa mapearTarefa(ResultSet rs) throws SQLException {
        Tarefa t = new Tarefa();
        t.setIdTarefa(rs.getInt("id"));
        t.setTitulo(rs.getString("titulo"));
        t.setDescricao(rs.getString("descricao"));
        t.setPrioridade(String.valueOf(rs.getInt("prioridade_id")));
        t.setStatus(String.valueOf(rs.getInt("status_id")));

        Timestamp dataCriacao = rs.getTimestamp("data_criacao");
        if (dataCriacao != null) {
            t.setDataCriacao(new java.util.Date(dataCriacao.getTime()));
        }

        Date dataVencimento = rs.getDate("data_vencimento");
        if (dataVencimento != null) {
            t.setDataVencimento(new java.util.Date(dataVencimento.getTime()));
        }

        t.setIdUsuario(rs.getInt("usuario_id"));
        t.setIdCategoria(rs.getInt("categoria_id"));
        return t;
    }
}
