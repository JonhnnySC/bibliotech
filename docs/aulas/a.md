# BiblioTech: arquitetura final (DDD + Clean Architecture + SOLID)

Salve este arquivo como `docs/arquitetura.md` no repositório.

## 1. Como ler

Três níveis: **contexto → camada → arquivo**.

- **Contexto** (DDD, Bounded Context): `identidade`, `acervo`, `circulacao`, mais o `compartilhado`.
- **Camada** (Clean Architecture): `presentation`, `application`, `domain`, `infra`, dentro de cada contexto.
- **Arquivo**: um por responsabilidade (SOLID).

## 2. A árvore completa

```
bibliotech/
├── backend/
│   ├── pom.xml
│   ├── .env                                       (local, fora do git)
│   ├── .env.example                               (chaves sem valores, vai pro git)
│   └── src/
│       ├── main/
│       │   ├── java/br/com/senac/bibliotech/
│       │   │   ├── BibliotechApplication.java
│       │   │   │
│       │   │   ├── compartilhado/                 ▌SHARED KERNEL: só o que todos usam
│       │   │   │   ├── domain/
│       │   │   │   │   ├── DominioException.java
│       │   │   │   │   ├── RecursoNaoEncontradoException.java
│       │   │   │   │   └── DomainEvent.java                   (interface marcadora)
│       │   │   │   ├── application/
│       │   │   │   │   └── port/DomainEventPublisher.java
│       │   │   │   ├── presentation/
│       │   │   │   │   └── ApiExceptionHandler.java           (exceção de domínio → 404/409/401/403)
│       │   │   │   └── infra/
│       │   │   │       ├── SpringDomainEventPublisher.java
│       │   │   │       ├── CorsConfiguration.java
│       │   │   │       ├── SwaggerConfiguration.java
│       │   │   │       └── ClockConfiguration.java
│       │   │   │
│       │   │   ├── identidade/                    ▌CONTEXTO: quem é o usuário, login, permissões
│       │   │   │   ├── api/                                   (contrato PÚBLICO: o que outros contextos podem usar)
│       │   │   │   │   ├── IdentidadeApi.java
│       │   │   │   │   └── LeitorResumo.java                  (record: id, nome, ativo, temPendencia)
│       │   │   │   ├── presentation/
│       │   │   │   │   ├── AuthController.java
│       │   │   │   │   ├── UsuarioController.java
│       │   │   │   │   ├── request/
│       │   │   │   │   │   ├── LoginRequest.java
│       │   │   │   │   │   ├── UsuarioRequest.java
│       │   │   │   │   │   ├── AtualizarUsuarioRequest.java
│       │   │   │   │   │   ├── AtualizarStatusRequest.java
│       │   │   │   │   │   ├── AtualizarPerfilRequest.java
│       │   │   │   │   │   └── AlterarSenhaRequest.java
│       │   │   │   │   └── response/
│       │   │   │   │       ├── LoginResponse.java
│       │   │   │   │       └── UsuarioResponse.java
│       │   │   │   ├── application/
│       │   │   │   │   ├── usecase/
│       │   │   │   │   │   ├── autenticacao/LoginUseCase.java
│       │   │   │   │   │   ├── usuario/
│       │   │   │   │   │   │   ├── CadastrarUsuarioUseCase.java
│       │   │   │   │   │   │   ├── ListarUsuariosUseCase.java
│       │   │   │   │   │   │   ├── BuscarUsuarioUseCase.java
│       │   │   │   │   │   │   ├── AtualizarUsuarioUseCase.java
│       │   │   │   │   │   │   ├── AtualizarStatusUsuarioUseCase.java
│       │   │   │   │   │   │   ├── AtualizarPerfilUsuarioUseCase.java
│       │   │   │   │   │   │   ├── AlterarSenhaUseCase.java
│       │   │   │   │   │   │   └── ExcluirUsuarioUseCase.java
│       │   │   │   │   │   └── leitor/ConsultarLeitorUseCase.java   (implements IdentidadeApi)
│       │   │   │   │   ├── dto/
│       │   │   │   │   │   ├── LoginCommand.java, LoginResult.java
│       │   │   │   │   │   ├── CadastrarUsuarioCommand.java
│       │   │   │   │   │   ├── AtualizarUsuarioCommand.java
│       │   │   │   │   │   ├── AlterarSenhaCommand.java
│       │   │   │   │   │   └── UsuarioResult.java
│       │   │   │   │   └── port/
│       │   │   │   │       ├── TokenProvider.java
│       │   │   │   │       ├── TokenClaims.java               (record seu, em vez de DecodedJWT)
│       │   │   │   │       └── PasswordHasher.java
│       │   │   │   ├── domain/
│       │   │   │   │   ├── model/
│       │   │   │   │   │   ├── usuario/
│       │   │   │   │   │   │   ├── Usuario.java               (RAIZ DO AGREGADO)
│       │   │   │   │   │   │   ├── Email.java                 (value object)
│       │   │   │   │   │   │   ├── Cpf.java                   (value object)
│       │   │   │   │   │   │   ├── SenhaHash.java             (value object)
│       │   │   │   │   │   │   ├── Perfil.java                (LEITOR, BIBLIOTECARIO, ADMINISTRADOR)
│       │   │   │   │   │   │   ├── StatusUsuario.java         (ATIVO, BLOQUEADO, INATIVO, EXCLUIDO)
│       │   │   │   │   │   │   └── UsuarioRepository.java     (interface)
│       │   │   │   │   │   └── leitor/
│       │   │   │   │   │       ├── Leitor.java                (RAIZ: usuarioId + nacionalidade)
│       │   │   │   │   │       └── LeitorRepository.java      (interface)
│       │   │   │   │   └── exception/
│       │   │   │   │       ├── CredenciaisInvalidasException.java
│       │   │   │   │       ├── TokenInvalidoException.java
│       │   │   │   │       ├── EmailJaCadastradoException.java
│       │   │   │   │       ├── CpfJaCadastradoException.java
│       │   │   │   │       ├── EmailInvalidoException.java
│       │   │   │   │       ├── CpfInvalidoException.java
│       │   │   │   │       └── SenhaAtualIncorretaException.java
│       │   │   │   └── infra/
│       │   │   │       ├── persistence/
│       │   │   │       │   ├── entity/       UsuarioJpaEntity.java, LeitorJpaEntity.java
│       │   │   │       │   ├── repository/   SpringDataUsuarioRepository.java, SpringDataLeitorRepository.java
│       │   │   │       │   ├── mapper/       UsuarioMapper.java, LeitorMapper.java
│       │   │   │       │   └── adapter/      UsuarioRepositoryAdapter.java, LeitorRepositoryAdapter.java
│       │   │   │       └── security/
│       │   │   │           ├── SecurityConfiguration.java
│       │   │   │           ├── JwtFilter.java                 (agora com ROLE_<perfil>)
│       │   │   │           ├── Auth0TokenProvider.java        (implements TokenProvider)
│       │   │   │           ├── BcryptPasswordHasher.java      (implements PasswordHasher)
│       │   │   │           ├── PasswordEncoderConfig.java
│       │   │   │           ├── RestAuthenticationEntryPoint.java   (401 em JSON)
│       │   │   │           └── RestAccessDeniedHandler.java        (403 em JSON)
│       │   │   │
│       │   │   ├── acervo/                        ▌CONTEXTO: o catálogo (livros, autores, editoras)
│       │   │   │   ├── api/
│       │   │   │   │   ├── AcervoApi.java
│       │   │   │   │   └── LivroResumo.java                   (record: id, titulo, isbn, capaUrl)
│       │   │   │   ├── presentation/
│       │   │   │   │   ├── LivroController.java
│       │   │   │   │   ├── AutorController.java
│       │   │   │   │   ├── request/    LivroRequest.java, AutorRequest.java
│       │   │   │   │   └── response/   LivroResponse.java, AutorResponse.java
│       │   │   │   ├── application/
│       │   │   │   │   ├── usecase/
│       │   │   │   │   │   ├── livro/
│       │   │   │   │   │   │   ├── CriarLivroUseCase.java
│       │   │   │   │   │   │   ├── AtualizarLivroUseCase.java
│       │   │   │   │   │   │   ├── BuscarLivroUseCase.java
│       │   │   │   │   │   │   ├── ListarLivrosUseCase.java   (implements AcervoApi)
│       │   │   │   │   │   │   └── ExcluirLivroUseCase.java
│       │   │   │   │   │   └── autor/
│       │   │   │   │   │       ├── CriarAutorUseCase.java
│       │   │   │   │   │       ├── AtualizarAutorUseCase.java
│       │   │   │   │   │       ├── ListarAutoresUseCase.java
│       │   │   │   │   │       └── ExcluirAutorUseCase.java
│       │   │   │   │   └── dto/        CriarLivroCommand.java, LivroResult.java, AutorResult.java ...
│       │   │   │   ├── domain/
│       │   │   │   │   ├── model/
│       │   │   │   │   │   ├── livro/
│       │   │   │   │   │   │   ├── Livro.java                 (RAIZ; guarda autorIds e editoraId, não objetos)
│       │   │   │   │   │   │   ├── Isbn.java                  (value object)
│       │   │   │   │   │   │   ├── GeneroLiterario.java
│       │   │   │   │   │   │   ├── BoxLivro.java              (decidir: coleção do Livro, ou apagar)
│       │   │   │   │   │   │   └── LivroRepository.java
│       │   │   │   │   │   ├── autor/    Autor.java, AutorRepository.java
│       │   │   │   │   │   └── editora/  Editora.java, EditoraRepository.java
│       │   │   │   │   └── exception/    IsbnInvalidoException.java, IsbnJaCadastradoException.java
│       │   │   │   └── infra/
│       │   │   │       └── persistence/
│       │   │   │           ├── entity/       LivroJpaEntity.java, AutorJpaEntity.java, EditoraJpaEntity.java
│       │   │   │           ├── repository/   SpringDataLivroRepository.java ...
│       │   │   │           ├── mapper/       LivroMapper.java ...
│       │   │   │           └── adapter/      LivroRepositoryAdapter.java ...
│       │   │   │
│       │   │   └── circulacao/                    ▌CONTEXTO: exemplares, empréstimos, atrasos, multas
│       │   │       ├── presentation/
│       │   │       │   ├── EmprestimoController.java
│       │   │       │   ├── ExemplarController.java
│       │   │       │   ├── request/
│       │   │       │   │   ├── EmprestimoRequest.java
│       │   │       │   │   ├── ExemplarRequest.java
│       │   │       │   │   └── AtualizarStatusExemplarRequest.java
│       │   │       │   └── response/     EmprestimoResponse.java, ExemplarResponse.java
│       │   │       ├── application/
│       │   │       │   ├── usecase/
│       │   │       │   │   ├── emprestimo/
│       │   │       │   │   │   ├── RealizarEmprestimoUseCase.java
│       │   │       │   │   │   ├── DevolverEmprestimoUseCase.java
│       │   │       │   │   │   └── ListarEmprestimosAtivosUseCase.java
│       │   │       │   │   └── exemplar/
│       │   │       │   │       ├── CadastrarExemplarUseCase.java
│       │   │       │   │       ├── ListarExemplaresPorLivroUseCase.java
│       │   │       │   │       └── AlterarStatusExemplarUseCase.java
│       │   │       │   ├── dto/
│       │   │       │   │   ├── RealizarEmprestimoCommand.java, EmprestimoResult.java
│       │   │       │   │   └── CadastrarExemplarCommand.java, ExemplarResult.java
│       │   │       │   └── port/
│       │   │       │       ├── ConsultaLeitor.java            (pergunta a "identidade")
│       │   │       │       └── ConsultaAcervo.java            (pergunta a "acervo")
│       │   │       ├── domain/
│       │   │       │   ├── model/
│       │   │       │   │   ├── emprestimo/
│       │   │       │   │   │   ├── Emprestimo.java            (RAIZ: abrir(), devolver(), estaAtrasado())
│       │   │       │   │   │   ├── PeriodoEmprestimo.java     (value object: início, previsto, devolução)
│       │   │       │   │   │   ├── Multa.java                 (value object)
│       │   │       │   │   │   ├── StatusEmprestimo.java
│       │   │       │   │   │   └── EmprestimoRepository.java  (interface)
│       │   │       │   │   └── exemplar/
│       │   │       │   │       ├── Exemplar.java              (RAIZ: emprestar(), devolver())
│       │   │       │   │       ├── StatusExemplar.java
│       │   │       │   │       └── ExemplarRepository.java    (interface)
│       │   │       │   ├── service/
│       │   │       │   │   └── PoliticaEmprestimo.java        (interface: prazo e multa diária)
│       │   │       │   ├── event/
│       │   │       │   │   ├── EmprestimoRealizado.java
│       │   │       │   │   └── EmprestimoDevolvido.java
│       │   │       │   └── exception/
│       │   │       │       ├── ExemplarIndisponivelException.java
│       │   │       │       ├── EmprestimoJaDevolvidoException.java
│       │   │       │       ├── LeitorComAtrasoException.java
│       │   │       │       ├── LeitorBloqueadoException.java
│       │   │       │       └── ExemplarEmprestadoException.java
│       │   │       └── infra/
│       │   │           ├── persistence/
│       │   │           │   ├── entity/       EmprestimoJpaEntity.java, ExemplarJpaEntity.java (com @Version)
│       │   │           │   ├── repository/   SpringDataEmprestimoRepository.java, SpringDataExemplarRepository.java
│       │   │           │   ├── mapper/       EmprestimoMapper.java, ExemplarMapper.java
│       │   │           │   └── adapter/      EmprestimoRepositoryAdapter.java, ExemplarRepositoryAdapter.java
│       │   │           ├── acl/
│       │   │           │   ├── ConsultaLeitorAdapter.java     (usa identidade.api.IdentidadeApi)
│       │   │           │   └── ConsultaAcervoAdapter.java     (usa acervo.api.AcervoApi)
│       │   │           └── configuration/
│       │   │               └── PoliticaEmprestimoConfig.java
│       │   │
│       │   └── resources/
│       │       ├── application.properties         (só o que é comum)
│       │       ├── application-dev.properties     (show-sql, DEBUG do security)
│       │       └── db/
│       │           ├── migration/
│       │           │   ├── V1__create_estrutura_inicial.sql ... V7__correcoes2.sql   (NUNCA editar)
│       │           │   ├── V8__multa_e_versao.sql                (multa em emprestimo, version em exemplar)
│       │           │   └── V9__separar_usuario_e_leitor.sql      (herança JOINED → composição)
│       │           └── seed/                       (dados de exemplo, só no profile dev)
│       │
│       └── test/java/br/com/senac/bibliotech/
│           ├── identidade/
│           │   ├── domain/        CpfTest, EmailTest, UsuarioTest
│           │   ├── application/   LoginUseCaseTest, AlterarSenhaUseCaseTest
│           │   └── infra/         UsuarioRepositoryAdapterIT       (Testcontainers + Postgres)
│           ├── acervo/
│           │   ├── domain/        IsbnTest, LivroTest
│           │   └── application/   CriarLivroUseCaseTest
│           ├── circulacao/
│           │   ├── domain/        EmprestimoTest, ExemplarTest, MultaTest
│           │   ├── application/   RealizarEmprestimoUseCaseTest, DevolverEmprestimoUseCaseTest
│           │   ├── infra/         EmprestimoRepositoryAdapterIT
│           │   └── presentation/  EmprestimoControllerTest         (@WebMvcTest)
│           ├── ArquiteturaTest.java               (ArchUnit: regra de dependência das camadas)
│           └── ModulosTest.java                   (Spring Modulith: ApplicationModules.verify())
│
├── frontend/                                      (hoje "front")
├── docs/
│   ├── arquitetura.md                             (este arquivo)
│   ├── adr/                                       (decisões: 001-leitor-como-composicao.md ...)
│   └── notas/                                     (arvore_front.txt, links_front.txt, anotações soltas)
├── docker-compose.yml
├── .gitignore                                     (.env, target/, .idea/)
└── README.md
```

## 3. As regras que mantêm tudo de pé

### Clean Architecture: dentro de cada contexto

```
presentation ──► application ──► domain ◄── infra
```

- `domain` não importa nada (nem Spring, nem JPA, nem outra camada).
- `application` só conhece `domain` (mais as suas portas).
- `presentation` só conhece `application`.
- `infra` implementa as interfaces declaradas em `domain` e `application`. Ninguém importa `infra`.

### DDD: entre contextos

- Um contexto **nunca** importa classe interna de outro. Só usa o pacote `api` do outro.
- Entre agregados, referência **por ID** (`Emprestimo` guarda `leitorId` e `exemplarId`).
- Um repositório por raiz de agregado; a interface fica em `domain/model/<agregado>/`.
- Só se altera o agregado pelos métodos da raiz, sem setters públicos.
- Value objects (`Cpf`, `Email`, `Isbn`, `Multa`) são imutáveis e se validam no construtor.
- Nomes do código = nomes do negócio, em português (linguagem ubíqua).

### SOLID: onde está

| Princípio | Onde |
|---|---|
| S | Um use case por ação; um adapter por porta |
| O | `PoliticaEmprestimo` (nova regra = nova classe, sem editar o use case) |
| L | Sem herança `Usuario` → `Leitor`; composição |
| I | Repositórios de domínio só com o que o contexto usa; portas pequenas |
| D | Use cases dependem de interfaces (`EmprestimoRepository`, `TokenProvider`, `PasswordHasher`, `ConsultaLeitor`, `Clock`) |

## 4. De onde vem cada arquivo atual

| Hoje | Vai para |
|---|---|
| `config/CorsConfiguration`, `SwaggerConfiguration` | `compartilhado/infra/` |
| `config/SecurityConfiguration`, `JwtFilter`, `PasswordEncoderConfig` | `identidade/infra/security/` |
| `controllers/AuthController`, `UsuarioController` | `identidade/presentation/` |
| `controllers/LivroController`, `AutorController` | `acervo/presentation/` |
| `controllers/EmprestimoController`, `ExemplarCOntroller` | `circulacao/presentation/` (corrigir o nome) |
| `dto/*Request`, `dto/*Response` | `presentation/request` e `response` do contexto dono |
| `entities/Usuario`, `Leitor` | `identidade/domain/model/` (pura) + `identidade/infra/persistence/entity/` (JPA) |
| `entities/Livro`, `Autor`, `Editora`, `BoxLivro` | `acervo/domain/model/` + `infra/persistence/entity/` |
| `entities/Emprestimo`, `Exemplar` | `circulacao/domain/model/` + `infra/persistence/entity/` |
| `enums/EnumPerfil`, `EnumStatusUsuario` | `identidade/domain/model/usuario/` (`Perfil`, `StatusUsuario`) |
| `enums/EnumGeneroLiterario` | `acervo/domain/model/livro/` (`GeneroLiterario`) |
| `enums/EnumStatusEmprestimo`, `EnumStatusExemplar` | `circulacao/domain/model/...` (`StatusEmprestimo`, `StatusExemplar`) |
| `exception/ApiExceptionHandler` | `compartilhado/presentation/` |
| `exception/RecursoNaoEncontrado`, `Conflito`, `CredenciaisInvalidas`, `Token` | `compartilhado/domain/` e `identidade/domain/exception/` (a `Conflito` é trocada por exceções semânticas) |
| `repository/*` | interface em `domain/model/<agregado>/`; `SpringData*` e `*Adapter` em `infra/persistence/` |
| `service/AuthService` | `identidade/application/usecase/autenticacao/LoginUseCase` |
| `service/TokenService` | porta `identidade/application/port/TokenProvider` + `infra/security/Auth0TokenProvider` |
| `service/UsuarioService` | quebrado em `identidade/application/usecase/usuario/*` |
| `service/LivroService`, `AutorService` | `acervo/application/usecase/...` |
| `service/EmprestimoService`, `ExemplarService` | `circulacao/application/usecase/...` |
| `infrastructure.external`, pacote `user` (vazios) | apagar |
| `arvore_front.txt`, `links_front.txt` | `docs/notas/` |

## 5. Ordem de migração (sem big-bang)

1. **Bugs primeiro:** authorities no `JwtFilter`, campos duplicados em `Leitor`, DTO no `EmprestimoController`.
2. **Higiene:** nomes, typo, pacotes vazios, profile `dev`, pom, mover notas para `docs/`.
3. **Migrations V8 e V9** (multa e versão; separar usuário e leitor).
4. **`compartilhado`:** exceções base, handler, config global.
5. **`circulacao`** (a mais rica): domínio puro + testes, depois persistência, use cases, controller. Remove o `EmprestimoService` e a entidade antigos.
6. **`identidade`:** value objects `Cpf` e `Email`, `TokenProvider`, `PasswordHasher`, use cases.
7. **`acervo`:** por último (é o CRUD mais simples; ótimo para comparar esforço).
8. **Fechamento:** `ArquiteturaTest` (ArchUnit) e `ModulosTest` (Modulith).

Regra prática para qualquer arquivo novo: pergunte "isso mudaria se eu trocasse o Postgres, o Spring ou o JSON?". Se sim, vai para `infra` ou `presentation`. Se não, é `domain` ou `application`.