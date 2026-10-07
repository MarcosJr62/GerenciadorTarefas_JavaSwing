package Gerenciador.entity;

/**
 * Usuário do sistema. Não carrega a senha nem o hash dela, para que o objeto
 * guardado na sessão nunca exponha a credencial.
 */
public class Usuario {

    private final int idUsuario;
    private final String nome;
    private final String email;
    private final String login;

    public Usuario(int idUsuario, String nome, String email, String login) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.login = login;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }
}
