-- Cria o usuário da aplicação e o banco. Rodar conectado como superusuário (postgres).
-- Forma recomendada: banco/setup-banco.ps1 (pede as senhas sem mostrá-las).
-- Manual: definir a variável de ambiente APP_DB_PASSWORD e rodar
--   psql -U postgres -f banco/sql/01_create_database.sql
-- A senha vem do ambiente para não ficar em arquivo versionado nem na linha de comando.

\getenv app_password APP_DB_PASSWORD
\if :{?app_password}
\else
  \echo 'ERRO: defina a variavel de ambiente APP_DB_PASSWORD antes de rodar.'
  \quit
\endif

CREATE ROLE gerenciador_app WITH LOGIN PASSWORD :'app_password';

CREATE DATABASE gerenciador_tarefas
    OWNER gerenciador_app
    ENCODING 'UTF8'
    TEMPLATE template0;

-- Impede que outros usuários do servidor se conectem a este banco.
REVOKE CONNECT ON DATABASE gerenciador_tarefas FROM PUBLIC;
GRANT CONNECT ON DATABASE gerenciador_tarefas TO gerenciador_app;
