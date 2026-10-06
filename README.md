# GerenciadorTarefas_JavaSwing

# TaskManager — Gerenciador de Tarefas

## Sobre o projeto

O **TaskManager** é um sistema de gerenciamento de tarefas desenvolvido em **Java Swing**, com o objetivo de auxiliar usuários a cadastrar, organizar, acompanhar e concluir suas atividades.

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

---

## Diagramas

Os diagramas foram desenvolvidos para representar a estrutura e o funcionamento planejado do sistema antes da implementação.

### Diagrama de Classes

O diagrama de classes apresenta as principais entidades do sistema e seus respectivos atributos e métodos.

As principais classes planejadas são:

* **Tarefa**
<img width="199" height="278" alt="diagrama (2)" src="https://github.com/user-attachments/assets/6ccede90-2882-43f6-b87d-7e8c78512418" />

* **Categoria**
<img width="158" height="164" alt="diagrama (1)" src="https://github.com/user-attachments/assets/32d99211-63c1-498a-874d-79d50ce6a038" />

Também são representados os relacionamentos entre essas classes.

### Modelo Entidade-Relacionamento

O MER representa a estrutura dos dados que serão utilizados pelo sistema, apresentando as entidades, seus atributos, chaves primárias e estrangeiras e os relacionamentos entre elas.

As principais entidades são:

* **USUARIO**
* **TAREFA**
* **CATEGORIA**

Um usuário pode possuir várias tarefas, enquanto cada tarefa pertence a um usuário. Da mesma forma, uma categoria pode estar relacionada a várias tarefas.

O modelo relacional apresenta a transformação das entidades do MER em tabelas do banco de dados, incluindo suas respectivas chaves primárias (**PK**) e chaves estrangeiras (**FK**).

<img width="232" height="421" alt="DiagramaMER" src="https://github.com/user-attachments/assets/ed892669-74a9-4bf9-b23a-d9356309af00" />

