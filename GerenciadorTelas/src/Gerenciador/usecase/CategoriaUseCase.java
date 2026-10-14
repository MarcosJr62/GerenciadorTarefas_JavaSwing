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

    public void cadastrar(String nome, String descricao) throws RegraNegocioException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("O nome da categoria não pode estar vazio.");
        }

        String nomeFormatado = nome.trim();
        String descFormatada = descricao != null ? descricao.trim() : "";

        try {
            if (categoriaDAO.existePorNome(nomeFormatado)) {
                throw new RegraNegocioException("Já existe uma categoria cadastrada com este nome.");
            }

            Categoria categoria = new Categoria(nomeFormatado, descFormatada);
            categoriaDAO.inseri(categoria);
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao salvar a categoria no banco de dados.");
        }
    }

    public void atualizar(int idCategoria, String nome, String descricao) throws RegraNegocioException {
        if (idCategoria <= 0) {
            throw new RegraNegocioException("Categoria inválida para atualização.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("O nome da categoria não pode estar vazio.");
        }

        String nomeFormatado = nome.trim();
        String descFormatada = descricao != null ? descricao.trim() : "";

        try {
            Categoria categoria = new Categoria(idCategoria, nomeFormatado, descFormatada);
            categoriaDAO.atualizar(categoria);
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao atualizar a categoria no banco de dados.");
        }
    }

    public void excluir(int idCategoria) throws RegraNegocioException {
        try {
            categoriaDAO.deletar(idCategoria);
        } catch (SQLException e) {
            throw new RegraNegocioException("Erro ao excluir a categoria no banco de dados.");
        }
    }
}
