package Gerenciador.view;

import Gerenciador.entity.Categoria;
import Gerenciador.usecase.CategoriaUseCase;
import Gerenciador.usecase.RegraNegocioException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

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

    private final CategoriaUseCase categoriaUseCase;

    public TelaCategorias(TelaPrincipal pai) {
        super(pai, "Categorias", true);
        this.categoriaUseCase = new CategoriaUseCase();

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

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        botoes.add(btExcluir);
        botoes.add(btFechar);

        add(topo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        setSize(360, 420);
        setLocationRelativeTo(pai);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btAdicionar.addActionListener(e -> adicionarCategoria());
        btExcluir.addActionListener(e -> excluirCategoria());
        btFechar.addActionListener(e -> dispose());

        atualizarTabela();
    }

    private void atualizarTabela() {
        modelo.setRowCount(0);
        try {
            List<Categoria> lista = categoriaUseCase.listar();
            for (Categoria c : lista) {
                modelo.addRow(new Object[]{c.getId(), c.getNome()});
            }
        } catch (RegraNegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adicionarCategoria() {
        try {
            categoriaUseCase.adicionar(txtNovaCategoria.getText());
            txtNovaCategoria.setText("");
            atualizarTabela();
        } catch (RegraNegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluirCategoria() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma categoria para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modelo.getValueAt(linhaSelecionada, 0);
        String nome = (String) modelo.getValueAt(linhaSelecionada, 1);

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir a categoria '" + nome + "'?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                categoriaUseCase.remover(id);
                atualizarTabela();
            } catch (RegraNegocioException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erro ao Excluir", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
