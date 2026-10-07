# Banco de dados do Gerenciador de Tarefas

Aqui fica tudo o que é preciso para o banco de dados do projeto funcionar. Usamos o **PostgreSQL**,
e cada PC que for rodar o sistema precisa ter o banco instalado (os dados ficam salvos no próprio
computador).

## Como o banco está organizado

O sistema guarda as informações em 5 tabelas:

```
usuario (1) ──< (N) tarefa (N) >── (1) categoria
                   │ │
        status (1)─┘ └─(1) prioridade
```

- **usuario** – quem usa o sistema: nome, e-mail, login e a senha (guardada de forma protegida, explicado lá embaixo).
- **tarefa** – o coração do sistema: título, descrição, data de criação, data limite, e a quem ela pertence.
- **categoria** – para organizar as tarefas (Faculdade, Trabalho, Pessoal, Estudos...).
- **status** – em que pé a tarefa está: Pendente, Em andamento ou Concluída.
- **prioridade** – o quanto ela é urgente: Alta, Média ou Baixa.

Algumas regras que o próprio banco garante:

- Não dá para ter dois usuários com o mesmo login ou o mesmo e-mail (nem mudando maiúsculas e minúsculas).
- Se um usuário for excluído, as tarefas dele vão junto.
- Se uma categoria for excluída, as tarefas continuam existindo, só ficam sem categoria.
- Um status ou uma prioridade que esteja sendo usado por alguma tarefa não pode ser apagado.

<details>
<summary>Ver todas as colunas de cada tabela</summary>

| Tabela       | Colunas |
|--------------|---------|
| `usuario`    | id, nome, email, login, senha_hash, tentativas_falhas, bloqueado_ate, data_criacao |
| `tarefa`     | id, titulo, descricao, data_criacao, data_vencimento, usuario_id, categoria_id, status_id, prioridade_id |
| `categoria`  | id, nome, descricao |
| `status`     | id, nome |
| `prioridade` | id, nome |

Os IDs são números inteiros (`int` no Java).
</details>

## Instalando no seu PC (só precisa fazer uma vez)

**1. Instale o PostgreSQL**

Baixe em https://www.postgresql.org/download/windows/ (versão 16 ou mais nova) e siga o instalador.
Durante a instalação ele vai pedir uma senha para o usuário `postgres` — **anote essa senha**, você vai
precisar dela no próximo passo.

**2. Crie o banco do projeto**

Abra o terminal na pasta do repositório e rode:

```powershell
powershell -ExecutionPolicy Bypass -File banco\setup-banco.ps1
```

O script vai te pedir duas coisas:

- a senha do `postgres` (a que você anotou na instalação);
- uma senha nova, inventada por você, para o sistema usar (mínimo de 8 caracteres).

As senhas não aparecem na tela enquanto você digita, isso é normal. No final ele mostra as tabelas
criadas e já deixa tudo configurado: banco, tabelas, status, prioridades e algumas categorias de exemplo.

**3. Rode o sistema**

O jeito mais fácil é pelo terminal (funciona no VS Code), na raiz do repositório:

```powershell
powershell -ExecutionPolicy Bypass -File executar.ps1
```

Ele compila tudo e abre a tela de login. Se preferir o IntelliJ, feche e abra ele de novo (para
reconhecer a configuração nova) e rode a classe `Main`.

> **Deu erro?** Se aparecer "autenticação falhou" logo no começo, a senha do `postgres` foi digitada
> errada — é só rodar o script de novo. Se o erro aparecer no meio do caminho, o banco pode ter sido
> criado pela metade; peça ajuda antes de rodar de novo.

## Para quem vai programar

A conexão com o banco já está pronta. Para usar:

```java
try (Connection conexao = ConexaoBD.abrir()) {
    // suas consultas aqui
}
```

O `try (...)` fecha a conexão sozinho no final, então não precisa se preocupar com isso.

O driver do PostgreSQL já está na pasta `GerenciadorTelas/lib` e configurado no projeto, então o
IntelliJ reconhece sem precisar fazer nada.

A senha do banco **nunca** fica escrita no código: o sistema lê ela de uma variável do Windows
chamada `DB_PASSWORD`, que o script de instalação cria. (Se um dia precisar apontar para outro banco,
dá para configurar também `DB_URL` e `DB_USER`.)

Duas regras importantes na hora de escrever os DAOs:

1. **Sempre use `PreparedStatement` com `?`** no lugar dos valores, nunca junte o texto digitado pelo
   usuário direto no SQL. Isso evita que alguém consiga mexer no banco digitando comandos nos campos.
2. **Toda busca de tarefa tem que filtrar pelo usuário logado**, usando
   `Sessao.getUsuarioLogado().getIdUsuario()`. Assim cada pessoa vê e mexe só nas próprias tarefas.

## Como o login protege as contas

- **A senha nunca é salva como texto.** O que vai para o banco é um código embaralhado (hash), gerado
  de um jeito que não dá para voltar para a senha original. Nem quem abre o banco consegue ver as senhas.
- **O sistema não entrega quem tem conta.** Errar a senha ou digitar um usuário que não existe mostra a
  mesma mensagem: "Usuário ou senha inválidos."
- **Errou 5 vezes seguidas? Espera 5 minutos.** Isso impede alguém de ficar testando senhas até acertar.
- **Senhas fracas não passam:** precisa ter de 8 a 128 caracteres, com pelo menos uma letra e um número,
  e não pode ser igual ao nome de usuário.
