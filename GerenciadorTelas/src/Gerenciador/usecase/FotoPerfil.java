package Gerenciador.usecase;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/**
 * Converte o arquivo escolhido pelo usuário numa foto de perfil quadrada em PNG.
 * A imagem é decodificada e gerada de novo, então o banco nunca guarda o arquivo original
 * (nem metadados, nem conteúdo que só finge ser imagem).
 */
public final class FotoPerfil {

    public static final int TAMANHO = 128;

    static final long MAX_BYTES_ARQUIVO = 5L * 1024 * 1024;
    // Evita que uma imagem pequena em bytes, mas enorme em pixels, estoure a memória ao decodificar.
    static final int MAX_LADO_PIXELS = 8000;
    private static final Set<String> FORMATOS_ACEITOS = Set.of("png", "jpeg", "gif", "bmp");
    private static final String MSG_ARQUIVO_INVALIDO = "O arquivo escolhido não é uma imagem válida (use PNG, JPG, GIF ou BMP).";

    private FotoPerfil() {
    }

    public static byte[] preparar(Path arquivo) throws RegraNegocioException, IOException {
        if (!Files.isRegularFile(arquivo)) {
            throw new RegraNegocioException("Arquivo não encontrado.");
        }
        long tamanho = Files.size(arquivo);
        if (tamanho == 0) {
            throw new RegraNegocioException(MSG_ARQUIVO_INVALIDO);
        }
        if (tamanho > MAX_BYTES_ARQUIVO) {
            throw new RegraNegocioException("A imagem deve ter no máximo 5 MB.");
        }
        return paraPng(recortarQuadrado(ler(arquivo)));
    }

    private static BufferedImage ler(Path arquivo) throws RegraNegocioException, IOException {
        try (ImageInputStream entrada = ImageIO.createImageInputStream(arquivo.toFile())) {
            Iterator<ImageReader> leitores = entrada == null ? null : ImageIO.getImageReaders(entrada);
            if (leitores == null || !leitores.hasNext()) {
                throw new RegraNegocioException(MSG_ARQUIVO_INVALIDO);
            }
            ImageReader leitor = leitores.next();
            try {
                if (!FORMATOS_ACEITOS.contains(leitor.getFormatName().toLowerCase(Locale.ROOT))) {
                    throw new RegraNegocioException(MSG_ARQUIVO_INVALIDO);
                }
                leitor.setInput(entrada, true, true);
                if (leitor.getWidth(0) > MAX_LADO_PIXELS || leitor.getHeight(0) > MAX_LADO_PIXELS) {
                    throw new RegraNegocioException("A imagem é grande demais (máximo " + MAX_LADO_PIXELS + " pixels de lado).");
                }
                return leitor.read(0);
            } catch (IIOException | IndexOutOfBoundsException e) {
                throw new RegraNegocioException(MSG_ARQUIVO_INVALIDO);
            } finally {
                leitor.dispose();
            }
        }
    }

    /** Corta o centro da imagem num quadrado e reduz para TAMANHO x TAMANHO. */
    static BufferedImage recortarQuadrado(BufferedImage original) {
        int lado = Math.min(original.getWidth(), original.getHeight());
        int x = (original.getWidth() - lado) / 2;
        int y = (original.getHeight() - lado) / 2;

        BufferedImage destino = new BufferedImage(TAMANHO, TAMANHO, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = destino.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(original, 0, 0, TAMANHO, TAMANHO, x, y, x + lado, y + lado, null);
        } finally {
            g.dispose();
        }
        return destino;
    }

    private static byte[] paraPng(BufferedImage imagem) throws IOException {
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        ImageIO.write(imagem, "png", saida);
        return saida.toByteArray();
    }
}
