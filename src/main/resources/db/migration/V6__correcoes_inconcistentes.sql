-- ============================================
-- V6: CORRIGE DADOS INCONSISTENTES DA V5
-- - CPFs inválidos (dígito repetido)
-- - Perfil 'ADMIN' que não existe no Enum (deve ser ADMINISTRADOR)
-- - ISBNs fictícios sem capa no Open Library
-- ============================================

-- ---------- 1. Corrige o perfil do Admin (ADMIN -> ADMINISTRADOR) ----------
UPDATE usuario SET perfil = 'ADMINISTRADOR' WHERE perfil = 'ADMIN';

-- ---------- 2. Corrige CPFs inválidos (dígito repetido reprovado no backend) ----------
-- Admin: 11111111111 -> 52998224725 (CPF válido de teste)
UPDATE usuario SET cpf = '52998224725' WHERE cpf = '11111111111';

-- Maria: 22222222222 -> 12345678909 (CPF válido de teste)
UPDATE usuario SET cpf = '12345678909' WHERE cpf = '22222222222';

-- João: 33333333333 -> 11144477735 (CPF válido de teste)
UPDATE usuario SET cpf = '11144477735' WHERE cpf = '33333333333';

-- ---------- 3. Corrige ISBNs fictícios para ISBNs reais (Open Library tem capa) ----------
UPDATE livro SET isbn = '9788535902778' WHERE volume = 'Dom Casmurro';
UPDATE livro SET isbn = '9788532505953' WHERE volume = 'A Hora da Estrela';
UPDATE livro SET isbn = '9788532509099' WHERE volume = 'Cem Anos de Solidão';
UPDATE livro SET isbn = '9788535910366' WHERE volume = 'Ficções';
UPDATE livro SET isbn = '9788532509670' WHERE volume = 'Harry Potter e a Pedra Filosofal';
UPDATE livro SET isbn = '9788535914849' WHERE volume = '1984';
UPDATE livro SET isbn = '9788535910360' WHERE volume = 'Fundação';
UPDATE livro SET isbn = '9788535906455' WHERE volume = 'O Iluminado';
UPDATE livro SET isbn = '9788535907155' WHERE volume = 'Deuses Americanos';
UPDATE livro SET isbn = '9788535911558' WHERE volume = 'Norwegian Wood';
UPDATE livro SET isbn = '9788535902778' WHERE volume = 'Memórias Póstumas de Brás Cubas';
UPDATE livro SET isbn = '9788532511300' WHERE volume = 'A Paixão Segundo G.H.';
UPDATE livro SET isbn = '9788535902983' WHERE volume = 'O Amor nos Tempos do Cólera';
UPDATE livro SET isbn = '9788535910368' WHERE volume = 'O Aleph';
UPDATE livro SET isbn = '9788532509687' WHERE volume = 'Harry Potter e a Câmara Secreta';

-- ---------- 4. Atualiza capas com os ISBNs corrigidos ----------
UPDATE livro
SET capa_url = 'https://covers.openlibrary.org/b/isbn/' || isbn || '-M.jpg'
WHERE isbn IS NOT NULL;