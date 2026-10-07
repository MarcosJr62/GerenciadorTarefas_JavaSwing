package Gerenciador.usecase;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Hash de senha com PBKDF2-HMAC-SHA256 (nativo do Java, sem biblioteca extra).
 * Formato salvo em usuario.senha_hash: pbkdf2_sha256$iteracoes$saltBase64$hashBase64
 * Guardar as iterações no próprio hash permite aumentá-las no futuro sem invalidar senhas antigas.
 */
public final class SenhaHash {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final String PREFIXO = "pbkdf2_sha256";
    // Valor recomendado pela OWASP para PBKDF2-HMAC-SHA256.
    private static final int ITERACOES = 600_000;
    private static final int TAMANHO_SALT_BYTES = 16;
    private static final int TAMANHO_HASH_BITS = 256;

    private static final SecureRandom RANDOM = new SecureRandom();

    private SenhaHash() {
    }

    public static String gerar(char[] senha) {
        if (senha == null || senha.length == 0) {
            throw new IllegalArgumentException("Senha não pode ser vazia.");
        }
        byte[] salt = new byte[TAMANHO_SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] hash = calcular(senha, salt, ITERACOES);
        Base64.Encoder base64 = Base64.getEncoder();
        return PREFIXO + "$" + ITERACOES + "$" + base64.encodeToString(salt) + "$" + base64.encodeToString(hash);
    }

    /** Retorna false (em vez de lançar exceção) para hash armazenado inválido, para o login só falhar. */
    public static boolean verificar(char[] senha, String hashArmazenado) {
        if (senha == null || senha.length == 0 || hashArmazenado == null) {
            return false;
        }
        String[] partes = hashArmazenado.split("\\$");
        if (partes.length != 4 || !PREFIXO.equals(partes[0])) {
            return false;
        }
        try {
            int iteracoes = Integer.parseInt(partes[1]);
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[3]);
            if (iteracoes <= 0 || salt.length == 0 || hashEsperado.length == 0) {
                return false;
            }
            byte[] hashCalculado = calcular(senha, salt, iteracoes);
            // Comparação em tempo constante, para não vazar informação pelo tempo de resposta.
            return MessageDigest.isEqual(hashCalculado, hashEsperado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] calcular(char[] senha, byte[] salt, int iteracoes) {
        PBEKeySpec spec = new PBEKeySpec(senha, salt, iteracoes, TAMANHO_HASH_BITS);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 indisponível nesta JVM.", e);
        } finally {
            spec.clearPassword();
        }
    }

    /** Apaga a senha da memória depois do uso (ex.: valor de JPasswordField.getPassword()). */
    public static void limpar(char[] senha) {
        if (senha != null) {
            Arrays.fill(senha, '\0');
        }
    }
}
