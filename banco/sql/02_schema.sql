-- Estrutura do banco. Rodar como o usuário da aplicação:
--   psql -U gerenciador_app -d gerenciador_tarefas -f banco/sql/02_schema.sql

BEGIN;

CREATE TABLE usuario (
    id            INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    login         VARCHAR(50)  NOT NULL,
    -- Guarda apenas o hash da senha (PBKDF2 com salt, gerado no Java), nunca a senha em texto puro.
    senha_hash    VARCHAR(255) NOT NULL,
    -- Proteção contra força bruta: após várias senhas erradas seguidas o login fica bloqueado por um tempo.
    tentativas_falhas  INTEGER     NOT NULL DEFAULT 0,
    bloqueado_ate      TIMESTAMPTZ,
    data_criacao  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT ck_usuario_nome_nao_vazio  CHECK (btrim(nome) <> ''),
    CONSTRAINT ck_usuario_login_nao_vazio CHECK (btrim(login) <> ''),
    CONSTRAINT ck_usuario_email_formato   CHECK (email ~* '^[^@\s]+@[^@\s]+\.[^@\s]+$')
);

-- Unicidade sem diferenciar maiúsculas/minúsculas ("Joao" e "joao" são o mesmo login).
CREATE UNIQUE INDEX uq_usuario_login ON usuario (lower(login));
CREATE UNIQUE INDEX uq_usuario_email ON usuario (lower(email));

CREATE TABLE categoria (
    id         INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome       VARCHAR(50)  NOT NULL,
    descricao  VARCHAR(255),
    CONSTRAINT ck_categoria_nome_nao_vazio CHECK (btrim(nome) <> '')
);

CREATE UNIQUE INDEX uq_categoria_nome ON categoria (lower(nome));

-- Opções dos combos "Status" e "Prioridade" das telas (valores em 03_seed.sql).
CREATE TABLE status (
    id    INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome  VARCHAR(30) NOT NULL,
    CONSTRAINT uq_status_nome UNIQUE (nome)
);

CREATE TABLE prioridade (
    id    INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome  VARCHAR(30) NOT NULL,
    CONSTRAINT uq_prioridade_nome UNIQUE (nome)
);

CREATE TABLE tarefa (
    id                INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo            VARCHAR(150) NOT NULL,
    descricao         TEXT,
    data_criacao      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    data_vencimento   DATE,
    usuario_id        INTEGER      NOT NULL,
    categoria_id      INTEGER,
    status_id         INTEGER      NOT NULL,
    prioridade_id     INTEGER      NOT NULL,
    CONSTRAINT ck_tarefa_titulo_nao_vazio CHECK (btrim(titulo) <> ''),
    -- Excluir o usuário apaga as tarefas dele.
    CONSTRAINT fk_tarefa_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario (id) ON DELETE CASCADE,
    -- Excluir uma categoria deixa as tarefas "sem categoria" em vez de apagá-las.
    CONSTRAINT fk_tarefa_categoria FOREIGN KEY (categoria_id)
        REFERENCES categoria (id) ON DELETE SET NULL,
    -- Status/prioridade em uso não podem ser excluídos.
    CONSTRAINT fk_tarefa_status FOREIGN KEY (status_id)
        REFERENCES status (id) ON DELETE RESTRICT,
    CONSTRAINT fk_tarefa_prioridade FOREIGN KEY (prioridade_id)
        REFERENCES prioridade (id) ON DELETE RESTRICT
);

-- Toda listagem/filtro da tela principal é por usuário logado + status/prioridade.
CREATE INDEX ix_tarefa_usuario_status     ON tarefa (usuario_id, status_id);
CREATE INDEX ix_tarefa_usuario_prioridade ON tarefa (usuario_id, prioridade_id);
CREATE INDEX ix_tarefa_usuario_vencimento ON tarefa (usuario_id, data_vencimento);
CREATE INDEX ix_tarefa_categoria          ON tarefa (categoria_id);

COMMIT;
