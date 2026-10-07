package Gerenciador.usecase;

/** Usuário inexistente ou senha errada; a tela trata de forma especial, sem revelar qual dos dois foi. */
public class CredenciaisInvalidasException extends RegraNegocioException {

    public CredenciaisInvalidasException() {
        super(UsuarioUseCase.MSG_LOGIN_INVALIDO);
    }
}
