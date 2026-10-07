package Gerenciador.usecase;

/** Erro de validação/regra cuja mensagem é segura para mostrar ao usuário na tela. */
public class RegraNegocioException extends Exception {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
