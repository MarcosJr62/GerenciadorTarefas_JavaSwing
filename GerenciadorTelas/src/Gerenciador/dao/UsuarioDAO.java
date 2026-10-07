package Gerenciador.dao;

import Gerenciador.entity.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UsuarioDAO {

    /** Dados que só o fluxo de login precisa; o hash nunca sai daqui para a sessão. */
    public record DadosLogin(Usuario usuario, String senhaHash, int segundosBloqueado) {
    }

    public int inserir(String nome, String email, String login, String senhaHash) throws SQLException {
        String sql = "INSERT INTO usuario (nome, email, login, senha_hash) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection c = ConexaoBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, email);
            ps.setString(3, login);
            ps.setString(4, senhaHash);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public boolean loginExiste(String login) throws SQLException {
        return existe("SELECT 1 FROM usuario WHERE lower(login) = lower(?)", login);
    }

    public boolean emailExiste(String email) throws SQLException {
        return existe("SELECT 1 FROM usuario WHERE lower(email) = lower(?)", email);
    }

    // O tempo de bloqueio é calculado com o relógio do banco, não o do PC, para não depender do horário local.
    public Optional<DadosLogin> buscarParaLogin(String login) throws SQLException {
        String sql = """
                SELECT id, nome, email, login, senha_hash,
                       GREATEST(0, CEIL(EXTRACT(EPOCH FROM bloqueado_ate - now())))::int AS segundos_bloqueado
                FROM usuario
                WHERE lower(login) = lower(?)""";
        try (Connection c = ConexaoBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Usuario usuario = new Usuario(rs.getInt("id"), rs.getString("nome"),
                        rs.getString("email"), rs.getString("login"));
                return Optional.of(new DadosLogin(usuario, rs.getString("senha_hash"), rs.getInt("segundos_bloqueado")));
            }
        }
    }

    /**
     * Soma uma falha numa única instrução (sem corrida entre leitura e escrita).
     * Ao atingir o limite, bloqueia o login e zera o contador para a próxima rodada.
     */
    public void registrarFalhaLogin(int idUsuario, int maxTentativas, int segundosBloqueio) throws SQLException {
        String sql = """
                UPDATE usuario SET
                    tentativas_falhas = CASE WHEN tentativas_falhas + 1 >= ? THEN 0 ELSE tentativas_falhas + 1 END,
                    bloqueado_ate     = CASE WHEN tentativas_falhas + 1 >= ?
                                             THEN now() + make_interval(secs => ?) ELSE bloqueado_ate END
                WHERE id = ?""";
        try (Connection c = ConexaoBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, maxTentativas);
            ps.setInt(2, maxTentativas);
            ps.setInt(3, segundosBloqueio);
            ps.setInt(4, idUsuario);
            ps.executeUpdate();
        }
    }

    public void registrarLoginComSucesso(int idUsuario) throws SQLException {
        String sql = "UPDATE usuario SET tentativas_falhas = 0, bloqueado_ate = NULL WHERE id = ?";
        try (Connection c = ConexaoBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    public void salvarFoto(int idUsuario, byte[] png) throws SQLException {
        String sql = "UPDATE usuario SET foto_perfil = ? WHERE id = ?";
        try (Connection c = ConexaoBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBytes(1, png);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }

    public void removerFoto(int idUsuario) throws SQLException {
        String sql = "UPDATE usuario SET foto_perfil = NULL WHERE id = ?";
        try (Connection c = ConexaoBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    public Optional<byte[]> buscarFoto(int idUsuario) throws SQLException {
        String sql = "SELECT foto_perfil FROM usuario WHERE id = ?";
        try (Connection c = ConexaoBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.ofNullable(rs.getBytes(1)) : Optional.empty();
            }
        }
    }

    private boolean existe(String sql, String valor) throws SQLException {
        try (Connection c = ConexaoBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
