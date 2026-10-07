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

        ControleFotoPerfil fotoPerfil = new ControleFotoPerfil(principal::mostrarFotoPerfil);
        fotoPerfil.carregar();
        principal.lblFotoPerfil.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                fotoPerfil.abrir(principal);
            }
        });

        principal.btNovaTarefa.addActionListener(e ->
                new TelaCadastroTarefa(principal, fotoPerfil.getFotoOuAvatar()).setVisible(true));

        javax.swing.JMenuBar barra = new javax.swing.JMenuBar();
        javax.swing.JMenu menu = new javax.swing.JMenu("Telas");
        javax.swing.JMenuItem categorias = new javax.swing.JMenuItem("Categorias");
        javax.swing.JMenuItem novoUsuario = new javax.swing.JMenuItem("Criar conta");
        javax.swing.JMenuItem foto = new javax.swing.JMenuItem("Foto de perfil...");
        javax.swing.JMenuItem sair = new javax.swing.JMenuItem("Sair");
        categorias.addActionListener(e -> new TelaCategorias(principal).setVisible(true));
        novoUsuario.addActionListener(e -> ControleCadastroUsuario.abrir(principal, null));
        foto.addActionListener(e -> fotoPerfil.abrir(principal));
        sair.addActionListener(e -> {
            Sessao.encerrar();
            principal.dispose();
            abrirLogin();
        });
        menu.add(categorias);
        menu.add(novoUsuario);
        menu.add(foto);
        menu.addSeparator();
        menu.add(sair);
        barra.add(menu);
        principal.setJMenuBar(barra);
        principal.setVisible(true);
    }
}
