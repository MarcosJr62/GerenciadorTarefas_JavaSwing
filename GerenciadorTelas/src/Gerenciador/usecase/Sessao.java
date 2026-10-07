package Gerenciador.usecase;

import Gerenciador.entity.Usuario;

/**
 * Usuário logado na aplicação. Os DAOs de tarefa devem filtrar sempre por
 * Sessao.getUsuarioLogado().getIdUsuario(), para um usuário nunca ver tarefas de outro.
 */
public final class Sessao {

    private static Usuario usuarioLogado;

    private Sessao() {
    }

    public static void iniciar(Usuario usuario) {
        usuarioLogado = usuario;
    }

    public static Usuario getUsuarioLogado() {
        if (usuarioLogado == null) {
            throw new IllegalStateException("Nenhum usuário logado.");
        }
        return usuarioLogado;
    }

    public static void encerrar() {
        usuarioLogado = null;
    }
}
