ALTER TABLE livro      ADD COLUMN biblioteca_id BIGINT NOT NULL REFERENCES biblioteca (id);
ALTER TABLE autor      ADD COLUMN biblioteca_id BIGINT NOT NULL REFERENCES biblioteca (id);
ALTER TABLE exemplar   ADD COLUMN biblioteca_id BIGINT NOT NULL REFERENCES biblioteca (id);
ALTER TABLE emprestimo ADD COLUMN biblioteca_id BIGINT NOT NULL REFERENCES biblioteca (id);
ALTER TABLE usuario    ALTER COLUMN biblioteca_id SET NOT NULL;

CREATE INDEX idx_livro_biblioteca      ON livro (biblioteca_id);
CREATE INDEX idx_autor_biblioteca      ON autor (biblioteca_id);
CREATE INDEX idx_exemplar_biblioteca   ON exemplar (biblioteca_id);
CREATE INDEX idx_emprestimo_biblioteca ON emprestimo (biblioteca_id);

-- unicidade de negócio passa a ser POR biblioteca
ALTER TABLE livro DROP CONSTRAINT IF EXISTS livro_isbn_key;
ALTER TABLE livro ADD CONSTRAINT uk_livro_biblioteca_isbn UNIQUE (biblioteca_id, isbn);