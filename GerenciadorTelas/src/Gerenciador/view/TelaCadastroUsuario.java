import javax.swing.*;
import java.awt.*;

public class TelaCadastroUsuario extends JDialog {

    public final JTextField txtNome = new JTextField(20);
    public final JTextField txtEmail = new JTextField(20);
    public final JTextField txtUsuario = new JTextField(20);
    public final JPasswordField txtSenha = new JPasswordField(20);
    public final JButton btCadastrar = new JButton("CADASTRAR");
    public final JButton btCancelar = new JButton("CANCELAR");

    public TelaCadastroUsuario(JFrame pai) {
        super(pai, "Criar Conta", true);

        JLabel cabecalho = new JLabel("CRIAR CONTA", SwingConstants.CENTER);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        campo(form, 0, "Nome:", txtNome);
        campo(form, 1, "E-mail:", txtEmail);
        campo(form, 2, "Usuário:", txtUsuario);
        campo(form, 3, "Senha:", txtSenha);

        JPanel botoes = new JPanel(new GridLayout(2, 1, 0, 8));
        botoes.add(btCadastrar);
        botoes.add(btCancelar);
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rodape.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        rodape.add(botoes);

        add(cabecalho, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(pai);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btCancelar.addActionListener(e -> dispose());
    }

    private void campo(JPanel p, int y, String rotulo, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = y * 2;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(8, 0, 2, 0);
        p.add(new JLabel(rotulo), c);

        c.gridy = y * 2 + 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 4, 0);
        p.add(campo, c);
    }
}
