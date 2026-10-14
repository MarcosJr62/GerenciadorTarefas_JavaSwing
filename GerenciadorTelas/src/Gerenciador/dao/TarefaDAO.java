package Gerenciador.dao;

import Gerenciador.entity.Tarefa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarefaDAO {

    // A tarefa trabalha com o nome ("Alta", "Pendente"); o id é resolvido no banco pelas tabelas de domínio.
    // Nome desconhecido vira NULL e é barrado pelo NOT NULL da coluna.
    private static final String ID_PRIORIDADE_POR_NOME = "(SELECT id FROM prioridade WHERE nome = ?)";
    private static final String ID_STATUS_POR_NOME = "(SELECT id FROM status WHERE nome = ?)";

    public List<Tarefa> listar(int idUsuario) throws SQLException {
        List<Tarefa> lista = new ArrayList<>();
        String sql = "SELECT t.id, t.titulo, t.descricao, p.nome AS prioridade, s.nome AS status, " +
                "t.data_criacao, t.data_vencimento, t.usuario_id, t.categoria_id " +
                "FROM tarefa t " +
                "JOIN prioridade p ON p.id = t.prioridade_id " +
                "JOIN status s ON s.id = t.status_id " +
                "WHERE t.usuario_id = ? ORDER BY t.id";

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
                "VALUES (?, ?, " + ID_PRIORIDADE_POR_NOME + ", " + ID_STATUS_POR_NOME + ", ?, ?, ?)";
        try (Connection conn = ConexaoBD.abrir();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, tarefa.getTitulo());
            ps.setString(2, tarefa.getDescricao());
            ps.setString(3, tarefa.getPrioridade());
            ps.setString(4, tarefa.getStatus());

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
        String sql = "UPDATE tarefa SET titulo = ?, descricao = ?, prioridade_id = " + ID_PRIORIDADE_POR_NOME +
                ", status_id = " + ID_STATUS_POR_NOME + ", data_vencimento = ?, categoria_id = ? " +
                "WHERE id = ? AND usuario_id = ?";
        try (Connection conn = ConexaoBD.abrir();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tarefa.getTitulo());
            ps.setString(2, tarefa.getDescricao());
            ps.setString(3, tarefa.getPrioridade());
            ps.setString(4, tarefa.getStatus());

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
        String sql = "UPDATE tarefa SET status_id = (SELECT id FROM status WHERE nome = 'Concluída') " +
                "WHERE id = ? AND usuario_id = ?";
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

    private Tarefa mapearTarefa(ResultSet rs) throws SQLException {
        Tarefa t = new Tarefa();
        t.setIdTarefa(rs.getInt("id"));
        t.setTitulo(rs.getString("titulo"));
        t.setDescricao(rs.getString("descricao"));
        t.setPrioridade(rs.getString("prioridade"));
        t.setStatus(rs.getString("status"));

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
