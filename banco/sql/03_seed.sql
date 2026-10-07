-- Dados iniciais. Status e prioridade são obrigatórios (a tarefa não existe sem eles);
-- as categorias são exemplos. Usuários são criados pela tela "Criar Conta", porque a
-- senha precisa ser transformada em hash (PBKDF2) pelo Java.
--   psql -U gerenciador_app -d gerenciador_tarefas -f banco/sql/03_seed.sql

-- O arquivo é UTF-8; sem isto o psql no Windows lê na codificação do console e corrompe os acentos.
SET client_encoding TO 'UTF8';

INSERT INTO status (nome) VALUES
    ('Pendente'),
    ('Em andamento'),
    ('Concluída')
ON CONFLICT DO NOTHING;

INSERT INTO prioridade (nome) VALUES
    ('Alta'),
    ('Média'),
    ('Baixa')
ON CONFLICT DO NOTHING;

INSERT INTO categoria (nome) VALUES
    ('Faculdade'),
    ('Trabalho'),
    ('Pessoal'),
    ('Estudos')
ON CONFLICT DO NOTHING;
