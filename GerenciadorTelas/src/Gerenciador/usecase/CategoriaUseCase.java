package Gerenciador.usecase;

import Gerenciador.dao.CategoriaDAO;
import Gerenciador.entity.Categoria;
import java.sql.SQLException;
import java.util.List;

public class CategoriaUseCase {

    private final CategoriaDAO categoriaDAO;

    public CategoriaUseCase() {
        this.categoriaDAO = new CategoriaDAO();
    }

    public List<Categoria> listar() throws RegraNegocioException {
        try {
            return categoriaDAO.listarTodas();
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao buscar categorias no banco de dados.");
        }
    }

    public void adicionar(String nome) throws RegraNegocioException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("O nome da categoria não pode estar vazio.");
        }

        String nomeFormatado = nome.trim();

        try {
            if (categoriaDAO.existePorNome(nomeFormatado)) {
                throw new RegraNegocioException("Já existe uma categoria cadastrada com este nome.");
            }

            Categoria categoria = new Categoria(nomeFormatado);
            categoriaDAO.inseri(categoria);
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao salvar a categoria no banco de dados.");
        }
    }

    public void remover(int id) throws RegraNegocioException {
        try {
            categoriaDAO.deletar(id);
        } catch (SQLException e) {
            throw new RegraNegocioException("Não é possível excluir a categoria pois ela está vinculada a tarefas existentes.");
        }
    }
}
