package Gerenciador.entity;

/** Situação da tarefa (Pendente, Em andamento, Concluída) — tabela status. */
public class Status {

    private final int idStatus;
    private final String nome;

    public Status(int idStatus, String nome) {
        this.idStatus = idStatus;
        this.nome = nome;
    }

    public int getIdStatus() {
        return idStatus;
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
