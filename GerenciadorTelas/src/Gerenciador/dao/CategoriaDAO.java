package Gerenciador.dao;

import Gerenciador.entity.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public void inseri(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categoria (nome, descricao) VALUES (?, ?)";
        try (Connection conn = ConexaoBD.abrir();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, categoria.getNome());
            ps.setString(2, categoria.getDescricao());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    categoria.setIdCategoria(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Categoria categoria) throws SQLException {
        String sql = "UPDATE categoria SET nome = ?, descricao = ? WHERE id = ?";
        try (Connection conn = ConexaoBD.abrir();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, categoria.getNome());
            ps.setString(2, categoria.getDescricao());
            ps.setInt(3, categoria.getIdCategoria());
            ps.executeUpdate();
        }
    }

    public List<Categoria> listarTodas() throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id, nome, descricao FROM categoria ORDER BY id";

        try (Connection conn = ConexaoBD.abrir();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Categoria(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao")
                ));
            }
        }
        return lista;
    }

    public void deletar(int idCategoria) throws SQLException {
        String sql = "DELETE FROM categoria WHERE id = ?";
        try (Connection conn = ConexaoBD.abrir();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);
            ps.executeUpdate();
        }
    }

    public boolean existePorNome(String nome) throws SQLException {
        String sql = "SELECT COUNT(*) FROM categoria WHERE LOWER(nome) = LOWER(?)";
        try (Connection conn = ConexaoBD.abrir();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
