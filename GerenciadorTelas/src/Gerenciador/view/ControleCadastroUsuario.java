import Gerenciador.entity.Usuario;
import Gerenciador.usecase.SenhaHash;
import Gerenciador.usecase.UsuarioUseCase;

import javax.swing.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/** Liga a TelaCadastroUsuario (layout feito pelo grupo) ao cadastro no banco. */
public final class ControleCadastroUsuario {

    private ControleCadastroUsuario() {
    }

    /** @param aoCadastrar recebe o login criado (pode ser null). */
    public static void abrir(JFrame pai, Consumer<String> aoCadastrar) {
        TelaCadastroUsuario tela = new TelaCadastroUsuario(pai);
        UsuarioUseCase usuarioUseCase = new UsuarioUseCase();
        tela.getRootPane().setDefaultButton(tela.btCadastrar);

        tela.btCadastrar.addActionListener(e -> {
            String nome = tela.txtNome.getText();
            String email = tela.txtEmail.getText();
            String login = tela.txtUsuario.getText();
            char[] senha = tela.txtSenha.getPassword();
            tela.btCadastrar.setEnabled(false);

            new SwingWorker<Usuario, Void>() {
                @Override
                protected Usuario doInBackground() throws Exception {
                    try {
                        return usuarioUseCase.cadastrar(nome, email, login, senha);
                    } finally {
                        SenhaHash.limpar(senha);
                    }
                }

                @Override
                protected void done() {
                    tela.btCadastrar.setEnabled(true);
                    try {
                        Usuario criado = get();
                        JOptionPane.showMessageDialog(tela,
                                "Conta criada com sucesso! Faça login com o usuário \"" + criado.getLogin() + "\".",
                                "Criar Conta", JOptionPane.INFORMATION_MESSAGE);
                        tela.dispose();
                        if (aoCadastrar != null) {
                            aoCadastrar.accept(criado.getLogin());
                        }
                    } catch (ExecutionException ex) {
                        JOptionPane.showMessageDialog(tela, MensagemErro.paraTela(ex.getCause()),
                                "Criar Conta", JOptionPane.WARNING_MESSAGE);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }.execute();
        });

        tela.setVisible(true);
    }
}
