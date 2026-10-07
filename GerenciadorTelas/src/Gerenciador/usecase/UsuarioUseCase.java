package Gerenciador.usecase;

import Gerenciador.dao.UsuarioDAO;
import Gerenciador.dao.UsuarioDAO.DadosLogin;
import Gerenciador.entity.Usuario;

import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Pattern;

public class UsuarioUseCase {

    public static final int MAX_TENTATIVAS_LOGIN = 5;
    public static final int SEGUNDOS_BLOQUEIO_LOGIN = 5 * 60;

    static final String MSG_LOGIN_INVALIDO = "Usuário ou senha inválidos.";

    private static final int SENHA_MIN = 8;
    // Limite superior evita que uma senha gigante deixe o PBKDF2 lento de propósito.
    private static final int SENHA_MAX = 128;
    private static final Pattern LOGIN_VALIDO = Pattern.compile("^[A-Za-z0-9._-]{3,50}$");
    private static final Pattern EMAIL_VALIDO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern TEM_LETRA = Pattern.compile("\\p{L}");
    private static final Pattern TEM_NUMERO = Pattern.compile("\\d");
    private static final String SQLSTATE_VALOR_DUPLICADO = "23505";

    private final UsuarioDAO dao;

    public UsuarioUseCase() {
        this(new UsuarioDAO());
    }

    UsuarioUseCase(UsuarioDAO dao) {
        this.dao = dao;
    }

    public Usuario cadastrar(String nome, String email, String login, char[] senha)
            throws RegraNegocioException, SQLException {
        String nomeLimpo = validarNome(nome);
        String emailLimpo = validarEmail(email);
        String loginLimpo = validarLogin(login);
        validarSenha(senha, loginLimpo);

        if (dao.loginExiste(loginLimpo)) {
            throw new RegraNegocioException("Este usuário já está em uso. Escolha outro.");
        }
        if (dao.emailExiste(emailLimpo)) {
            throw new RegraNegocioException("Este e-mail já está cadastrado.");
        }

        String hash = SenhaHash.gerar(senha);
        try {
            int id = dao.inserir(nomeLimpo, emailLimpo, loginLimpo, hash);
            return new Usuario(id, nomeLimpo, emailLimpo, loginLimpo);
        } catch (SQLException e) {
            // Outro cadastro igual pode ter entrado entre a checagem acima e o INSERT.
            if (SQLSTATE_VALOR_DUPLICADO.equals(e.getSQLState())) {
                throw new RegraNegocioException("Usuário ou e-mail já cadastrado.");
            }
            throw e;
        }
    }

    /**
     * Mesma mensagem para usuário inexistente e senha errada, para a tela não revelar
     * quais logins existem. Após MAX_TENTATIVAS_LOGIN erros seguidos, bloqueia o login.
     */
    public Usuario autenticar(String login, char[] senha) throws RegraNegocioException, SQLException {
        if (login == null || login.isBlank() || senha == null || senha.length == 0) {
            throw new RegraNegocioException("Informe o usuário e a senha.");
        }
        if (senha.length > SENHA_MAX || login.length() > 50) {
            throw new RegraNegocioException(MSG_LOGIN_INVALIDO);
        }

        Optional<DadosLogin> encontrado = dao.buscarParaLogin(login.trim());
        if (encontrado.isEmpty()) {
            // Calcula um hash mesmo assim, para o tempo de resposta não denunciar que o login não existe.
            SenhaHash.verificar(senha, HashFicticio.VALOR);
            throw new RegraNegocioException(MSG_LOGIN_INVALIDO);
        }

        DadosLogin dados = encontrado.get();
        int idUsuario = dados.usuario().getIdUsuario();
        if (dados.segundosBloqueado() > 0) {
            throw new RegraNegocioException(mensagemBloqueio(dados.segundosBloqueado()));
        }
        if (!SenhaHash.verificar(senha, dados.senhaHash())) {
            dao.registrarFalhaLogin(idUsuario, MAX_TENTATIVAS_LOGIN, SEGUNDOS_BLOQUEIO_LOGIN);
            throw new RegraNegocioException(MSG_LOGIN_INVALIDO);
        }

        dao.registrarLoginComSucesso(idUsuario);
        return dados.usuario();
    }

    static String mensagemBloqueio(int segundos) {
        int minutos = (segundos + 59) / 60;
        return "Muitas tentativas incorretas. Tente novamente em " + minutos
                + (minutos == 1 ? " minuto." : " minutos.");
    }

    private static String validarNome(String nome) throws RegraNegocioException {
        String limpo = nome == null ? "" : nome.strip();
        if (limpo.isEmpty()) {
            throw new RegraNegocioException("Informe o nome.");
        }
        if (limpo.length() > 100) {
            throw new RegraNegocioException("O nome pode ter no máximo 100 caracteres.");
        }
        if (limpo.chars().anyMatch(Character::isISOControl)) {
            throw new RegraNegocioException("O nome contém caracteres inválidos.");
        }
        return limpo;
    }

    private static String validarEmail(String email) throws RegraNegocioException {
        String limpo = email == null ? "" : email.strip();
        if (limpo.length() > 150 || !EMAIL_VALIDO.matcher(limpo).matches()) {
            throw new RegraNegocioException("Informe um e-mail válido.");
        }
        return limpo;
    }

    private static String validarLogin(String login) throws RegraNegocioException {
        String limpo = login == null ? "" : login.strip();
        if (!LOGIN_VALIDO.matcher(limpo).matches()) {
            throw new RegraNegocioException(
                    "O usuário deve ter de 3 a 50 caracteres: letras, números, ponto, hífen ou sublinhado.");
        }
        return limpo;
    }

    private static void validarSenha(char[] senha, String login) throws RegraNegocioException {
        if (senha == null || senha.length < SENHA_MIN || senha.length > SENHA_MAX) {
            throw new RegraNegocioException("A senha deve ter de " + SENHA_MIN + " a " + SENHA_MAX + " caracteres.");
        }
        CharSequence texto = java.nio.CharBuffer.wrap(senha);
        if (!TEM_LETRA.matcher(texto).find() || !TEM_NUMERO.matcher(texto).find()) {
            throw new RegraNegocioException("A senha deve ter pelo menos uma letra e um número.");
        }
        if (igualIgnorandoMaiusculas(login, senha)) {
            throw new RegraNegocioException("A senha não pode ser igual ao usuário.");
        }
    }

    // Compara sem criar String da senha (String fica na memória e não pode ser apagada).
    private static boolean igualIgnorandoMaiusculas(String texto, char[] senha) {
        if (texto.length() != senha.length) {
            return false;
        }
        for (int i = 0; i < senha.length; i++) {
            if (Character.toLowerCase(texto.charAt(i)) != Character.toLowerCase(senha[i])) {
                return false;
            }
        }
        return true;
    }

    /** Gerado só no primeiro uso, para não atrasar a abertura do programa. */
    private static final class HashFicticio {
        static final String VALOR = SenhaHash.gerar("senha-ficticia-para-tempo-constante".toCharArray());
    }
}
