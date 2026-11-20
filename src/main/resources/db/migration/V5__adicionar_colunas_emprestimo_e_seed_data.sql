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