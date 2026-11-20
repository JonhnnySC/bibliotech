mkdir -p docs && cat > docs/relatorio-avaliacao-final.md << 'FIM_DO_DOC'
# BiblioTech — Relatório Técnico para a Avaliação Final

**Data:** 20/11/2026
**Repositório:** JonhnnySC/bibliotech
**Base da análise:** árvore do repo (commit 998a441) + arquivos enviados (LoginResponse, AuthService, TokenService, UsuarioLogadoResponse) + PDFs da avaliação e do guia passo a passo.

---

## 1. Sumário executivo

| Item | Situação |
|---|---|
| Requisitos do PDF cobertos pelo plano | 100% (obrigatórios + os 6 testes práticos) |
| Requisitos confirmados como implementados | apenas os que já existiam antes do plano |
| Alteração nova confirmada no código | TokenService.revogar() |
| Maior risco | plano especificado, aplicação não verificada |
| Mudança de rota decidida | Flyway removido; schema gerado pelas entidades (ddl-auto=update) |
| Fase ainda não entregue | 7 (limpeza + ensaio dos 6 testes) |

O desenho técnico satisfaz a avaliação. O que separa o projeto da nota é aplicar, verificar e ensaiar — não projetar.

---

## 2. Estado verificado do código

### 2.1 Já existia e está correto

| Peça | Evidência |
|---|---|
| CRUD da entidade principal | LivroController: POST, GET lista, GET /{id}, PUT, DELETE |
| JWT completo | TokenService (java-jwt, emissor, JTI, expiração) + JwtFilter + SecurityConfiguration |
| Swagger/OpenAPI | SwaggerConfiguration com esquema bearerAuth |
| DTOs | application/dto com Request/Response por entidade |
| Camadas separadas | domain / application / infrastructure / presentation |
| Value Object | CPF com validação de dígitos |
| Exceções de negócio | ConflitoException, RecursoNaoEncontradoException, CredenciaisInvalidasException, TokenException + ApiExceptionHandler |
| Enums preenchidos | EnumPerfil (3), EnumGeneroLiterario (60+) |
| Anti timing-attack | hashFalso no AuthService |
| Front | Next App Router + TS, services/, store/, types/, grupo (sistema)/, Tailwind |
| Stack | Spring Boot 4.1.1, Java 25, Lombok, spring-dotenv, PostgreSQL 18 no Docker |

### 2.2 Confirmado como feito nas fases novas

- TokenService.revogar(String token).

### 2.3 Pendente, com evidência nos arquivos enviados

| Arquivo | O que mostra | Falta |
|---|---|---|
| LoginResponse | 3 campos (token, tipo, expiraEm) | campo usuario (Fase 1.2) |
| AuthService | retorna 3 args, sem logout() | Fases 1.2 e 1.3 |
| UsuarioLogadoResponse | é o principal (CPF + GrantedAuthority) | renomear para UsuarioLogado e criar o DTO público (1.0/1.1) |
| TokenService | em application.service, importa com.auth0 | mover para infrastructure/security como TokenAdapter (6.1) |

### 2.4 Pendente pelo estado do repositório

Fase 2 (Redux sem cookie, favoritos, multilayout), Fase 3 (bootstrap, desktop, README, .env.example), Fase 4 (multilocação), Fase 5 (ViaCEP), Fase 6 (portas), Fase 7 (limpeza).

---

## 3. Decisões de arquitetura fechadas

| # | Decisão | Motivo |
|---|---|---|
| 1 | Empresa = Biblioteca; não existe admin global | o PDF manda o JavaFX criar o primeiro Administrador e a primeira Empresa |
| 2 | biblioteca_id em Livro, Autor, Exemplar, Emprestimo, Usuario | toda tabela de negócio tem dono |
| 3 | Editora fora do escopo | catálogo compartilhado, sem controller |
| 4 | Biblioteca vem do token, nunca do corpo | nenhum DTO de request tem bibliotecaId |
| 5 | Fora do escopo responde 404, não 403 | 403 confirma que o recurso existe |
| 6 | Token e usuário só na store Redux | requisito explícito; F5 desloga por consequência |
| 7 | Registro público removido | cadastro sem empresa violaria o NOT NULL |
| 8 | Desktop fala com a API, não com o banco | reaproveita CPF, BCrypt e regras |
| 9 | Bootstrap protegido por X-Bootstrap-Key (tempo constante) | chave vazia = endpoint desligado |
| 10 | ViaCEP no back, atrás de ConsultaCepPort | exemplo limpo de DIP para o Teste 6 |
| 11 | Flyway removido; ddl-auto=update | schema nasce das entidades; cold start sem checksum |
| 12 | update, nunca create/create-drop | create apagaria o admin no restart do Teste 4 |

---

## 4. Flyway removido — o que mudou

Instruções do guia que ficaram obsoletas:

| Guia dizia | Agora |
|---|---|
| 3.1 editar a V5 para tirar o seed | não existe migration; o seed simplesmente não existe |
| 4.1 criar V9__multilocacao.sql | vira @ManyToOne(optional=false) + @JoinColumn(nullable=false, updatable=false) |
| 5.1 criar V10__endereco_biblioteca.sql | vira os 7 campos String na entidade Biblioteca |
| down -v por checksum mismatch | não há checksum; o wipe garante schema limpo |
| 6.4 remover coluna de Leitor pede migration | basta remover o campo da entidade e recriar o banco |
| README com passo de migration | README sem Flyway |

Consequência: tudo que era garantido só pelo SQL (UNIQUE, NOT NULL, tamanhos, índices) precisa estar anotado nas entidades.

Regra de operação: o update não altera tipo, tamanho nem constraint antiga. Mudou isso, rode docker compose down -v && docker compose up -d.

---

## 5. Escopo por fase

Status: FEITO / ESPECIFICADO (aplicação não confirmada) / NÃO ENTREGUE

| Fase | Entregas | Status |
|---|---|---|
| 1 | DTO público UsuarioLogadoResponse; usuario no login; logout; expiração por .env; log INFO; LimpaTokensNaSubida; GET /livros/generos | ESPECIFICADO (só revogar() FEITO) |
| 2.1 | authSlice sem cookie; configurarApi(store); interceptors req/res; ExpiracaoDoToken; StoreProvider; TelaLogin; layout condicional; Header.sair(); remover js-cookie e sessao.ts | ESPECIFICADO |
| 2.2 | favoritosSlice com extraReducers ouvindo auth/logout; estrela e "Só favoritos" | ESPECIFICADO |
| 2.3 | grupo (publico); SomentePerfis; layouts por perfil; Sidebar filtrada | ESPECIFICADO |
| 3 | /auth/bootstrap + BootstrapService; remover /auth/registro; .env.example e README; desktop admin-desktop com 2 telas e Task | ESPECIFICADO |
| 4 | BibliotecaDoLogado; findByIdAndBiblioteca_Id; setBiblioteca em toda criação; escopo em relacionamentos cruzados; ISBN por biblioteca; MultilocacaoTest | ESPECIFICADO |
| 5 | ConsultaCepPort + ViaCepClient (timeouts 2s/3s); BibliotecaService; /bibliotecas/minha GET/PUT e /cep/{cep}; PUT só ADMINISTRADOR; tela no front | ESPECIFICADO |
| 6 | TokenPort/SenhaPort/UsuarioLogadoPort + adaptadores; 9 repositórios como porta+JPA+adapter; exceções próprias; lados mappedBy; renomear ExemplarCOntroller e valueObject; docs/arquitetura.md | ESPECIFICADO |
| 7 | limpeza e ensaio | NÃO ENTREGUE |

---

## 6. Atenções

### 6.1 Bloqueios — sem isso o cold start ou o CRUD não funciona

| # | Problema | Correção |
|---|---|---|
| B1 | Token.token sem length: Hibernate cria varchar(255) e o JWT tem 300-500 chars, o login estoura ao salvar | @Column(nullable=false, length=1024) |
| B2 | Editora órfã: não tem controller e o seed sumiu; se Livro.editora for obrigatória, POST /livros falha no banco vazio | get-or-create por nome, ou tornar opcional, ou criar EditoraController |
| B3 | Serviço sem setBiblioteca + coluna NOT NULL: todo POST falha | setBiblioteca(getReferenceById(bibliotecaDoLogado.id())) em livro, autor, exemplar, emprestimo e usuario |
| B4 | ISBN com unique=true volta a ser global e o passo 5 do Teste 3 falha | @Table(uniqueConstraints=@UniqueConstraint(columnNames={"biblioteca_id","isbn"})) |
| B5 | UNIQUE de e-mail/CPF/CNPJ só existia no SQL | declarar unique=true nas entidades |
| B6 | Sem /auth/bootstrap o banco vazio não tem admin nem como criar | Fase 3 completa antes de qualquer wipe |
| B7 | ddl-auto=create apagaria o admin no restart do Teste 4 | usar update |

### 6.2 Armadilhas de operação

| # | Atenção |
|---|---|
| A1 | docker compose down -v apaga o volume inteiro; só nesse contexto |
| A2 | update não altera tipo, tamanho nem constraint antiga: mudou, recrie o banco |
| A3 | devtools + LimpaTokensNaSubida: cada recompile desloga o Swagger (não é bug) |
| A4 | substring("Bearer".length()) do guia está errado; use "Bearer " (7 chars, com espaço) |
| A5 | não pôr /auth/logout no permitAll nem no shouldNotFilter |
| A6 | não tornar o filtro de multilocação tolerante a usuário ausente |
| A7 | regex do e-mail: conferir se está [^@\s] e não [^@s] (rejeitaria maria@senac.com) |
| A8 | POST /usuarios com perfil LEITOR precisa instanciar Leitor (herança JOINED) |
| A9 | JWT_EXPIRATION_MINUTES=1 só no ensaio; voltar para 60 |
| A10 | a rede da sala precisa liberar viacep.com.br; testar antes |
| A11 | .env nunca commitado; só .env.example; BOOTSTRAP_KEY anotada |
| A12 | o back só lê o .env ao subir: mudou a chave, reinicie |
| A13 | o interceptor converte ProblemDetail em Error(mensagem); nas telas use e.message |

### 6.3 Decisões que é preciso saber explicar (não são bugs)

| Tema | Resposta |
|---|---|
| Usuario implements UserDetails | padrão da Aula 3; as roles nascem do perfil no domínio; exceção consciente |
| Spring na application (@Service, @Transactional) | estereótipos não são detalhe de infraestrutura; JWT, JPA e HTTP estão atrás de porta |
| Por que não Flyway | sistema inicializável do zero pelo bootstrap; em produção eu versionaria o schema, porque update não faz downgrade |
| Editora sem biblioteca_id | catálogo compartilhado, sem exposição por controller |
| Favoritos somem no F5 | consequência de "exclusivamente na store" |
| Next não é SPA puro | navegação client-side; o PDF aceita Next; grupos de rota organizam layouts |
| F5 desloga | exigência do PDF; sem cookie, localStorage ou persist |
| 404 em vez de 403 | não confirmar existência de recurso de outra biblioteca |

---

## 7. Conformidade com o PDF da avaliação

| Requisito | No código verificado | Coberto pelo plano |
|---|---|---|
| DDD/Clean, 4 camadas | parcial (sem portas) | Fase 6 |
| Portas + DIP | não | Fase 6 |
| CRUD entidade principal | sim | sim |
| JPA relacionamentos | parcial (só @ManyToOne) | Fase 6.4 |
| Swagger | sim | sim (anotar endpoints novos) |
| DTOs em tudo | sim | sim (/generos devolve enum) |
| Login JWT | sim | sim (+ usuario na resposta) |
| Todos endpoints protegidos | sim | sim |
| Multi-empresa (dono) | parcial (só Usuario) | Fase 4 |
| Consultas filtradas | não | Fase 4 |
| Bootstrap sem INSERT | não (seed na V5) | Fase 3 + Flyway removido |
| Desktop JavaFX | parcial (fluxo antigo em back/) | Fase 3.5 |
| React TS Next / SPA | sim | sim |
| Multilayout | parcial (troca por rota+cookie) | Fases 2.1 e 2.3 |
| Estilização | sim | sim |
| Redux auth | parcial (com cookie) | Fase 2.1 |
| Redux 2ª funcionalidade | não | Fase 2.2 |
| Token/usuário só na store | não | Fase 2.1 |
| Camada Service | sim | sim |
| Interceptor req (da store) | parcial (lê cookie) | Fase 2.1 |
| Interceptor res (401 logout login) | não | Fase 2.1 |

---

## 8. Roteiro dos 6 testes práticos

| Teste | O que mostrar | Peça que garante | Risco |
|---|---|---|---|
| 1 Bootstrap | wipe, React no login, JavaFX cria biblioteca+admin, login OK | Fase 3 + schema pelas entidades | alto (desktop) |
| 2 Segurança | /livros/generos com token 200, sem token 401; desligar anyRequest(), reiniciar, 200 sem token; voltar o código | Fase 1.6 | médio (treinar o ponto exato) |
| 3 Multilocação | A cria livro; B não vê; GET/PUT/DELETE por id 404; mesmo ISBN em B cria | Fase 4 | médio (varrer todos os serviços) |
| 4 Redux/interceptor | login, reinicia back, recarrega lista, login aparece sozinho; F5 desloga; favoritos | LimpaTokensNaSubida + Fase 2 | baixo se Fase 1 aplicada |
| 5 ViaCEP | CEP preenche endereço; Network mostra chamada ao próprio back; salva e persiste; 00000000 não encontrado; 123 não chama | Fase 5 | médio (rede da sala) |
| 6 Explicação | camadas/portas, JPA, store/slice/reducers sem ler | Fase 6 + docs + treino | médio |

### Teste de fumaça do CRUD do zero

bootstrap, login, POST /autores, POST /livros, POST /exemplares, POST /usuarios (LEITOR), POST /emprestimos, GETs. Todos 201/200.

| Erro no log | Causa | Correção |
|---|---|---|
| null value in column "biblioteca_id" | serviço não setou o dono | B3 |
| null value in column "editora_id" | editora órfã | B2 |
| value too long for varchar(255) | token sem length | B1 |
| duplicate key livro_isbn_key | unicidade global | B4 |
| Email Invalido! | regex do setEmail | A7 |

---

## 9. Checklist de verificação

    # peças das fases existem?
    ls src/main/java/br/com/senac/bibliotech/application/port/ \
       src/main/java/br/com/senac/bibliotech/infrastructure/persistence/ \
       src/main/java/br/com/senac/bibliotech/infrastructure/external/
    grep -n "usuario" src/main/java/br/com/senac/bibliotech/application/dto/LoginResponse.java
    ls src/main/resources/db/migration        # deve NÃO existir
    grep -n "ddl-auto" src/main/resources/application.properties   # deve ser update

    # acoplamento proibido — deve sair vazio
    B=src/main/java/br/com/senac/bibliotech
    grep -rnE "HttpStatus|ResponseStatusException|SecurityContextHolder|PasswordEncoder|JpaRepository|com\.auth0|jakarta\.servlet" $B/domain $B/application

    # resíduos — devem sair vazios
    grep -rn "js-cookie\|localStorage" front/app
    grep -rniE "seed|admin@bibliotech|biblioteca nula" src front/app
    grep -ril viacep src/main/java            # só ViaCepClient

    # os três builds
    ./mvnw clean test
    cd front && npm run build && npm run lint && cd ..
    cd admin-desktop && ./mvnw clean package && cd ..

    # schema nasceu certo?
    docker compose exec postgres psql -U postgres -d bibliotech -c "\d livro"
    docker compose exec postgres psql -U postgres -d bibliotech -c "\d token"

---

## 10. Ordem de execução

1. Fechar a Fase 1 (sem usuario no login a store nasce vazia).
2. Auditoria das entidades (B1 a B5).
3. Fase 3 (bootstrap, desktop, README, .env.example) — destrava o Teste 1 e o wipe seguro.
4. Fase 4 (multilocação) — Teste 3.
5. Fase 2 (Redux, interceptors, favoritos, multilayout) — Teste 4.
6. Fase 5 (ViaCEP) — Teste 5.
7. Fase 6 (portas, JPA, nomes, docs) — Teste 6.
8. Fase 7 (limpeza e ensaio duas vezes do banco apagado).

Regra: um commit por passo, checagem da fase antes de avançar. Se atrasar, corte a Fase 6.4; nunca corte o ensaio.

---

## 11. Pendências e perguntas em aberto

1. Data exata da avaliação (assumida ~27/11).
2. Horas disponíveis por dia.
3. Livro.editora é obrigatória? LivroRequest manda nome ou id da editora? Define a correção de B2.
4. O que já foi aplicado das fases 1 a 6 além do revogar()? Rodar o checklist da seção 9 para converter este relatório de plano para estado verificado.

---

## Conclusão

O projeto tem base boa (CRUD, JWT, Swagger, DTOs, camadas, Next/TS) e o plano cobre integralmente o que falta. Este relatório não atesta a implementação porque os arquivos disponíveis ainda mostram o estado inicial. Com a saída do checklist da seção 9, a próxima versão sai com status verificado por requisito.
FIM_DO_DOC