package Gerenciador.usecase;

import Gerenciador.dao.TarefaDAO;
import Gerenciador.entity.Tarefa;

import java.sql.SQLException;
import java.util.List;

public class TarefaUseCase {

    private final TarefaDAO tarefaDAO = new TarefaDAO();

    private static int idUsuario() {
        return Sessao.getUsuarioLogado().getIdUsuario();
    }

    public List<Tarefa> listar() throws RegraNegocioException {
        try {
            return tarefaDAO.listar(idUsuario());
        } catch (SQLException e) {
            throw erroBanco(e, "Erro ao buscar as tarefas no banco de dados.");
        }
    }

    /** Cadastra se idTarefa == 0; senão atualiza a tarefa existente. */
    public void salvar(Tarefa tarefa) throws RegraNegocioException {
        validar(tarefa);
        tarefa.setTitulo(tarefa.getTitulo().trim());
        tarefa.setIdUsuario(idUsuario());
        try {
            if (tarefa.getIdTarefa() == 0) {
                tarefaDAO.inserir(tarefa);
            } else {
                tarefaDAO.atualizar(tarefa);
            }
        } catch (SQLException e) {
            throw erroBanco(e, "Erro ao salvar a tarefa no banco de dados.");
        }
    }

    public void concluir(int idTarefa) throws RegraNegocioException {
        try {
            tarefaDAO.concluir(idTarefa, idUsuario());
        } catch (SQLException e) {
            throw erroBanco(e, "Erro ao concluir a tarefa.");
        }
    }

    public void excluir(int idTarefa) throws RegraNegocioException {
        try {
            tarefaDAO.excluir(idTarefa, idUsuario());
        } catch (SQLException e) {
            throw erroBanco(e, "Erro ao excluir a tarefa.");
        }
    }

    private static void validar(Tarefa tarefa) throws RegraNegocioException {
        if (tarefa == null) {
            throw new RegraNegocioException("A tarefa não pode ser nula.");
        }
        if (tarefa.getTitulo() == null || tarefa.getTitulo().trim().isEmpty()) {
            throw new RegraNegocioException("O título da tarefa é obrigatório.");
        }
        if (tarefa.getTitulo().trim().length() > 150) {
            throw new RegraNegocioException("O título pode ter no máximo 150 caracteres.");
        }
        if (tarefa.getPrioridade() == null || tarefa.getPrioridade().trim().isEmpty()) {
            throw new RegraNegocioException("A prioridade da tarefa é obrigatória.");
        }
        if (tarefa.getStatus() == null || tarefa.getStatus().trim().isEmpty()) {
            throw new RegraNegocioException("O status da tarefa é obrigatório.");
        }
    }

    private static RegraNegocioException erroBanco(SQLException e, String mensagem) {
        System.err.println("[ERRO] " + e);
        return new RegraNegocioException(mensagem);
    }
}
