package Gerenciador.view;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.Icon;

/** Bolinha colorida ao lado do status na tabela: vermelho, amarelo ou verde. */
final class IconeStatus implements Icon {
    private static final int TAMANHO = 12;
    private static final Icon PENDENTE = new IconeStatus(new Color(0xD32F2F));
    private static final Icon EM_ANDAMENTO = new IconeStatus(new Color(0xF9A825));
    private static final Icon CONCLUIDA = new IconeStatus(new Color(0x388E3C));

    private final Color cor;

    private IconeStatus(Color cor) {
        this.cor = cor;
    }

    /** Null para status desconhecido (a célula fica só com o texto). */
    static Icon para(String status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case "Pendente": return PENDENTE;
            case "Em andamento": return EM_ANDAMENTO;
            case "Concluída": return CONCLUIDA;
            default: return null;
        }
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(cor);
        g2.fill(new Ellipse2D.Float(x, y, TAMANHO, TAMANHO));
        g2.setColor(cor.darker());
        g2.draw(new Ellipse2D.Float(x, y, TAMANHO - 1, TAMANHO - 1));
        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return TAMANHO;
    }

    @Override
    public int getIconHeight() {
        return TAMANHO;
    }
}
