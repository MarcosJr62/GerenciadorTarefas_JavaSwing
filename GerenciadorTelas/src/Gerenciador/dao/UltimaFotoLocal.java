package Gerenciador.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Cópia da foto do último usuário que entrou neste PC, guardada na pasta do usuário do Windows.
 * Existe para a tela de login mostrar a foto antes da autenticação, sem consultar o banco
 * por um login digitado (o que deixaria qualquer um ver a foto de qualquer conta).
 */
public final class UltimaFotoLocal {

    private static final Path ARQUIVO = pastaDados().resolve("ultima-foto-perfil.png");
    // Mesmo limite da coluna foto_perfil no banco.
    private static final long MAX_BYTES = 1024 * 1024;

    private UltimaFotoLocal() {
    }

    public static Optional<byte[]> ler() throws IOException {
        if (!Files.isRegularFile(ARQUIVO) || Files.size(ARQUIVO) > MAX_BYTES) {
            return Optional.empty();
        }
        return Optional.of(Files.readAllBytes(ARQUIVO));
    }

    public static void salvar(byte[] png) throws IOException {
        Files.createDirectories(ARQUIVO.getParent());
        Files.write(ARQUIVO, png);
    }

    public static void apagar() throws IOException {
        Files.deleteIfExists(ARQUIVO);
    }

    private static Path pastaDados() {
        String appData = System.getenv("APPDATA");
        Path base = (appData == null || appData.isBlank()) ? Path.of(System.getProperty("user.home")) : Path.of(appData);
        return base.resolve("GerenciadorTarefas");
    }
}
