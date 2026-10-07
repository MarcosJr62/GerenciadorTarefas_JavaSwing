import Gerenciador.entity.Usuario;
import Gerenciador.usecase.SenhaHash;
import Gerenciador.usecase.UsuarioUseCase;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class TelaLogin extends JFrame {

    public final JTextField txtUsuario = new JTextField(16);
    public final JPasswordField txtSenha = new JPasswordField(16);
    public final JButton btEntrar = new JButton("Entrar");
    public final JButton btCriarConta = new JButton("Criar Conta");
    private final JLabel lblMensagem = new JLabel(" ", SwingConstants.CENTER);

    private final UsuarioUseCase usuarioUseCase = new UsuarioUseCase();
    private final Consumer<Usuario> aoEntrar;

    public TelaLogin(Consumer<Usuario> aoEntrar) {
        super("Gerenciador de Tarefas - Login");
        this.aoEntrar = aoEntrar;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        conteudo.add(centralizado(cabecalho()));
        conteudo.add(Box.createVerticalStrut(15));
        conteudo.add(centralizado(new JLabel(new IconeUsuario(48))));
        conteudo.add(Box.createVerticalStrut(15));
        conteudo.add(centralizado(formulario()));
        conteudo.add(Box.createVerticalStrut(8));
        lblMensagem.setForeground(new Color(0xB00020));
        conteudo.add(centralizado(lblMensagem));
        conteudo.add(Box.createVerticalStrut(8));
        conteudo.add(centralizado(botoes()));
        add(conteudo);

        getRootPane().setDefaultButton(btEntrar);
        btEntrar.addActionListener(e -> entrar());
        btCriarConta.addActionListener(e ->
                ControleCadastroUsuario.abrir(this, loginCriado -> {
                    txtUsuario.setText(loginCriado);
                    txtSenha.requestFocusInWindow();
                }));

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void entrar() {
        String login = txtUsuario.getText();
        char[] senha = txtSenha.getPassword();
        txtSenha.setText("");
        lblMensagem.setText(" ");
        ocupado(true);

        new SwingWorker<Usuario, Void>() {
            @Override
            protected Usuario doInBackground() throws Exception {
                try {
                    return usuarioUseCase.autenticar(login, senha);
                } finally {
                    SenhaHash.limpar(senha);
                }
            }

            @Override
            protected void done() {
                ocupado(false);
                try {
                    Usuario usuario = get();
                    dispose();
                    aoEntrar.accept(usuario);
                } catch (ExecutionException e) {
                    lblMensagem.setText(MensagemErro.paraTela(e.getCause()));
                    pack();
                    txtSenha.requestFocusInWindow();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    private void ocupado(boolean sim) {
        btEntrar.setEnabled(!sim);
        btCriarConta.setEnabled(!sim);
        btEntrar.setText(sim ? "Entrando..." : "Entrar");
        setCursor(sim ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    private JComponent cabecalho() {
        JLabel titulo = new JLabel("<html><center>TASK MANAGER<br>Gerenciador de Tarefas</center></html>",
                SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 14f));
        titulo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        return titulo;
    }

    private JComponent formulario() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.EAST;
        c.gridx = 0;
        c.gridy = 0;
        form.add(new JLabel("Usuário:"), c);
        c.gridy = 1;
        form.add(new JLabel("Senha:"), c);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 1;
        c.gridy = 0;
        form.add(txtUsuario, c);
        c.gridy = 1;
        form.add(txtSenha, c);
        return form;
    }

    private JComponent botoes() {
        JPanel painel = new JPanel(new GridLayout(2, 1, 0, 10));
        painel.add(btEntrar);
        painel.add(btCriarConta);
        return painel;
    }

    private static JComponent centralizado(JComponent componente) {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        linha.add(componente);
        return linha;
    }

    /** Ícone de usuário desenhado no código, para não depender de arquivo de imagem. */
    private static final class IconeUsuario implements Icon {
        private final int tamanho;

        IconeUsuario(int tamanho) {
            this.tamanho = tamanho;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(3f));
            float t = tamanho;
            g2.draw(new Ellipse2D.Float(x + 2, y + 2, t - 4, t - 4));
            g2.draw(new Ellipse2D.Float(x + t * 0.35f, y + t * 0.2f, t * 0.3f, t * 0.3f));
            Shape circulo = new Ellipse2D.Float(x + 2, y + 2, t - 4, t - 4);
            g2.setClip(circulo);
            g2.draw(new Ellipse2D.Float(x + t * 0.2f, y + t * 0.58f, t * 0.6f, t * 0.6f));
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return tamanho;
        }

        @Override
        public int getIconHeight() {
            return tamanho;
        }
    }
}
