import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TelaCategorias extends JDialog {

    public final JTextField txtNovaCategoria = new JTextField(18);
    public final JButton btAdicionar = new JButton("Adicionar");
    public final JButton btExcluir = new JButton("EXCLUIR");
    public final JButton btFechar = new JButton("FECHAR");

    public final DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Categoria"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    public final JTable tabela = new JTable(modelo);

    public TelaCategorias(JFrame pai) {
        super(pai, "Categorias", true);

        JLabel cabecalho = new JLabel("CATEGORIAS", SwingConstants.CENTER);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel nova = new JPanel(new BorderLayout(8, 4));
        nova.add(new JLabel("Nova Categoria:"), BorderLayout.NORTH);
        nova.add(txtNovaCategoria, BorderLayout.CENTER);
        nova.add(btAdicionar, BorderLayout.EAST);
        nova.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(cabecalho, BorderLayout.NORTH);
        topo.add(nova, BorderLayout.CENTER);

        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        modelo.addRow(new Object[]{"01", "Faculdade"});
        modelo.addRow(new Object[]{"02", "Trabalho"});
        modelo.addRow(new Object[]{"03", "Pessoal"});
        modelo.addRow(new Object[]{"04", "Estudos"});

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        botoes.add(btExcluir);
        botoes.add(btFechar);

        add(topo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        setSize(360, 420);
        setLocationRelativeTo(pai);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btFechar.addActionListener(e -> dispose());
    }
}
