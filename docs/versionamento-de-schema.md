a ideia central é versionamento de schema, igual você versiona código. Mudanças no banco não devem ser feitas "na mão" direto no DBeaver/psql em produção (ou mesmo em dev, se o time for maior que 1 pessoa), porque senão ninguém sabe reproduzir o estado do banco, não dá pra fazer rollback, e cada ambiente (dev/staging/prod) vira uma coisa diferente.

spring.jpa.hibernate.ddl-auto=update -

é uotimo parta prototipar, o hibernate olha as entidades e table e gera/altera o schema sozinho.
MAS É CONSIDERADO MÁ PRATIOCA DE PRODUÇÃO:

- não tem historico do que mudou, não dá pra fazer rollback, e em cenários mais complexos(renomear coluna, migrar dado existente) o Hibernate não sabe o que fazer.

Qual ferramenta usar:? FLYWAY OU LIQUIBASE

- Flyway - Migrations em SQL puro, simples e fácil de entender = V1__create_table_autor.sql, V2__add_column_nacionalidade.sql
- Liquibase - Migrations em XML, JSON ou YAML, mais verboso, mas mais poderoso (ex: renomear coluna, migrar dados existentes, etc)
-------
O Spring Boot com Flyway detecta essa pasta automaticamente (convenção, zero config extra). Cada arquivo segue o padrão:

V<numero>__descricao_curta.sql

exemplos: 

V1__create_table_usuario.sql
V2__create_table_leitor.sql
V3__create_table_editora.sql
V4__create_table_autor.sql
V5__create_table_livro.sql
V6__create_table_exemplar.sql
V7__create_table_box_livro.sql
V8__create_table_emprestimo.sql]

------------------------

não editar nada no arquivo depois que fora analizado pelo glyway, pois existe um checksum, senão dá conflito com o checksum e não funciona mais.


MIGRATION V5 - 

1. Corrige a estrutura da tabela emprestimo

   Adiciona data_emprestimo, data_prevista_devolucao, data_devolucao
   Adiciona status_emprestimo (ATIVO, DEVOLVIDO, ATRASADO, etc.)
   Adiciona as foreign keys leitor_id e exemplar_id
   Cria índices para performance

2. Dados iniciais variados

   8 editoras brasileiras e internacionais
   10 autores clássicos e contemporâneos
   15 livros incluindo clássicos brasileiros e best-sellers internacionais
   3 usuários (1 admin, 2 leitores)
   30 exemplares (2 por livro, variando capa dura/mole)

3. Boas práticas aplicadas

   ON CONFLICT DO NOTHING - não falha se já existir dados
   IF NOT EXISTS - seguro para reexecutar
   Índices para queries frequentes
   Foreign keys com nomes explícitos

