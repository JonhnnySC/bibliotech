-- Exemplar: cada cópia física tem UM status (substitui quantidade)
ALTER TABLE exemplar
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'DISPONIVEL';

-- se a V1 criou quantidade_disponivel e você tirou o campo da entidade:
-- ALTER TABLE exemplar DROP COLUMN quantidade_disponivel;

-- Usuario
ALTER TABLE usuario
    ADD COLUMN perfil VARCHAR(20) NOT NULL DEFAULT 'LEITOR',
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    ADD COLUMN genero VARCHAR(20);

-- Emprestimo (ajuste o DEFAULT para o valor real do seu EnumStatusEmprestimo)
ALTER TABLE emprestimo
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ATIVO';