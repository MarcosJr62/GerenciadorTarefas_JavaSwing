import Gerenciador.usecase.RegraNegocioException;

import java.sql.SQLException;

/**
 * Converte exceções em texto seguro para a tela: regra de negócio mostra a própria mensagem;
 * falhas técnicas mostram texto genérico e o detalhe vai só para o console (System.err).
 */
public final class MensagemErro {

    private MensagemErro() {
    }

    public static String paraTela(Throwable erro) {
        if (erro instanceof RegraNegocioException) {
            return erro.getMessage();
        }
        System.err.println("[ERRO] " + erro);
        if (erro instanceof SQLException) {
            return "Não foi possível acessar o banco de dados. Verifique se o PostgreSQL está rodando.";
        }
        if (erro instanceof IllegalStateException && String.valueOf(erro.getMessage()).contains("DB_PASSWORD")) {
            return "Banco de dados não configurado (variável DB_PASSWORD). Veja banco/README.md.";
        }
        return "Ocorreu um erro inesperado. Tente novamente.";
    }
}
