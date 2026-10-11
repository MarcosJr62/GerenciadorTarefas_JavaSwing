package Gerenciador.view;
import Gerenciador.entity.Categoria;
import Gerenciador.entity.Tarefa;
import Gerenciador.usecase.CategoriaUseCase;
import Gerenciador.usecase.RegraNegocioException;
import Gerenciador.usecase.TarefaUseCase;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/** Liga a TelaPrincipal e a TelaCadastroTarefa ao banco: lista, filtra, cria, edita, conclui e exclui. */
public final class ControleTarefas {

    private final TelaPrincipal tela;
    private final ControleFotoPerfil fotoPerfil;
    private final TarefaUseCase tarefaUseCase = new TarefaUseCase();
    private final CategoriaUseCase categoriaUseCase = new CategoriaUseCase();

    private List<Tarefa> todas = new ArrayList<>();
    private List<Categoria> categorias = new ArrayList<>();

    public ControleTarefas(TelaPrincipal tela, ControleFotoPerfil fotoPerfil) {
        this.tela = tela;
        this.fotoPerfil = fotoPerfil;
    }

    /** Liga os botões e filtros e carrega a lista pela primeira vez. */
    public void ligar() {
        tela.btNovaTarefa.addActionListener(e -> abrirFormulario(null));
        tela.btEditar.addActionListener(e -> {
            Tarefa t = selecionada("editar");
            if (t != null) abrirFormulario(t);
        });
        tela.btConcluir.addActionListener(e -> concluir());
        tela.btExcluir.addActionListener(e -> excluir());

        tela.cbStatus.addActionListener(e -> aplicarFiltros());
        tela.cbPrioridade.addActionListener(e -> aplicarFiltros());
        tela.txtPesquisar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void removeUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void changedUpdate(DocumentEvent e) { aplicarFiltros(); }
        });
        atualizar();
    }

    /** Recarrega tarefas e categorias do banco e redesenha a tabela. */
    public void atualizar() {
        new SwingWorker<Void, Void>() {
            private List<Tarefa> tarefas;
            private List<Categoria> cats;

            @Override
            protected Void doInBackground() throws Exception {
                tarefas = tarefaUseCase.listar();
                cats = categoriaUseCase.listar();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    todas = tarefas;
                    categorias = cats;
                    aplicarFiltros();
                } catch (ExecutionException e) {
                    JOptionPane.showMessageDialog(tela, MensagemErro.paraTela(e.getCause()),
                            "Tarefas", JOptionPane.WARNING_MESSAGE);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    private void aplicarFiltros() {
        String status = (String) tela.cbStatus.getSelectedItem();
        String prioridade = (String) tela.cbPrioridade.getSelectedItem();
        String busca = tela.txtPesquisar.getText().trim().toLowerCase();

        tela.modelo.setRowCount(0);
        for (Tarefa t : todas) {
            if (!"Todas".equals(status) && !t.getStatus().equals(status)) continue;
            if (!"Todas".equals(prioridade) && !t.getPrioridade().equals(prioridade)) continue;
            if (!busca.isEmpty()) {
                String titulo = t.getTitulo().toLowerCase();
                String desc = t.getDescricao() == null ? "" : t.getDescricao().toLowerCase();
                if (!titulo.contains(busca) && !desc.contains(busca)) continue;
            }
            tela.modelo.addRow(new Object[]{t.getIdTarefa(), t.getTitulo(), t.getPrioridade(),
                    nomeCategoria(t.getIdCategoria()), t.getStatus()});
        }
    }

    private String nomeCategoria(int id) {
        for (Categoria c : categorias) {
            if (c.getId() == id) return c.getNome();
        }
        return "";
    }

    private Tarefa selecionada(String acao) {
        int linha = tela.tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(tela, "Selecione uma tarefa para " + acao + ".",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int id = (int) tela.modelo.getValueAt(linha, 0);
        for (Tarefa t : todas) {
            if (t.getIdTarefa() == id) return t;
        }
        return null;
    }

    private void abrirFormulario(Tarefa existente) {
        if (categorias.isEmpty()) {
            JOptionPane.showMessageDialog(tela,
                    "Cadastre ao menos uma categoria (menu Telas > Categorias) antes de criar tarefas.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        TelaCadastroTarefa form = new TelaCadastroTarefa(tela, fotoPerfil.getFotoOuAvatar());
        form.carregarCategorias(categorias);
        if (existente != null) {
            form.preencher(existente);
        }
        form.btSalvar.addActionListener(e -> salvar(form, existente));
        form.setVisible(true);
    }

    private void salvar(TelaCadastroTarefa form, Tarefa existente) {
        Tarefa t = new Tarefa();
        t.setIdTarefa(existente == null ? 0 : existente.getIdTarefa());
        t.setTitulo(form.txtTitulo.getText());
        t.setDescricao(form.txtDescricao.getText());
        t.setPrioridade((String) form.cbPrioridade.getSelectedItem());
        t.setStatus((String) form.cbStatus.getSelectedItem());
        Categoria cat = (Categoria) form.cbCategoria.getSelectedItem();
        t.setIdCategoria(cat == null ? 0 : cat.getId());

        try {
            t.setDataVencimento(lerData(form.txtDataLimite.getText()));
        } catch (RegraNegocioException ex) {
            JOptionPane.showMessageDialog(form, ex.getMessage(), "Nova Tarefa", JOptionPane.WARNING_MESSAGE);
            return;
        }

        form.btSalvar.setEnabled(false);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                tarefaUseCase.salvar(t);
                return null;
            }

            @Override
            protected void done() {
                form.btSalvar.setEnabled(true);
                try {
                    get();
                    form.dispose();
                    atualizar();
                } catch (ExecutionException e) {
                    JOptionPane.showMessageDialog(form, MensagemErro.paraTela(e.getCause()),
                            "Tarefa", JOptionPane.WARNING_MESSAGE);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    private void concluir() {
        Tarefa t = selecionada("concluir");
        if (t == null) return;
        if ("Concluída".equals(t.getStatus())) {
            JOptionPane.showMessageDialog(tela, "Esta tarefa já está concluída.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        executar(() -> {
            tarefaUseCase.concluir(t.getIdTarefa());
            return null;
        });
    }

    private void excluir() {
        Tarefa t = selecionada("excluir");
        if (t == null) return;
        int ok = JOptionPane.showConfirmDialog(tela,
                "Deseja realmente excluir a tarefa '" + t.getTitulo() + "'?",
                "Confirmação", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        executar(() -> {
            tarefaUseCase.excluir(t.getIdTarefa());
            return null;
        });
    }

    /** Roda a ação fora da tela e, se der certo, recarrega a tabela. */
    private void executar(Callable<Void> acao) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                return acao.call();
            }

            @Override
            protected void done() {
                try {
                    get();
                    atualizar();
                } catch (ExecutionException e) {
                    JOptionPane.showMessageDialog(tela, MensagemErro.paraTela(e.getCause()),
                            "Tarefas", JOptionPane.WARNING_MESSAGE);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    /** Lê dd/MM/yy da máscara; campo vazio (só "_" e "/") vira null. */
    private static Date lerData(String texto) throws RegraNegocioException {
        if (texto == null || texto.contains("_")) {
            if (texto == null || texto.replaceAll("[_/]", "").isEmpty()) return null;
            throw new RegraNegocioException("Data limite incompleta. Use o formato dd/mm/aa.");
        }
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yy");
        f.setLenient(false);
        try {
            return f.parse(texto);
        } catch (ParseException e) {
            throw new RegraNegocioException("Data limite inválida. Use o formato dd/mm/aa.");
        }
    }
}