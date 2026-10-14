package Gerenciador.view;

import Gerenciador.entity.Categoria;
import Gerenciador.entity.Tarefa;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class TelaCadastroTarefa extends JDialog {

    public final JTextField txtTitulo = new JTextField(20);
    public final JTextArea txtDescricao = new JTextArea(5, 20);
    public final JComboBox<String> cbPrioridade = new JComboBox<>(new String[]{"Alta", "Média", "Baixa"});
    public final JComboBox<Categoria> cbCategoria = new JComboBox<>();
    public final JFormattedTextField txtDataLimite;
    public final JComboBox<String> cbStatus =
            new JComboBox<>(new String[]{"Pendente", "Em andamento", "Concluída"});
    public final JButton btSalvar = new JButton("SALVAR");
    public final JButton btCancelar = new JButton("CANCELAR");

    private final JLabel lblTitulo = new JLabel("NOVA TAREFA", SwingConstants.CENTER);

    public TelaCadastroTarefa(JFrame pai, Icon fotoPerfil) {
        super(pai, "Nova Tarefa", true);

        JFormattedTextField data;
        try {
            MaskFormatter mascara = new MaskFormatter("##/##/##");
            mascara.setPlaceholderCharacter('_');
            data = new JFormattedTextField(mascara);
        } catch (ParseException e) {
            data = new JFormattedTextField();
        }
        data.setColumns(8);
        txtDataLimite = data;

        cbPrioridade.setSelectedItem("Média");

        JPanel cabecalho = cabecalho(fotoPerfil);

        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        linha(form, 0, "Título:", txtTitulo, true);
        linha(form, 1, "Descrição:", new JScrollPane(txtDescricao), true);
        linha(form, 2, "Prioridade:", cbPrioridade, false);
        linha(form, 3, "Categoria:", cbCategoria, false);
        linha(form, 4, "Data limite:", txtDataLimite, false);
        linha(form, 5, "Status:", cbStatus, false);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        botoes.add(btSalvar);
        botoes.add(btCancelar);

        add(cabecalho, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(380, 0));
        setLocationRelativeTo(pai);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btCancelar.addActionListener(e -> dispose());
    }

    public void carregarCategorias(List<Categoria> categorias) {
        cbCategoria.removeAllItems();
        cbCategoria.addItem(new Categoria(0, "(Sem categoria)", ""));
        for (Categoria c : categorias) {
            cbCategoria.addItem(c);
        }
    }

    public void preencher(Tarefa t) {
        setTitle("Editar Tarefa");
        lblTitulo.setText("EDITAR TAREFA");
        txtTitulo.setText(t.getTitulo());
        txtDescricao.setText(t.getDescricao());
        cbPrioridade.setSelectedItem(t.getPrioridade());
        cbStatus.setSelectedItem(t.getStatus());
        for (int i = 0; i < cbCategoria.getItemCount(); i++) {
            if (cbCategoria.getItemAt(i).getIdCategoria() == t.getIdCategoria()) {
                cbCategoria.setSelectedIndex(i);
                break;
            }
        }
        if (t.getDataVencimento() != null) {
            txtDataLimite.setText(new SimpleDateFormat("dd/MM/yy").format(t.getDataVencimento()));
        }
    }

    private JPanel cabecalho(Icon fotoPerfil) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painel.add(lblTitulo, BorderLayout.CENTER);
        if (fotoPerfil != null) {
            painel.add(new JLabel(fotoPerfil), BorderLayout.EAST);
            painel.add(Box.createHorizontalStrut(fotoPerfil.getIconWidth()), BorderLayout.WEST);
        }
        return painel;
    }

    private void linha(JPanel p, int y, String rotulo, JComponent campo, boolean expandir) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridy = y;
        c.anchor = GridBagConstraints.NORTHWEST;

        c.gridx = 0;
        p.add(new JLabel(rotulo), c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = expandir ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
        p.add(campo, c);
    }
}
