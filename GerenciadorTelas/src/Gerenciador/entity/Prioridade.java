package Gerenciador.entity;

/** Prioridade da tarefa (Alta, Média, Baixa) — tabela prioridade. */
public class Prioridade {

    private final int idPrioridade;
    private final String nome;

    public Prioridade(int idPrioridade, String nome) {
        this.idPrioridade = idPrioridade;
        this.nome = nome;
    }

    public int getIdPrioridade() {
        return idPrioridade;
    }

    public String getNome() {
        return nome;
    }

    /** O JComboBox usa toString() para exibir o item. */
    @Override
    public String toString() {
        return nome;
    }
}
