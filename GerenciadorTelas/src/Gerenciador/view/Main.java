package Gerenciador.view;

import Gerenciador.entity.Usuario;
import Gerenciador.usecase.Sessao;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            new TelaLogin(usuarioLogado -> {
                Sessao.iniciar(usuarioLogado);

                TelaPrincipal telaPrincipal = new TelaPrincipal();
                telaPrincipal.setTitle("Gerenciador de Tarefas - Olá, " + usuarioLogado.getNome() + "!");

                ControleFotoPerfil controleFoto = new ControleFotoPerfil(foto -> telaPrincipal.mostrarFotoPerfil(foto));
                controleFoto.carregar();

                telaPrincipal.lblFotoPerfil.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        controleFoto.abrir(telaPrincipal);
                    }
                });

                ControleTarefas controleTarefas = new ControleTarefas(telaPrincipal, controleFoto);
                controleTarefas.ligar();

                JMenuBar barraMenu = new JMenuBar();
                JMenu menuTelas = new JMenu("Telas");
                JMenuItem itemCategorias = new JMenuItem("Categorias");
                itemCategorias.addActionListener(e -> controleTarefas.abrirCategorias());
                menuTelas.add(itemCategorias);
                barraMenu.add(menuTelas);
                telaPrincipal.setJMenuBar(barraMenu);

                telaPrincipal.setVisible(true);
            }).setVisible(true);
        });
    }
}
