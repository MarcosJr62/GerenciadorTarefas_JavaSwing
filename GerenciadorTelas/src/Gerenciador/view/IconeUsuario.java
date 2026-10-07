import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.Icon;

/** Ícone de usuário desenhado no código, para não depender de arquivo de imagem. */
final class IconeUsuario implements Icon {
    private final int tamanho;

    IconeUsuario(int tamanho) {
        this.tamanho = tamanho;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(3f));
        float t = tamanho;
        g2.draw(new Ellipse2D.Float(x + 2, y + 2, t - 4, t - 4));
        g2.draw(new Ellipse2D.Float(x + t * 0.35f, y + t * 0.2f, t * 0.3f, t * 0.3f));
        Shape circulo = new Ellipse2D.Float(x + 2, y + 2, t - 4, t - 4);
        g2.setClip(circulo);
        g2.draw(new Ellipse2D.Float(x + t * 0.2f, y + t * 0.58f, t * 0.6f, t * 0.6f));
        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return tamanho;
    }

    @Override
    public int getIconHeight() {
        return tamanho;
    }
}
