import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TelaPrincipal extends JFrame {

    public final JButton btNovaTarefa = new JButton("Nova Tarefa");
    public final JButton btEditar = new JButton("Editar");
    public final JButton btExcluir = new JButton("Excluir");
    public final JButton btConcluir = new JButton("Concluir");

    public final JComboBox<String> cbStatus =
            new JComboBox<>(new String[]{"Todas", "Pendente", "Em andamento", "Concluída"});
    public final JComboBox<String> cbPrioridade =
            new JComboBox<>(new String[]{"Todas", "Alta", "Média", "Baixa"});
    public final JTextField txtPesquisar = new JTextField(18);

    public final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Título", "Priorid.", "Categoria", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    public final JTable tabela = new JTable(modelo);

    public TelaPrincipal() {
        super("Gerenciador de Tarefas - Olá, Aluno!");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(640, 460);
        setLocationRelativeTo(null);

        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.add(painelBotoes());
        topo.add(painelFiltros());

        tabela.setRowHeight(26);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    private JPanel painelBotoes() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        p.add(btNovaTarefa);
        p.add(btEditar);
        p.add(btExcluir);
        p.add(btConcluir);
        return p;
    }

    private JPanel painelFiltros() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        p.add(new JLabel("Status"));
        p.add(cbStatus);
        p.add(Box.createHorizontalStrut(20));
        p.add(new JLabel("Prioridade"));
        p.add(cbPrioridade);
        p.add(new JLabel("Pesquisar:"));
        p.add(txtPesquisar);
        return p;
    }
}
