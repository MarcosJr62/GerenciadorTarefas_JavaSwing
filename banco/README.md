# Banco de dados — PostgreSQL

## Modelo

```
usuario (1) ──< (N) tarefa (N) >── (1) categoria
                   │ │
        status (1)─┘ └─(1) prioridade
```

| Tabela      | Colunas |
|-------------|---------|
| `usuario`   | id, nome, email (único), login (único), senha_hash, tentativas_falhas, bloqueado_ate, data_criacao |
| `categoria` | id, nome (único), descricao |
| `tarefa`    | id, titulo, descricao, data_criacao, data_vencimento, usuario_id (FK), categoria_id (FK), status_id (FK), prioridade_id (FK) |
| `status`    | id, nome (único) — Pendente, Em andamento, Concluída |
| `prioridade`| id, nome (único) — Alta, Média, Baixa |

- Login e e-mail são únicos sem diferenciar maiúsculas/minúsculas.
- Excluir usuário apaga as tarefas dele; excluir categoria deixa as tarefas sem categoria;
  status e prioridade em uso não podem ser excluídos.
- IDs são `INTEGER` (`int` no Java).

## Como instalar (Windows) — fazer uma vez em cada PC

1. Instale o PostgreSQL (16 ou superior): https://www.postgresql.org/download/windows/
   Guarde a senha do usuário `postgres` definida na instalação.
2. Na raiz do repositório, rode:

   ```powershell
   powershell -ExecutionPolicy Bypass -File banco\setup-banco.ps1
   ```

   O script pede a senha do `postgres` e uma senha nova para o usuário da aplicação
   (`gerenciador_app`), cria o banco `gerenciador_tarefas`, as 5 tabelas, os status, as prioridades, as categorias iniciais
   e grava a variável de ambiente `DB_PASSWORD` usada pelo Java.
3. **Feche e abra o IntelliJ** para ele enxergar a variável `DB_PASSWORD`.

## Conexão no Java

- `Gerenciador.dao.ConexaoBD.abrir()` devolve uma `Connection` (sempre fechar com try-with-resources).
- Driver: `GerenciadorTelas/lib/postgresql-42.7.13.jar` (já configurado no `GerenciadorTelas.iml`).
- Variáveis: `DB_PASSWORD` (obrigatória), `DB_URL` e `DB_USER` (opcionais, padrão `localhost:5432` e `gerenciador_app`).

## Regras para os DAOs

- Sempre `PreparedStatement` com `?` — nunca concatenar texto digitado no SQL.
- Toda consulta/alteração de tarefa filtra por `usuario_id = Sessao.getUsuarioLogado().getIdUsuario()`,
  para um usuário nunca ver nem alterar tarefas de outro.

## Segurança do login

- Senha guardada só como hash PBKDF2-HMAC-SHA256 (600.000 iterações, salt aleatório) — `SenhaHash`.
- Mesma mensagem para usuário inexistente e senha errada (não revela quais logins existem).
- 5 senhas erradas seguidas bloqueiam o login por 5 minutos (controlado no banco).
- Senha: 8 a 128 caracteres, com letra e número, diferente do usuário.
- A senha da conexão com o banco fica em variável de ambiente, nunca no código.
