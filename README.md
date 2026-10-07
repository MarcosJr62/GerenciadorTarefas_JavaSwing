# GerenciadorTarefas_JavaSwing

# Gerenciador de Tarefas

## Sobre o projeto

O **Gerenciador de Tarefas** é um sistema de gerenciamento de tarefas desenvolvido em **Java Swing**, com o objetivo de auxiliar usuários a cadastrar, organizar, acompanhar e concluir suas atividades.

O sistema foi planejado para permitir o gerenciamento de tarefas por meio de informações como **título, descrição, prioridade, categoria, status e prazo**, além de oferecer recursos de cadastro de usuários, login, pesquisa e filtragem.

O projeto foi estruturado pensando em uma aplicação desktop simples e intuitiva, servindo também como prática dos conceitos de **Programação Orientada a Objetos, Java Swing, banco de dados e modelagem de sistemas**.

---

## Objetivo

O principal objetivo do projeto é desenvolver uma aplicação capaz de centralizar e organizar tarefas, permitindo que o usuário acompanhe suas atividades pendentes e concluídas de maneira simples.

Entre as principais funcionalidades planejadas estão:

* Cadastro e login de usuários;
* Cadastro, edição e exclusão de tarefas;
* Definição de prioridade;
* Organização por categorias;
* Controle do status das tarefas;
* Definição de prazo;
* Pesquisa de tarefas;
* Filtros por status e prioridade;
* Visualização das tarefas cadastradas.

---

## Telas do sistema

As telas foram planejadas para manter uma interface simples e objetiva, facilitando a utilização do sistema.

### Tela de Login

Tela responsável pela autenticação do usuário no sistema. Também possui acesso à opção de criação de uma nova conta.

<img width="348" height="322" alt="image" src="https://github.com/user-attachments/assets/1e1ad92c-a121-461d-805c-22e1e156cd91" />

### Tela Principal

É a tela central do sistema, onde o usuário poderá visualizar suas tarefas e utilizar as principais funções, como criar, editar, excluir e concluir tarefas.

Também contará com recursos de pesquisa e filtros por status e prioridade.

<img width="422" height="454" alt="image" src="https://github.com/user-attachments/assets/fd4a8cd7-ac65-4b52-b92f-c3826e417b97" />

### Cadastro de Tarefa

Tela destinada ao cadastro de novas tarefas. Nela serão informados dados como título, descrição, prioridade, categoria, data limite e status.

<img width="235" height="462" alt="image" src="https://github.com/user-attachments/assets/f8e0962e-7af1-41a9-a67d-641a88eb07ba" />

### Categorias

Tela responsável pelo gerenciamento das categorias utilizadas para organizar as tarefas.

<img width="329" height="426" alt="image" src="https://github.com/user-attachments/assets/6c4674c9-b9b7-4297-bf4a-65ddf0e889f3" />

### Cadastro de Usuário

Tela destinada à criação de novos usuários, contendo informações como nome, e-mail, login e senha.

<img width="278" height="427" alt="image" src="https://github.com/user-attachments/assets/7aee4207-96d2-48d3-a850-42d3b5727f50" />

---

## Diagramas

Os diagramas foram desenvolvidos para representar a estrutura e o funcionamento planejado do sistema antes da implementação.

### Diagrama de Classes

O diagrama de classes apresenta as principais entidades do sistema e seus respectivos atributos e métodos.

```mermaid
classDiagram
    class Usuario {
        -int idUsuario
        -String nome
        -String email
        -String login
        -String senha
        +cadastrar()
        +atualizar()
        +excluir()
        +autenticar()
    }
    class Tarefa {
        -int idTarefa
        -String titulo
        -String descricao
        -Date dataCriacao
        -Date dataVencimento
        -int idUsuario
        -int idCategoria
        -int idStatus
        -int idPrioridade
        +cadastrar()
        +atualizar()
        +excluir()
        +concluir()
    }
    class Categoria {
        -int idCategoria
        -String nome
        -String descricao
        +cadastrar()
        +atualizar()
        +excluir()
    }
    class Status {
        -int idStatus
        -String nome
    }
    class Prioridade {
        -int idPrioridade
        -String nome
    }
    Usuario "1" --> "0..*" Tarefa : possui
    Categoria "1" --> "0..*" Tarefa : classifica
    Status "1" --> "0..*" Tarefa : situação
    Prioridade "1" --> "0..*" Tarefa : prioridade
```

A senha do usuário é armazenada apenas como hash (PBKDF2), nunca em texto puro.

### Modelo Entidade-Relacionamento

O MER representa a estrutura dos dados que serão utilizados pelo sistema, apresentando as entidades, seus atributos, chaves primárias e estrangeiras e os relacionamentos entre elas.

As principais entidades são:

* **USUARIO**
* **TAREFA**
* **CATEGORIA**
* **STATUS**
* **PRIORIDADE**

Um usuário pode possuir várias tarefas, enquanto cada tarefa pertence a um usuário. Da mesma forma, uma categoria, um status e uma prioridade podem estar relacionados a várias tarefas.

O modelo relacional apresenta a transformação das entidades do MER em tabelas do banco de dados, incluindo suas respectivas chaves primárias (**PK**) e chaves estrangeiras (**FK**).

```mermaid
erDiagram
    USUARIO ||--o{ TAREFA : possui
    CATEGORIA |o--o{ TAREFA : classifica
    STATUS ||--o{ TAREFA : "situação de"
    PRIORIDADE ||--o{ TAREFA : "prioridade de"

    USUARIO {
        int id PK
        varchar nome
        varchar email UK
        varchar login UK
        varchar senha_hash
        int tentativas_falhas
        timestamptz bloqueado_ate
        timestamptz data_criacao
    }
    TAREFA {
        int id PK
        varchar titulo
        text descricao
        timestamptz data_criacao
        date data_vencimento
        int usuario_id FK
        int categoria_id FK
        int status_id FK
        int prioridade_id FK
    }
    CATEGORIA {
        int id PK
        varchar nome UK
        varchar descricao
    }
    STATUS {
        int id PK
        varchar nome UK
    }
    PRIORIDADE {
        int id PK
        varchar nome UK
    }
```

`tentativas_falhas` e `bloqueado_ate` protegem o login: após 5 senhas erradas seguidas, o acesso fica bloqueado por 5 minutos.

---

## Como executar

1. Instale o PostgreSQL e crie o banco seguindo [banco/README.md](banco/README.md).
2. Rode o sistema de um destes jeitos:
   - **Pelo terminal (VS Code ou qualquer outro)**, na raiz do repositório:

     ```powershell
     powershell -ExecutionPolicy Bypass -File executar.ps1
     ```

     O script compila o projeto e abre a tela de login. Precisa do JDK 17 ou superior instalado.
   - **Pelo IntelliJ:** abra a pasta `GerenciadorTelas` e execute a classe `Main` (`src/Gerenciador/view/Main.java`).
