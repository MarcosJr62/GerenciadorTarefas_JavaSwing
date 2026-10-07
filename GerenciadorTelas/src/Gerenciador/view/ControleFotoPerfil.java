import Gerenciador.usecase.UsuarioUseCase;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/** Escolhe a foto de perfil do usuário logado e guarda a versão já carregada durante a sessão. */
public final class ControleFotoPerfil {

    public static final int TAMANHO_ICONE = 48;

    private final UsuarioUseCase usuarioUseCase = new UsuarioUseCase();
    private final Consumer<ImageIcon> aoMudarFoto;
    private ImageIcon foto;

    /** @param aoMudarFoto avisado quando a foto é carregada, trocada ou removida (recebe null sem foto). */
    public ControleFotoPerfil(Consumer<ImageIcon> aoMudarFoto) {
        this.aoMudarFoto = aoMudarFoto;
    }

    private void atualizar(ImageIcon novaFoto) {
        foto = novaFoto;
        aoMudarFoto.accept(novaFoto);
    }

    /** Foto redonda pronta para exibir, ou null se o usuário ainda não escolheu uma. */
    public ImageIcon getFoto() {
        return foto;
    }

    public void carregar() {
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                Optional<byte[]> png = usuarioUseCase.buscarFotoPerfil();
                return png.isPresent() ? paraIcone(png.get()) : null;
            }

            @Override
            protected void done() {
                try {
                    atualizar(get());
                } catch (ExecutionException e) {
                    MensagemErro.paraTela(e.getCause());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    /** Janela com a foto atual e os botões de escolher ou remover. */
    public void abrir(JFrame pai) {
        String escolher = "Escolher foto...";
        String remover = "Remover foto";
        String cancelar = "Cancelar";
        Object[] opcoes = foto == null ? new Object[]{escolher, cancelar} : new Object[]{escolher, remover, cancelar};
        String texto = foto == null ? "Você ainda não tem foto de perfil." : "Esta é sua foto de perfil atual.";

        int escolhida = JOptionPane.showOptionDialog(pai, texto, "Foto de perfil", JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE, foto, opcoes, escolher);
        if (escolhida < 0) {
            return;
        }
        if (opcoes[escolhida] == escolher) {
            escolher(pai);
        } else if (opcoes[escolhida] == remover) {
            remover(pai);
        }
    }

    private void remover(JFrame pai) {
        int confirmacao = JOptionPane.showConfirmDialog(pai, "Remover sua foto de perfil?",
                "Foto de perfil", JOptionPane.YES_NO_OPTION);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }
        executar(pai, () -> {
            usuarioUseCase.removerFotoPerfil();
            return null;
        }, "Foto de perfil removida.");
    }

    private void escolher(JFrame pai) {
        JFileChooser seletor = new JFileChooser();
        seletor.setDialogTitle("Escolher foto de perfil");
        seletor.setAcceptAllFileFilterUsed(false);
        seletor.setFileFilter(new FileNameExtensionFilter("Imagens (PNG, JPG, GIF, BMP)",
                "png", "jpg", "jpeg", "gif", "bmp"));
        if (seletor.showOpenDialog(pai) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path arquivo = seletor.getSelectedFile().toPath();
        executar(pai, () -> paraIcone(usuarioUseCase.alterarFotoPerfil(arquivo)), "Foto de perfil atualizada!");
    }

    /** Roda a alteração fora da tela e, se der certo, troca a foto em memória pelo resultado (null = sem foto). */
    private void executar(JFrame pai, Callable<ImageIcon> alteracao, String mensagemSucesso) {
        pai.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                return alteracao.call();
            }

            @Override
            protected void done() {
                pai.setCursor(Cursor.getDefaultCursor());
                try {
                    atualizar(get());
                    JOptionPane.showMessageDialog(pai, mensagemSucesso,
                            "Foto de perfil", JOptionPane.INFORMATION_MESSAGE);
                } catch (ExecutionException e) {
                    JOptionPane.showMessageDialog(pai, MensagemErro.paraTela(e.getCause()),
                            "Foto de perfil", JOptionPane.WARNING_MESSAGE);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    /** Foto do usuário, ou o ícone de avatar padrão se ele não tiver foto. */
    public Icon getFotoOuAvatar() {
        return foto != null ? foto : new IconeUsuario(TAMANHO_ICONE);
    }

    static ImageIcon paraIcone(byte[] png) throws IOException {
        BufferedImage imagem = ImageIO.read(new ByteArrayInputStream(png));
        if (imagem == null) {
            throw new IOException("Foto de perfil salva está corrompida.");
        }
        return new ImageIcon(recortarCirculo(imagem, TAMANHO_ICONE));
    }

    // Desenha o círculo com antialiasing e depois "pinta" a foto só dentro dele, para a borda não serrilhar.
    private static BufferedImage recortarCirculo(BufferedImage imagem, int tamanho) {
        BufferedImage redonda = new BufferedImage(tamanho, tamanho, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = redonda.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.fill(new Ellipse2D.Float(0, 0, tamanho, tamanho));
            g.setComposite(AlphaComposite.SrcIn);
            g.drawImage(imagem, 0, 0, tamanho, tamanho, null);
        } finally {
            g.dispose();
        }
        return redonda;
    }
}
