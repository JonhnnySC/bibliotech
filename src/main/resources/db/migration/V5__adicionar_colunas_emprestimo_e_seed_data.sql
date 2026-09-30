-- ============================================
-- V5 DEFINITIVA: completar schema + imagens + seed
-- Defensiva: não quebra se V2/V3 já criaram colunas/constraints
-- ============================================

-- ---------- 1. Colunas de EMPRESTIMO (erro do Hibernate) ----------
ALTER TABLE emprestimo ADD COLUMN IF NOT EXISTS leitor_id BIGINT;
ALTER TABLE emprestimo ADD COLUMN IF NOT EXISTS exemplar_id BIGINT;
ALTER TABLE emprestimo ADD COLUMN IF NOT EXISTS data_emprestimo DATE;
ALTER TABLE emprestimo ADD COLUMN IF NOT EXISTS data_prevista_devolucao DATE;
ALTER TABLE emprestimo ADD COLUMN IF NOT EXISTS data_devolucao DATE;
ALTER TABLE emprestimo ADD COLUMN IF NOT EXISTS status_emprestimo VARCHAR(20) NOT NULL DEFAULT 'ATIVO';

-- ---------- 2. Colunas de EXEMPLAR (o vermelho do IDE) ----------
ALTER TABLE exemplar ADD COLUMN IF NOT EXISTS livro_id BIGINT;
ALTER TABLE exemplar ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'DISPONIVEL';

-- ---------- 3. Colunas de IMAGEM (Opção 1: só URLs) ----------
ALTER TABLE livro ADD COLUMN IF NOT EXISTS capa_url VARCHAR(500);
ALTER TABLE autor ADD COLUMN IF NOT EXISTS foto_url VARCHAR(500);

-- ---------- 4. Foreign keys (só se não existirem) ----------
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_emprestimo_leitor') THEN
            ALTER TABLE emprestimo ADD CONSTRAINT fk_emprestimo_leitor
                FOREIGN KEY (leitor_id) REFERENCES leitor(id);
        END IF;
        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_emprestimo_exemplar') THEN
            ALTER TABLE emprestimo ADD CONSTRAINT fk_emprestimo_exemplar
                FOREIGN KEY (exemplar_id) REFERENCES exemplar(id);
        END IF;
        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_exemplar_livro') THEN
            ALTER TABLE exemplar ADD CONSTRAINT fk_exemplar_livro
                FOREIGN KEY (livro_id) REFERENCES livro(id);
        END IF;
    END $$;

-- ---------- 5. Índices ----------
CREATE INDEX IF NOT EXISTS idx_emprestimo_status ON emprestimo(status_emprestimo);
CREATE INDEX IF NOT EXISTS idx_emprestimo_leitor ON emprestimo(leitor_id);
CREATE INDEX IF NOT EXISTS idx_exemplar_livro ON exemplar(livro_id);
CREATE INDEX IF NOT EXISTS idx_exemplar_status ON exemplar(status);

-- ============================================
-- SEED DE DADOS
-- ============================================

-- Editoras
INSERT INTO editora (nome, nacionalidade) VALUES
                                              ('Companhia das Letras', 'Brasileira'),
                                              ('Record', 'Brasileira'),
                                              ('Rocco', 'Brasileira'),
                                              ('Penguin Random House', 'Americana'),
                                              ('HarperCollins', 'Americana'),
                                              ('Intrínseca', 'Brasileira'),
                                              ('DarkSide Books', 'Brasileira'),
                                              ('Aleph', 'Brasileira');

-- Autores
INSERT INTO autor (nome, nacionalidade, data_nascimento) VALUES
                                                             ('Machado de Assis', 'Brasileira', '1839-06-21'),
                                                             ('Clarice Lispector', 'Brasileira', '1920-12-10'),
                                                             ('Gabriel García Márquez', 'Colombiana', '1927-03-06'),
                                                             ('Jorge Luis Borges', 'Argentina', '1899-08-24'),
                                                             ('J.K. Rowling', 'Britânica', '1965-07-31'),
                                                             ('George Orwell', 'Britânica', '1903-06-25'),
                                                             ('Isaac Asimov', 'Americana', '1920-01-02'),
                                                             ('Stephen King', 'Americana', '1947-09-21'),
                                                             ('Neil Gaiman', 'Britânica', '1960-11-10'),
                                                             ('Haruki Murakami', 'Japonesa', '1949-01-12');

-- Livros
INSERT INTO livro (volume, isbn, descricao, edicao, tipo, paginas, data_lancamento) VALUES
                                                                                        ('Dom Casmurro', '978-8535914843', 'Romance clássico de Machado de Assis sobre ciúme e traição', '1ª', 'Físico', 256, '1899-01-01'),
                                                                                        ('A Hora da Estrela', '978-8532505953', 'Último romance de Clarice Lispector', '1ª', 'Físico', 96, '1977-01-01'),
                                                                                        ('Cem Anos de Solidão', '978-8532509099', 'Obra-prima do realismo mágico', '1ª', 'Físico', 448, '1967-06-05'),
                                                                                        ('Ficções', '978-8535910366', 'Coletânea de contos fantásticos de Borges', '1ª', 'Físico', 192, '1944-01-01'),
                                                                                        ('Harry Potter e a Pedra Filosofal', '978-8532509670', 'Primeiro livro da saga Harry Potter', '1ª', 'Físico', 309, '1997-06-26'),
                                                                                        ('1984', '978-8535914849', 'Distopia clássica sobre vigilância e totalitarismo', '1ª', 'Físico', 336, '1949-06-08'),
                                                                                        ('Fundação', '978-8535910360', 'Primeiro livro da série Fundação de Asimov', '1ª', 'Físico', 256, '1951-01-01'),
                                                                                        ('O Iluminado', '978-8535910361', 'Romance de terror psicológico', '1ª', 'Físico', 448, '1977-01-28'),
                                                                                        ('Deuses Americanos', '978-8535910362', 'Fantasia sobre deuses antigos na América moderna', '1ª', 'Físico', 588, '2001-06-19'),
                                                                                        ('Norwegian Wood', '978-8535910363', 'Romance sobre amor e perda', '1ª', 'Físico', 296, '1987-09-04'),
                                                                                        ('Memórias Póstumas de Brás Cubas', '978-8535910364', 'Romance inovador narrado por um defunto autor', '1ª', 'Físico', 208, '1881-01-01'),
                                                                                        ('A Paixão Segundo G.H.', '978-8535910365', 'Romance existencialista de Clarice Lispector', '1ª', 'Físico', 184, '1964-01-01'),
                                                                                        ('O Amor nos Tempos do Cólera', '978-8535910367', 'História de amor que dura mais de 50 anos', '1ª', 'Físico', 448, '1985-01-01'),
                                                                                        ('O Aleph', '978-8535910368', 'Coletânea de contos fantásticos de Borges', '1ª', 'Físico', 176, '1949-01-01'),
                                                                                        ('Harry Potter e a Câmara Secreta', '978-8532509687', 'Segundo livro da saga Harry Potter', '1ª', 'Físico', 352, '1998-07-02');

-- Usuários (senha: troque o hash abaixo por um gerado com
-- new BCryptPasswordEncoder().encode("123456") se o login falhar)
INSERT INTO usuario (nome, email, cpf, senha) VALUES
                                                  ('Admin', 'admin@bibliotech.com', '11111111111', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
                                                  ('Maria Leitora', 'maria@email.com', '22222222222', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
                                                  ('João Estudante', 'joao@email.com', '33333333333', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- Leitores (herdam o id do usuario - estratégia JOINED)
INSERT INTO leitor (id, nacionalidade)
SELECT id, 'Brasileira' FROM usuario
WHERE email IN ('maria@email.com', 'joao@email.com')
ON CONFLICT DO NOTHING;

-- Exemplares: 2 cópias por livro
-- Exemplares: 2 cópias por livro (com cast explícito para DATE)
INSERT INTO exemplar (livro_id, capa_dura, data_compra, data_impressao, status)
SELECT
    l.id,
    TRUE,
    DATE '2024-01-15',
    DATE '2023-12-01',
    'DISPONIVEL'
FROM livro l
UNION ALL
SELECT
    l.id,
    FALSE,
    DATE '2024-02-20',
    DATE '2024-01-15',
    'DISPONIVEL'
FROM livro l;

-- ---------- 6. NOT NULL condicional (só se não houver linhas órfãs) ----------
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM exemplar WHERE livro_id IS NULL) THEN
            ALTER TABLE exemplar ALTER COLUMN livro_id SET NOT NULL;
        END IF;
        IF NOT EXISTS (SELECT 1 FROM emprestimo WHERE leitor_id IS NULL) THEN
            ALTER TABLE emprestimo ALTER COLUMN leitor_id SET NOT NULL;
        END IF;
        IF NOT EXISTS (SELECT 1 FROM emprestimo WHERE exemplar_id IS NULL) THEN
            ALTER TABLE emprestimo ALTER COLUMN exemplar_id SET NOT NULL;
        END IF;
    END $$;

-- ---------- 7. Capas reais via Open Library (pelo ISBN) ----------
UPDATE livro
SET capa_url = 'https://covers.openlibrary.org/b/isbn/' || isbn || '-M.jpg'
WHERE isbn IS NOT NULL AND capa_url IS NULL;