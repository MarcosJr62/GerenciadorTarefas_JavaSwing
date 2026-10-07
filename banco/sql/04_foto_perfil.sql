-- Foto de perfil do usuário (PNG 128x128 gerado pelo Java). Pode rodar mais de uma vez sem erro.
--   psql -U gerenciador_app -d gerenciador_tarefas -f banco/sql/04_foto_perfil.sql

ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS foto_perfil BYTEA
        CONSTRAINT ck_usuario_foto_tamanho CHECK (octet_length(foto_perfil) <= 1048576);
