import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> {
            TelaPrincipal principal = new TelaPrincipal();

            principal.btNovaTarefa.addActionListener(e ->
                    new TelaCadastroTarefa(principal).setVisible(true));

            javax.swing.JMenuBar barra = new javax.swing.JMenuBar();
            javax.swing.JMenu menu = new javax.swing.JMenu("Telas");
            javax.swing.JMenuItem categorias = new javax.swing.JMenuItem("Categorias");
            javax.swing.JMenuItem usuario = new javax.swing.JMenuItem("Criar conta");
            categorias.addActionListener(e -> new TelaCategorias(principal).setVisible(true));
            usuario.addActionListener(e -> new TelaCadastroUsuario(principal).setVisible(true));
            menu.add(categorias);
            menu.add(usuario);
            barra.add(menu);
            principal.setJMenuBar(barra);
            principal.setVisible(true);
        });
    }
}
