package Gerenciador.usecase;

import Gerenciador.dao.TarefaDAO;
import Gerenciador.entity.Tarefa;

import java.util.List;

public class TarefaUseCase {

    private TarefaDAO tarefaDAO;

    public TarefaUseCase() {
        tarefaDAO = new TarefaDAO();
    }

    public void cadastrar(Tarefa tarefa) {
        validarTarefa(tarefa);
        tarefaDAO.cadastrar(tarefa);
    }

    public void atualizar(Tarefa tarefa) {
        validarTarefa(tarefa);
        tarefaDAO.atualizar(tarefa);
    }

    public void excluir(int idTarefa) {
        tarefaDAO.excluir(idTarefa);
    }

    public void concluir(int idTarefa) {
        tarefaDAO.concluir(idTarefa);
    }

    public List<Tarefa> listar() {
        return tarefaDAO.listar();
    }

    public List<Tarefa> filtrarPorStatus(String status) {
        return tarefaDAO.filtrarPorStatus(status);
    }

    public List<Tarefa> filtrarPorPrioridade(String prioridade) {
        return tarefaDAO.filtrarPorPrioridade(prioridade);
    }

    public List<Tarefa> pesquisar(String texto) {
        return tarefaDAO.pesquisar(texto);
    }

    private void validarTarefa(Tarefa tarefa) {

        if (tarefa == null) {
            throw new IllegalArgumentException("A tarefa não pode ser nula.");
        }

        if (tarefa.getTitulo() == null ||
            tarefa.getTitulo().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "O título da tarefa é obrigatório."
            );
        }

        if (tarefa.getPrioridade() == null ||
            tarefa.getPrioridade().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "A prioridade da tarefa é obrigatória."
            );
        }

        if (tarefa.getStatus() == null ||
            tarefa.getStatus().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "O status da tarefa é obrigatório."
            );
        }
    }
}
