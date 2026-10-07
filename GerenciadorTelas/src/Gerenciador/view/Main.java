import Gerenciador.entity.Usuario;
import Gerenciador.usecase.Sessao;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(Main::abrirLogin);
    }

    private static void abrirLogin() {
        new TelaLogin(Main::abrirPrincipal).setVisible(true);
    }

    private static void abrirPrincipal(Usuario usuario) {
        Sessao.iniciar(usuario);
        TelaPrincipal principal = new TelaPrincipal();
        principal.setTitle("Gerenciador de Tarefas - Olá, " + usuario.getNome() + "!");

        principal.btNovaTarefa.addActionListener(e ->
                new TelaCadastroTarefa(principal).setVisible(true));

        javax.swing.JMenuBar barra = new javax.swing.JMenuBar();
        javax.swing.JMenu menu = new javax.swing.JMenu("Telas");
        javax.swing.JMenuItem categorias = new javax.swing.JMenuItem("Categorias");
        javax.swing.JMenuItem novoUsuario = new javax.swing.JMenuItem("Criar conta");
        javax.swing.JMenuItem sair = new javax.swing.JMenuItem("Sair");
        categorias.addActionListener(e -> new TelaCategorias(principal).setVisible(true));
        novoUsuario.addActionListener(e -> ControleCadastroUsuario.abrir(principal, null));
        sair.addActionListener(e -> {
            Sessao.encerrar();
            principal.dispose();
            abrirLogin();
        });
        menu.add(categorias);
        menu.add(novoUsuario);
        menu.addSeparator();
        menu.add(sair);
        barra.add(menu);
        principal.setJMenuBar(barra);
        principal.setVisible(true);
    }
}
