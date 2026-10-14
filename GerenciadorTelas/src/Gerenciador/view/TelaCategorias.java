package Gerenciador.view;

import Gerenciador.entity.Categoria;
import Gerenciador.usecase.CategoriaUseCase;
import Gerenciador.usecase.RegraNegocioException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaCategorias extends JDialog {

    public final JTextField txtNome = new JTextField(18);
    public final JTextField txtDescricao = new JTextField(18);
    public final JButton btCadastrar = new JButton("Cadastrar");
    public final JButton btEditar = new JButton("EDITAR");
    public final JButton btExcluir = new JButton("EXCLUIR");
    public final JButton btFechar = new JButton("FECHAR");

    public final DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Nome", "Descrição"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    public final JTable tabela = new JTable(modelo);

    private final CategoriaUseCase categoriaUseCase;
    private Integer idCategoriaEmEdicao = null;

    public TelaCategorias(JFrame pai) {
        super(pai, "Categorias", true);
        this.categoriaUseCase = new CategoriaUseCase();

        JLabel cabecalho = new JLabel("GERENCIADOR DE CATEGORIAS", SwingConstants.CENTER);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formNova = new JPanel(new GridLayout(2, 2, 8, 8));
        formNova.add(new JLabel("Nome:"));
        formNova.add(txtNome);
        formNova.add(new JLabel("Descrição:"));
        formNova.add(txtDescricao);

        JPanel painelBotoesForm = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        painelBotoesForm.add(btCadastrar);

        JPanel novaContainer = new JPanel(new BorderLayout(8, 8));
        novaContainer.add(formNova, BorderLayout.CENTER);
        novaContainer.add(painelBotoesForm, BorderLayout.SOUTH);
        novaContainer.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(cabecalho, BorderLayout.NORTH);
        topo.add(novaContainer, BorderLayout.CENTER);

        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        botoes.add(btEditar);
        botoes.add(btExcluir);
        botoes.add(btFechar);

        add(topo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        setSize(480, 480);
        setLocationRelativeTo(pai);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btCadastrar.addActionListener(e -> salvarCategoria());
        btEditar.addActionListener(e -> carregarParaEdicao());
        btExcluir.addActionListener(e -> excluirCategoria());
        btFechar.addActionListener(e -> dispose());

        atualizarTabela();
    }

    private void atualizarTabela() {
        modelo.setRowCount(0);
        try {
            List<Categoria> lista = categoriaUseCase.listar();
            for (Categoria c : lista) {
                modelo.addRow(new Object[]{c.getIdCategoria(), c.getNome(), c.getDescricao()});
            }
        } catch (RegraNegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarCategoria() {
        try {
            if (idCategoriaEmEdicao == null) {
                // Cadastrar nova
                categoriaUseCase.cadastrar(txtNome.getText(), txtDescricao.getText());
            } else {
                // Atualizar existente
                categoriaUseCase.atualizar(idCategoriaEmEdicao, txtNome.getText(), txtDescricao.getText());
                idCategoriaEmEdicao = null;
                btCadastrar.setText("Cadastrar");
            }
            txtNome.setText("");
            txtDescricao.setText("");
            atualizarTabela();
        } catch (RegraNegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void carregarParaEdicao() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma categoria para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        idCategoriaEmEdicao = (int) modelo.getValueAt(linhaSelecionada, 0);
        txtNome.setText((String) modelo.getValueAt(linhaSelecionada, 1));
        txtDescricao.setText((String) modelo.getValueAt(linhaSelecionada, 2));
        btCadastrar.setText("Salvar Alterações");
        txtNome.requestFocus();
    }

    private void excluirCategoria() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma categoria para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCategoria = (int) modelo.getValueAt(linhaSelecionada, 0);
        String nome = (String) modelo.getValueAt(linhaSelecionada, 1);

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir a categoria '" + nome + "'?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                categoriaUseCase.excluir(idCategoria);
                txtNome.setText("");
                txtDescricao.setText("");
                idCategoriaEmEdicao = null;
                btCadastrar.setText("Cadastrar");
                atualizarTabela();
            } catch (RegraNegocioException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erro ao Excluir", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
