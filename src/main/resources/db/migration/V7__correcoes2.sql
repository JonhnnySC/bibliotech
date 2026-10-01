-- Tabela de associação N:N entre autores e livros
CREATE TABLE IF NOT EXISTS autor_livro (
                                           autor_id BIGINT NOT NULL,
                                           livro_id BIGINT NOT NULL,
                                           PRIMARY KEY (autor_id, livro_id),
                                           FOREIGN KEY (autor_id) REFERENCES autor(id) ON DELETE CASCADE,
                                           FOREIGN KEY (livro_id) REFERENCES livro(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_autor_livro_autor ON autor_livro(autor_id);
CREATE INDEX IF NOT EXISTS idx_autor_livro_livro ON autor_livro(livro_id);