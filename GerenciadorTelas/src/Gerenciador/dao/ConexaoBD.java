package Gerenciador.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Abre conexões JDBC com o PostgreSQL.
 * Configuração por variáveis de ambiente, para a senha nunca ficar no código:
 *   DB_URL      (padrão: jdbc:postgresql://localhost:5432/gerenciador_tarefas)
 *   DB_USER     (padrão: gerenciador_app)
 *   DB_PASSWORD (obrigatória)
 *
 * Quem chama deve fechar a conexão com try-with-resources.
 */
public final class ConexaoBD {

    private static final String URL_PADRAO = "jdbc:postgresql://localhost:5432/gerenciador_tarefas";
    private static final String USUARIO_PADRAO = "gerenciador_app";

    private ConexaoBD() {
    }

    public static Connection abrir() throws SQLException {
        String url = lerVariavel("DB_URL", URL_PADRAO);
        String usuario = lerVariavel("DB_USER", USUARIO_PADRAO);
        String senha = System.getenv("DB_PASSWORD");
        if (senha == null || senha.isBlank()) {
            throw new IllegalStateException("Variável de ambiente DB_PASSWORD não definida.");
        }
        return DriverManager.getConnection(url, usuario, senha);
    }

    private static String lerVariavel(String nome, String padrao) {
        String valor = System.getenv(nome);
        return (valor == null || valor.isBlank()) ? padrao : valor;
    }
}
