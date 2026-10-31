# BiblioTech: o que falta para a Avaliação Final

Oct 31, 2026 · @Nigro

O projeto ainda não atende à Avaliação Final: no commit b5d6012 faltam quatro blocos que o professor testa ao vivo (multilocação, bootstrapping com JavaFX, integração externa e o fluxo Redux com expiração do token). Li o código do back e do front; não rodei build nem testes.

## Placar verificado no código

Dos 12 requisitos do PDF que conferi, 3 atendem, 2 atendem em parte e 7 não atendem. Esforço: P = horas, M = meio dia a um dia, G = mais de um dia.

| Requisito do PDF | Situação no código | Esforço |
| --- | --- | --- |
| Portas e inversão de dependência | Não atende: `Usuario implements UserDetails` e JPA no domínio; repositories em `domain/repository` estendem `JpaRepository`; `AuthService` usa a classe concreta `TokenService`; não existe pacote de portas | G |
| CRUD da entidade principal, DTOs, Swagger com Bearer | Atende (DTOs existem para todos os controllers; não li cada método) | n/a |
| JPA com `@OneToMany` | Parcial: há `@ManyToOne`, `@ManyToMany` e herança JOINED, mas nenhum `@OneToMany` | P |
| `/login` com JWT e rotas protegidas | Atende; sem token a API devolve 401 (`HttpStatusEntryPoint`) | n/a |
| Multilocação | Não atende: só `Usuario` tem `biblioteca`; Livro, Autor, Editora, Exemplar e Emprestimo não têm dono nem filtro | G |
| Bootstrapping com JavaFX | Não atende: a migration V5 insere editoras, autores, livros e exemplares; não há JavaFX nem endpoint de bootstrap | G |
| Integração externa (ViaCEP ou e-mail) | Não atende: não existe nenhuma chamada externa | M |
| Next, TypeScript, SPA, service por controller | Atende | n/a |
| Multilayout (login x painel) | Parcial: só existem o layout raiz e `(sistema)`; falta o grupo público | P |
| Token e dados do usuário só na store | Não atende: token e e-mail ficam em cookie; a store guarda só o e-mail | M |
| Segundo slice do Redux | Não atende: só existe `authSlice` | M |
| Interceptors (request lê a store; 401 despacha logout) | Não atende: o request lê o cookie e o 401 faz `window.location.href` | P |

## O que corrigi na análise anterior

O placar dela se confirmou quase inteiro, e achei seis pontos que ela errou, omitiu ou deixou solto.

**Confirmado no código:** o commit é o b5d6012; não há `@OneToMany`, ViaCEP, JavaFX, ArchUnit nem pacote de portas (o `docs/arquitetura.md` descreve outra estrutura, com bounded contexts, que não existe); o token está em cookie; o interceptor lê o cookie; o 401 usa `window.location.href`; o `application` usa `@Service`, `@Value`, `SecurityContextHolder` e `ResponseStatusException`.

**Corrigido ou acrescentado:**

1. **A regra de multilocação do código está errada, e é mais grave do que a análise dizia.** `UsuarioService.listar()` trata `biblioteca == null` como "vê tudo" para qualquer perfil. O `/auth/registro` cria um Leitor sem biblioteca, então um Leitor recém-cadastrado lista todos os usuários. A nota de sábado diz que só quem não é admin precisa de filtro; a regra deve ser por perfil, não por biblioteca nula.
2. **Acesso por id não confere dono.** `buscar`, `atualizar`, `atualizarStatus` e `excluir` usam `findById` sem filtro. Além disso `PUT /usuarios/{id}` e `GET /usuarios/{id}` não têm regra própria no `SecurityConfiguration` e caem em `anyRequest().authenticated()`: qualquer usuário logado edita qualquer usuário. Isso derruba o Teste 3 (acessar o registro de outro usuário pelo Swagger).
3. **O Teste 2 vai quebrar depois da multilocação.** Hoje, com a segurança desligada, só `GET /usuarios` e `POST /emprestimos` dão 500 (chamam `getUsuarioLogado()`). Quando livros, autores e exemplares passarem a filtrar pela biblioteca do logado, todos darão 500. Escolha já um endpoint de demonstração que não dependa do usuário, por exemplo uma lista dos gêneros literários (enum). Não torne o filtro tolerante a usuário ausente: seria devolver tudo a quem não está logado.
4. **A análise anterior levantava o risco de 403 em vez de 401 e a dúvida do `RestClient`. Os dois estão resolvidos:** o `SecurityConfiguration` já usa `HttpStatusEntryPoint(401)`, e o Spring Boot é o 4.1.1 (Spring 7), onde `RestClient` já existe sem mexer no `pom.xml`.
5. **Contradição interna:** o item de multilocação dizia que o admin sem biblioteca vê tudo, e o de bootstrapping criava o admin junto com a primeira biblioteca. Um admin vinculado a uma biblioteca é filtrado. Veja a decisão 1 em "Decisões pendentes".
6. **Cookie versus store:** a nota da Aula 19.10 manda guardar o token em cookie, mas o PDF da avaliação exige token e dados do usuário "exclusivamente na store do Redux". O que vale nota é o PDF.

**Não é exigência do PDF** (faça só se sobrar tempo): ArchUnit, modelo JPA separado com mappers, remover `enable_lazy_load_no_trans`, limpeza agendada de tokens, renomear `valueObject` e `ExemplarCOntroller`, o erro de ESLint em `(sistema)/layout.tsx`. O `.env.example` e o README não são exigidos, mas sem eles o professor não consegue subir o projeto do zero.

## Token: expirar e voltar ao login sozinho

O backend já faz o lado dele (o JWT vence em 60 minutos e o `JwtFilter` responde 401); falta o front guardar o vencimento na store, deslogar no instante certo e renderizar o login. A Aula 19.10 marca isso como urgente: "na avaliação final, se expirar o token, deve-se voltar para a tela de login".

**O que já existe no back:** expiração em `api.security.token.expiration-minutes=60`; `JwtFilter` devolve 401 "Token inválido ou expirado" para JWT vencido ou fora do banco; `LoginResponse` já traz `expiraEm`.

**O que muda no back (sem tocar nas versões do `pom.xml`):**

1. `LoginResponse` passa a devolver também o usuário (id, nome, e-mail, perfil, `bibliotecaId`) num DTO próprio. O PDF exige dados do usuário na store, e hoje só existe o e-mail.
2. Tempo de expiração vindo do `.env`: `api.security.token.expiration-minutes=${JWT_EXPIRATION_MINUTES:60}`. Na demonstração use `JWT_EXPIRATION_MINUTES=1`.
3. `POST /auth/logout` apagando a linha usada na requisição. Sem isso, o token só deixa de valer pelo tempo, e a linha fica na tabela para sempre. Reiniciar o backend não invalida nada, porque o token está no banco.

**O que muda no front:**

Passo 1: o slice guarda tudo e começa vazio. Nada vem de cookie, então recarregar a página desloga; é o que o PDF pede.

```ts
// store/authSlice.ts
interface Usuario { id: number; nome: string; email: string; perfil: string; bibliotecaId: number | null }
interface AuthState { token: string | null; expiraEm: string | null; usuario: Usuario | null }

const initialState: AuthState = { token: null, expiraEm: null, usuario: null };

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    // login devolve o estado inteiro: token, vencimento e usuário entram juntos
    login: (_state, action: PayloadAction<AuthState>) => action.payload,
    // UM reducer de saída: botão Sair, timer e 401 passam todos por aqui
    logout: () => initialState,
  },
});
```

Passo 2: o interceptor lê a store, nunca o cookie. A store é injetada por função para evitar import circular (`import type` some na compilação).

```ts
// services/api.ts
import type { store } from "../store/store";
import { logout } from "../store/authSlice";
type AppStore = typeof store;

let storeRef: AppStore;
export function configurarApi(s: AppStore) { storeRef = s; } // chamada em store.ts, logo após configureStore

api.interceptors.request.use((config) => {
  const token = storeRef.getState().auth.token;
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (resposta) => resposta,
  (erro) => {
    // Só desloga se HAVIA sessão: o 401 de senha errada no /auth/login não pode derrubar nada.
    // 403 (falta de perfil) não desloga: o token é válido.
    if (erro.response?.status === 401 && storeRef.getState().auth.token) {
      storeRef.dispatch(logout());
    }
    // mantenha aqui o bloco que já existe e monta new Error(mensagem) a partir do ProblemDetail do back
    return Promise.reject(erro);
  },
);
```

Passo 3: um timer desloga no instante do vencimento, mesmo sem nenhuma requisição. É isso que faz a tela de login aparecer sozinha.

```tsx
// components/ExpiracaoDoToken.tsx (renderizado dentro do <Provider>, em StoreProvider)
"use client";
export default function ExpiracaoDoToken() {
  const dispatch = useAppDispatch();
  const expiraEm = useAppSelector((s) => s.auth.expiraEm);

  useEffect(() => {
    if (!expiraEm) return;
    const venceEm = new Date(expiraEm).getTime();
    const verificar = () => { if (Date.now() >= venceEm) dispatch(logout()); };

    const timer = setTimeout(verificar, Math.max(venceEm - Date.now(), 0));
    // Abas em segundo plano têm timers atrasados pelo navegador: ao voltar, confere de novo.
    document.addEventListener("visibilitychange", verificar);
    // O cleanup evita timer duplicado (o modo estrito do React roda o efeito duas vezes em dev).
    return () => { clearTimeout(timer); document.removeEventListener("visibilitychange", verificar); };
  }, [expiraEm, dispatch]);
  return null;
}
```

Passo 4: o layout do painel observa o token e renderiza o login quando ele some, sem `window.location` nem `router.push`. Extraia o formulário de `login/page.tsx` para um componente `TelaLogin` e use-o nos dois lugares. O custo: a URL fica em `/livros` enquanto o login aparece; a vantagem é seguir o texto do PDF ("renderizar condicionalmente a tela de login") e não piscar.

```tsx
// (sistema)/layout.tsx
const token = useAppSelector((s) => s.auth.token);
if (!token) return <TelaLogin />;
```

Remova `sessao.ts`, `js-cookie`, `@types/js-cookie` e `sessaoRestaurada`; troque `obterEmailLogado()` por `useAppSelector` em `usuarios`, `usuarios/[id]/editar` e `livros/[id]` (a `home` não usa o cookie); o `Header` passa a despachar `logout()`. O cadastro (`cadastro/page.tsx`) também chama `salvarSessao` e precisa do mesmo ajuste.

**Três jeitos de mostrar o Teste 4:**

| Jeito | Como | O que prova |
| --- | --- | --- |
| Vencimento | `JWT_EXPIRATION_MINUTES=1`, logar e esperar | O login aparece sozinho, sem clicar (timer) |
| Token revogado | Apagar a linha da tabela `token` e recarregar a lista | 401 no interceptor de resposta vira `logout` e o login aparece |
| Logout no servidor | `POST /auth/logout` e repetir a lista | O mesmo caminho do 401 |

## As cinco exceções do token

Só um dos cinco lugares proibidos está violado hoje: o cookie. A nota da Aula 19.10 diz apenas "cinco hipóteses que não precisa se utilizar um token" e não lista quais são, então li o seu pedido como "os cinco lugares onde o token não pode ficar". Essa lista de cinco é minha, não do professor; se ele ditou outra, me diga quais e eu ajusto.

| Lugar proibido | Por quê | No seu repositório |
| --- | --- | --- |
| Cookie legível por JavaScript | Qualquer script na página (XSS) lê o token; o PDF manda guardar só na store | Violado: `sessao.ts` grava `token` e `email` com `js-cookie`; some quando o item do Redux for feito |
| `localStorage` | Sobrevive ao fechar a aba e é legível por qualquer script | Ok para o token: o único uso é o cache de capas (`capa:<isbn>`) em `lib/capas.ts` |
| `sessionStorage` | Mesmo problema do anterior, só dura menos | Ok: não aparece |
| URL ou query string | Vai para histórico, logs de servidor e cabeçalho `Referer` | Ok: nenhum `@RequestParam` de token no back nem token em link no front |
| Logs, código-fonte, Git, `NEXT_PUBLIC_*` | Quem lê o repositório ou o bundle lê o token (e o segredo que o assina) | Ok: `.env` está no `.gitignore` do back e do front; `NEXT_PUBLIC_API_URL` só tem a URL |

O único lugar permitido é a store do Redux, em memória, e por isso nada de `redux-persist`: ele gravaria a store no `localStorage` e reabriria o segundo problema da tabela.

**Se a nota quis dizer "cinco rotas que funcionam sem token":** hoje o `SecurityConfiguration` libera `/swagger-ui/**`, `/v3/api-docs/**`, `/swagger-resources/**`, `/webjars/**`, `/auth/login`, `/auth/registro`, `/error` e o preflight `OPTIONS`. Depois do bootstrapping a lista enxuta é: Swagger e a documentação OpenAPI, `/auth/login`, `/auth/bootstrap`, `/error` e o `OPTIONS`. `/auth/registro` só deve continuar público se criar usuário já com biblioteca (decisão 3). Troque `logging.level.org.springframework.security=DEBUG` por `INFO` antes de apresentar, para o console não virar ruído.

## Plano de trabalho, em ordem

Comece pelo Redux (rápido, cobre três requisitos e o Teste 4) e faça as portas de token, senha e usuário logado junto com a multilocação, porque os mesmos services são reescritos nas duas tarefas. As letras do esforço seguem o placar.

1. **Redux, interceptors e multilayout (M).** Tudo da seção sobre o token, mais: um segundo slice com funcionalidade real, sugerido pelo professor, que é favoritos de livros (adicionar, remover, limpar; estrela na lista de livros e filtro "só favoritos"); e o grupo de rotas `(publico)` com landing, login e cadastro sem menu lateral, mantendo `(sistema)` para o painel. Telas administrativas escondidas no menu e bloqueadas na rota para quem não é ADMINISTRADOR. *Pronto quando:* recarregar a página volta ao login, o token vence e o login aparece sozinho, e os favoritos funcionam na tela.
2. **Multilocação (G).** Decida o modelo (decisões 1 e 2) e então: migration V9 com `biblioteca_id`, chaves estrangeiras e índices; um único método nos services que descobre a biblioteca do logado; `findAllByBiblioteca_Id` e `findByIdAndBiblioteca_Id` nos repositories; criação sempre com a biblioteca do logado, nunca informada pelo front; acesso por id a registro de outra biblioteca devolve 404; filtro por perfil (só ADMINISTRADOR vê tudo). Conserte também `GET` e `PUT /usuarios/{id}` (filtro por biblioteca e restrição de quem pode editar) e escreva testes automatizados das regras. *Pronto quando:* logado como A, cria um livro; logado como B, a listagem não mostra e `GET /livros/{id}` no Swagger devolve 404.
3. **Bootstrapping e JavaFX (G).** Tire o seed da V5 (usuários e, de preferência, todo o acervo; editar a V5 muda o checksum, então recrie o banco com `docker compose down -v`). Crie `POST /auth/bootstrap`: público, só funciona se não existir nenhum usuário, exige uma chave do `.env` e devolve 409 depois que existe admin. Libere a rota no `SecurityConfiguration` e no `JwtFilter`. O app JavaFX fica em módulo separado (por exemplo `admin-desktop/`), com tela de chave, tela de primeira configuração e chamada com `java.net.http.HttpClient`; a chave vem de configuração, nunca fixa no código. Crie `.env.example` e um README com os passos para subir tudo do zero. *Pronto quando:* `docker compose down -v` e `up`, o back sobe, o front mostra o login, o JavaFX cria o admin e esse admin loga no front.
4. **ViaCEP (M).** Campos de endereço na Biblioteca (cep, logradouro, bairro, cidade, uf) em nova migration; porta `ConsultaCepPort` na `application` e `ViaCepClient` na `infrastructure` com `RestClient` (já disponível no Boot 4.1.1, sem mexer no `pom.xml`). Trate CEP malformado (400), CEP inexistente (o ViaCEP responde 200 com um objeto de erro, então devolva 404 ou 422 você mesmo), timeout e serviço fora do ar (503). Campo de CEP no cadastro da biblioteca no front, documentado no Swagger. *Pronto quando:* com um CEP real o endereço volta preenchido e com um inexistente a API responde com erro claro.
5. **Portas restantes (G).** Repositories como interfaces puras em `domain/repository` com adaptadores em `infrastructure/persistence` que usam o Spring Data por dentro; exceção própria no lugar de `ResponseStatusException` em `UsuarioService` e `LivroService`, tratada no `ApiExceptionHandler`; `AuthService` dependendo da porta de token, não de `TokenService`. Atualize o `docs/arquitetura.md`, que descreve uma estrutura que não existe. O modelo JPA separado com mappers é opcional; as portas são o que o PDF exige.
6. **JPA (P).** `@OneToMany(mappedBy = "livro")` em Livro para exemplares, `@OneToMany(mappedBy = "leitor")` em Leitor para empréstimos, e `@OneToMany(mappedBy = "biblioteca")` em Biblioteca. Remova `perfil` e `status` redeclarados em `Leitor` (já existem em `Usuario`). Remova `enable_lazy_load_no_trans` montando os DTOs dentro da transação, ou esteja pronto para justificar o atalho.
7. **Limpeza e ensaio (P).** `./mvnw verify` verde; `git mv` em duas etapas para `valueObject` para `valueobject` e `ExemplarCOntroller` para `ExemplarController` (o Windows ignora a diferença de caixa); README e `.env.example`; ESLint; nível de log do Spring Security em `INFO`. Ensaie o roteiro abaixo duas vezes com o banco limpo.

## Decisões pendentes

Cinco escolhas mudam o que você implementa; deixei a minha recomendação em cada uma.

1. **Admin global ou admin de uma biblioteca?** Recomendo admin global: o bootstrap cria só o primeiro ADMINISTRADOR, sem biblioteca, que enxerga tudo e cria as bibliotecas (`POST` e `GET /bibliotecas`) e os usuários A e B em bibliotecas diferentes. Isso já cumpre "primeiro usuário Administrador" do PDF e permite o Teste 3. Um admin preso a uma biblioteca seria filtrado como os demais e você não teria como criar uma segunda biblioteca.
2. **Quais entidades têm dono?** Recomendo `biblioteca_id` em Livro, Autor e Empréstimo, com Exemplar herdando pelo livro (a Editora não tem controller nem ligação com Livro hoje, então fica de fora). O mínimo defensável é Livro e Empréstimo, mas aí um usuário da biblioteca B vê os autores da A, e o professor pode perguntar por quê.
3. **O que fazer com `/auth/registro`?** Recomendo tirar o registro público da demonstração: hoje ele cria um Leitor sem biblioteca, e com a regra por perfil esse Leitor ficaria sem acesso ou, pior, com acesso a tudo. Usuários passam a ser criados por admin ou bibliotecário, e a página `cadastro` do front sai ou vira um pedido de acesso.
4. **JavaFX ou outra tecnologia no desktop?** O PDF pede "Aplicação Desktop com JavaFX separada". Se você quiser fazer em Rust, peça ao professor a autorização por escrito antes; sem ela, é requisito não atendido e o Teste 1 depende dele.
5. **Recarregar a página deslogar é aceitável?** É a consequência de guardar o token só na store, e é o que o PDF descreve. Vale confirmar com o professor, já que a Aula 19.10 ensinou cookie.

## Roteiro de ensaio

Os seis testes do PDF, com os ajustes que decorrem desta análise. Ensaie com o banco limpo e o relógio do token em 1 minuto.

| # | Teste | Passo a passo | Cuidado |
| --- | --- | --- | --- |
| 1 | Bootstrapping | `docker compose down -v`, subir o back, abrir o front (login), abrir o JavaFX, criar o admin, logar no front | Chave do bootstrap vem do `.env`, não do código |
| 2 | Segurança | Pegar o token no Swagger (Authorize), chamar um endpoint protegido, desligar a segurança no `SecurityConfiguration`, reiniciar e chamar o endpoint sem token | Use o endpoint que não depende do usuário logado (por exemplo a lista de gêneros); os que filtram por biblioteca vão dar 500 |
| 3 | Multilocação | Logado como A, criar um livro; sair; logar como B; conferir a lista vazia; `GET /livros/{id}` no Swagger deve dar 404 | A e B em bibliotecas diferentes, criados pelo admin global |
| 4 | Redux e interceptor | Logar, esperar o token vencer (ou apagar a linha na tabela `token`), recarregar a lista; o login aparece sozinho; mostrar os favoritos | Com `JWT_EXPIRATION_MINUTES=1` o login aparece sem nenhuma requisição |
| 5 | Integração externa | Cadastrar a biblioteca informando só o CEP e mostrar o endereço preenchido; tentar um CEP inexistente | Tenha um CEP real anotado e confirme que a internet da sala libera o ViaCEP |
| 6 | Explicação | Camadas e portas, `@OneToMany`, JOINED em `Usuario` e `Leitor`, `@ManyToMany` de autor, Redux (store, slice, reducers, como o interceptor acessa a store) | Prepare o desenho das dependências entre camadas |

Não rodei o build, os testes nem o front: tudo acima vem da leitura do código no commit b5d6012. Rode `./mvnw verify` e `npm run build` no início de cada bloco para saber o que quebrou.

## Guia do back, arquivo por arquivo

Cada passo abaixo diz o arquivo e o que fazer nele. Caminhos do Java são relativos a `src/main/java/br/com/senac/bibliotech/` (escrevo `…/`); os de recursos, a `src/main/resources/`. Faça um commit por passo e rode `./mvnw verify` ao fim de cada um. Nenhum passo precisa mexer no `pom.xml` do back. Não compilei nada disto: os trechos de código são o esqueleto, e a IDE vai apontar imports e pequenos ajustes.

### B1. Login devolve o usuário, o token expira por configuração e existe logout

Por quê: o Redux precisa dos dados do usuário e do vencimento, e o Teste 4 precisa de um jeito rápido de expirar o token.

| Arquivo | O que fazer |
| --- | --- |
| `…/application/dto/UsuarioLogadoResponse.java` (novo) | Record com `Long id`, `String nome`, `String email`, `EnumPerfil perfil`, `Long bibliotecaId` e um `static from(Usuario)`. DTO próprio para não vazar CPF nem senha |
| `…/application/dto/LoginResponse.java` | Acrescente o campo `UsuarioLogadoResponse usuario` no fim do record |
| `…/application/service/AuthService.java` | No `login()`, troque `new LoginResponse(token.valor(), "Bearer", token.expiraEm())` por uma chamada que inclui `UsuarioLogadoResponse.from(usuario)` |
| `…/application/service/TokenService.java` | Método novo `@Transactional void revogar(String token)`: `tokenRepository.findTokenByToken(token).ifPresent(tokenRepository::delete)` |
| `…/application/service/AuthService.java` | Método novo `logout(String token)` que só delega para `tokenService.revogar(token)` (o controller não deve falar com o `TokenService`) |
| `…/presentation/AuthController.java` | `@PostMapping("/logout")`: receba `@RequestHeader("Authorization") String cabecalho`, tire o prefixo ` Bearer  `, chame `authService.logout(...)` e devolva 204. Não coloque `/auth/logout` na lista pública: o `anyRequest().authenticated()` já protege |
| `application.properties` | Troque `api.security.token.expiration-minutes=60` por `api.security.token.expiration-minutes=${JWT_EXPIRATION_MINUTES:60}` e `logging.level.org.springframework.security=DEBUG` por `INFO` |
| `.env` (raiz, fora do Git) | Acrescente `JWT_EXPIRATION_MINUTES=1` só durante os ensaios do Teste 4; volte para 60 depois |

**Pronto quando:** o `POST /auth/login` no Swagger devolve `usuario` junto com o token; com `JWT_EXPIRATION_MINUTES=1` o `expiraEm` vem 1 minuto à frente; `POST /auth/logout` com o token devolve 204 e um `GET /livros` logo depois devolve 401.

### B2. Multilocação

Por quê: o Teste 3 exige que o usuário B não veja nem alcance, pela API, o que o A criou. A regra do filtro mora em um lugar só, e o acesso por id também filtra, devolvendo 404 (é assim que a API "bloqueia"). Pressupõe as decisões 1 e 2: admin global e dono em Livro, Autor e Empréstimo.

**1. Migration** `…/resources/db/migration/V9__multilocacao.sql` (novo). As colunas já podem ser `NOT NULL` se você tirar o seed (B3) e recriar o banco antes; com dados antigos, deixe sem `NOT NULL`.

```sql
-- Dono dos registros: a biblioteca (o "tenant").
ALTER TABLE livro      ADD COLUMN IF NOT EXISTS biblioteca_id BIGINT;
ALTER TABLE autor      ADD COLUMN IF NOT EXISTS biblioteca_id BIGINT;
ALTER TABLE emprestimo ADD COLUMN IF NOT EXISTS biblioteca_id BIGINT;

ALTER TABLE livro      ADD CONSTRAINT fk_livro_biblioteca      FOREIGN KEY (biblioteca_id) REFERENCES biblioteca (id);
ALTER TABLE autor      ADD CONSTRAINT fk_autor_biblioteca      FOREIGN KEY (biblioteca_id) REFERENCES biblioteca (id);
ALTER TABLE emprestimo ADD CONSTRAINT fk_emprestimo_biblioteca FOREIGN KEY (biblioteca_id) REFERENCES biblioteca (id);

-- O filtro roda em TODA consulta: sem índice, cada listagem varre a tabela inteira.
CREATE INDEX IF NOT EXISTS idx_livro_biblioteca      ON livro (biblioteca_id);
CREATE INDEX IF NOT EXISTS idx_autor_biblioteca      ON autor (biblioteca_id);
CREATE INDEX IF NOT EXISTS idx_emprestimo_biblioteca ON emprestimo (biblioteca_id);
```

**2. Entidades.** Nos três arquivos `…/domain/entities/Livro.java`, `Autor.java` e `Emprestimo.java`, acrescente o campo abaixo. `LAZY` porque o filtro é feito na query e não precisamos carregar a biblioteca inteira a cada livro.

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "biblioteca_id")
private Biblioteca biblioteca;
```

**3. Quem é o escopo do logado.** Arquivo novo `…/application/service/BibliotecaDoLogado.java`. Corrige o erro de hoje ("sem biblioteca vê tudo" para qualquer perfil): só o admin global é irrestrito.

```java
@Component
@RequiredArgsConstructor
public class BibliotecaDoLogado {
    private final UsuarioService usuarioService;

    /** Id da biblioteca que filtra as consultas; null = admin global, sem filtro. */
    public Long idOuNull() {
        UsuarioLogado logado = usuarioService.getUsuarioLogado();
        boolean admin = logado.authorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
        if (logado.bibliotecaId() == null) {
            if (admin) return null;                       // admin global enxerga tudo
            throw new AcessoNegadoException("Usuário sem biblioteca"); // os demais NÃO veem nada
        }
        return logado.bibliotecaId();
    }
}
```

Crie também `…/application/exception/AcessoNegadoException.java` (`extends RuntimeException`) e, em `…/presentation/ApiExceptionHandler.java`, um `@ExceptionHandler(AcessoNegadoException.class)` que devolve `HttpStatus.FORBIDDEN`, igual aos handlers que já existem ali.

**4. Repositories** (em `…/domain/repository/`). Os nomes abaixo são "derived queries": o Spring monta o SQL pelo nome, e `Biblioteca_Id` navega para `biblioteca.id`.

| Arquivo | Acrescente |
| --- | --- |
| `LivroRepository.java` | `List<Livro> findAllByBiblioteca_Id(Long bibliotecaId);` e `Optional<Livro> findByIdAndBiblioteca_Id(Long id, Long bibliotecaId);` |
| `AutorRepository.java` | Os mesmos dois métodos, com `Autor` |
| `ExemplarRepository.java` | `List<Exemplar> findByLivroIdAndLivro_Biblioteca_Id(Long livroId, Long bibliotecaId);` e `Optional<Exemplar> findByIdAndLivro_Biblioteca_Id(Long id, Long bibliotecaId);` (o exemplar herda a biblioteca pelo livro) |
| `EmprestimoRepository.java` | Uma cópia da `@Query` de `findByStatusEmprestimo` com `AND e.biblioteca.id = :bibliotecaId` e o parâmetro `@Param("bibliotecaId") Long bibliotecaId`; mais `Optional<Emprestimo> findByIdAndBiblioteca_Id(Long id, Long bibliotecaId);` |

**5. Services** (em `…/application/service/`). Injete `BibliotecaDoLogado` e o repository de biblioteca. O padrão é o mesmo em todos: pergunte o escopo uma vez e escolha a consulta.

```java
// LivroService: o mesmo desenho serve para os outros.
private Livro buscarNoEscopo(Long id) {
    Long escopo = bibliotecaDoLogado.idOuNull();
    return (escopo == null
            ? livroRepository.findById(id)
            : livroRepository.findByIdAndBiblioteca_Id(id, escopo))
            .orElseThrow(() -> new RecursoNaoEncontradoException("Livro", id)); // 404, mesmo se existir em outra biblioteca
}
```

| Arquivo | O que fazer |
| --- | --- |
| `LivroService.java` | `criar`: o livro nasce com `.biblioteca(bibliotecaRepository.getReferenceById(escopo))`; se `escopo == null` (admin global), lance 400 dizendo que livro se cadastra dentro de uma biblioteca. `listarTodos`: `escopo == null ? findAll() : findAllByBiblioteca_Id(escopo)`. `buscarPorId`, `atualizar` e `deletar`: use `buscarNoEscopo(id)`. Em `atualizar`, troque o `ResponseStatusException` por `RecursoNaoEncontradoException` |
| `AutorService.java` | Mesmo padrão em `listarTodos` (linha 41), nos dois `findById` (linhas 49 e 56) e na criação. Na linha 85, `livroRepository.findAllById(desejados)` aceita livros de outra biblioteca: filtre o resultado pelo escopo e rejeite se algum id não pertencer |
| `ExemplarService.java` | `criar`: busque o livro com o escopo (não `findById` direto). `listarPorLivro` e `atualizarStatus`: use os métodos `…Livro_Biblioteca_Id` |
| `EmprestimoService.java` | `realizarEmprestimo`: confira que o leitor e o exemplar são do escopo e grave `emprestimo.setBiblioteca(...)` com a biblioteca do exemplar (acrescente o campo no `Emprestimo.registrar`). `devolverEmprestimo` e `listarEmprestimos`: use as consultas com escopo |
| `UsuarioService.java` | `buscarEntidade(id)`: além do filtro de `EXCLUIDO`, rejeite com 404 quem não é do escopo (`escopo != null` e a biblioteca do usuário difere). `listar()`: use `bibliotecaDoLogado.idOuNull()` no lugar do `if (logado.bibliotecaId() == null)`. `cadastrar`: com `escopo != null`, a biblioteca é a do logado; com admin global, aceite um `bibliotecaId` opcional no `UsuarioRequest` e ignore esse campo para qualquer outro perfil |

**6. Quem pode chamar o quê** em `…/infrastructure/security/SecurityConfiguration.java`, antes do `.anyRequest().authenticated()`: restrinja `PUT /usuarios/**` a `hasAnyRole("ADMINISTRADOR", "BIBLIOTECARIO")` e `/bibliotecas/**` a `hasRole("ADMINISTRADOR")`.

**7. Bibliotecas** (necessário para criar A e B em lugares diferentes):

| Arquivo (novo) | Conteúdo |
| --- | --- |
| `…/application/dto/BibliotecaRequest.java` | `nome` e `cnpj`, com `@NotBlank`; o CEP entra no B4 |
| `…/application/dto/BibliotecaResponse.java` | `id`, `nome`, `cnpj` e `static from(Biblioteca)` |
| `…/application/service/BibliotecaService.java` | `criar(BibliotecaRequest)` e `listar()` |
| `…/presentation/BibliotecaController.java` | `@RequestMapping("/bibliotecas")` com `POST` (201) e `GET`, no mesmo molde do `LivroController` (anotações `@Tag`, `@Operation`, `@ApiResponses`) |

**8. Teste automatizado:** `src/test/java/br/com/senac/bibliotech/application/MultilocacaoTest.java`. Com Mockito (já vem no `spring-boot-starter-test`), simule um logado da biblioteca 1 e confirme que `buscarPorId` de um livro da biblioteca 2 lança `RecursoNaoEncontradoException`, e que um não admin sem biblioteca recebe `AcessoNegadoException`.

**Pronto quando:** logado como A (biblioteca 1), você cria um livro; logado como B (biblioteca 2), `GET /livros` volta vazio e `GET /livros/{id}` do livro de A devolve 404.

### B3. Bootstrapping: banco limpo, primeiro admin e app JavaFX

Por quê: o Teste 1 começa com o banco apagado e cria o primeiro usuário pelo desktop, então nenhum `INSERT` pode existir nas migrations, e o endpoint que cria o primeiro admin precisa ser público mas só funcionar uma vez. Pressupõe a decisão 1 (admin global, sem biblioteca) e a decisão 3 (sem registro público).

**1. Tirar o seed.** Em `…/resources/db/migration/V5__adicionar_colunas_emprestimo_e_seed_data.sql`, apague do cabeçalho `SEED DE DADOS` (linha 46, junto com a linha de `====` acima dela) até a linha 122, que termina logo antes de `6. NOT NULL condicional`. Isso remove editoras, autores, livros, os três usuários (incluindo `admin@bibliotech.com`), os leitores e os exemplares. Deixe as seções 1 a 5 (estrutura) e a 6 em diante. As `UPDATE` da V6 e da seção 7 continuam inofensivas em tabelas vazias. Como o conteúdo da migration mudou, o Flyway recusa bancos já migrados: recrie o banco.

```bash
docker compose down -v   # -v apaga o volume do Postgres
docker compose up -d
```

**2. Endpoint de bootstrap.**

| Arquivo | O que fazer |
| --- | --- |
| `application.properties` | Acrescente `app.bootstrap.chave=${BOOTSTRAP_KEY:}` (vazia = bootstrap desligado, igual ao que você já faz com `ADMIN_PROMOTION_PASSWORD`) |
| `.env` | Acrescente `BOOTSTRAP_KEY=` com uma chave longa e aleatória |
| `…/application/dto/BootstrapRequest.java` (novo) | Record com `nome`, `email`, `senha` e `cpf`, com as mesmas validações do `UsuarioRequest`. A chave NÃO vai no corpo: vai no cabeçalho `X-Bootstrap-Key` |
| `…/application/service/BootstrapService.java` (novo) | `@Transactional UsuarioResponse criarPrimeiroAdmin(String chave, BootstrapRequest r)` (regras abaixo) |
| `…/presentation/AuthController.java` | `@PostMapping("/bootstrap")` recebendo `@RequestHeader("X-Bootstrap-Key") String chave` e o corpo; devolve 201 |
| `…/infrastructure/security/SecurityConfiguration.java` | Acrescente `"/auth/bootstrap"` na lista do `permitAll()` |
| `…/infrastructure/security/JwtFilter.java` | No método `shouldNotFilter`, acrescente a condição `uri.equals("/auth/bootstrap")`, ligada às outras pelo operador OU, ao lado da de `/auth/login` |
| `…/presentation/ApiExceptionHandler.java` | O handler de `AcessoNegadoException` (feito no B2) cobre a chave errada; o de `ConflitoException` já devolve 409 |

Regras do `criarPrimeiroAdmin`, nesta ordem: (a) compare a chave com `MessageDigest.isEqual` (copie o método `senhaEspecialConfere` do `UsuarioService`) e, se não conferir ou se a chave configurada estiver vazia, lance `AcessoNegadoException`, que vira 403; (b) se `usuarioRepository.count() > 0`, lance `ConflitoException("O sistema já foi inicializado")`, que vira 409; (c) valide o CPF com `new CPF(r.cpf())`; (d) crie o `Usuario` com `perfil = ADMINISTRADOR`, `status = ATIVO`, senha com `passwordEncoder.encode(...)` e **sem biblioteca**. A chave vem primeiro de propósito: quem não a conhece recebe 403 e não descobre se o sistema já foi inicializado.

**3. Tirar o registro público.** Apague `registrar(...)` e `@PostMapping("/registro")` do `AuthController`, tire `"/auth/registro"` do `SecurityConfiguration` e do `JwtFilter`. Se o `UsuarioService.cadastrar` só era usado por ele, ele continua servindo ao `POST /usuarios` (admin e bibliotecário).

**4. Documentar como subir do zero.**

| Arquivo (novo, na raiz) | Conteúdo |
| --- | --- |
| `.env.example` | As chaves, sem valores: `DB_USER`, `DB_PASSWORD`, `DB_NAME`, `JWT_SECRET` (mínimo 32 caracteres, o `TokenService` recusa menos), `JWT_EXPIRATION_MINUTES`, `ADMIN_PROMOTION_PASSWORD`, `BOOTSTRAP_KEY` |
| `README.md` | Passos numerados: copiar `.env.example` para `.env` e preencher; `docker compose up -d`; `./mvnw spring-boot:run`; `cd front && npm install && npm run dev`; abrir o app desktop e criar o admin; logar no front |

**5. App desktop em JavaFX**, em pasta separada da raiz do repositório, `admin-desktop/`, com `pom.xml` próprio (não é o do back, então o `pom.xml` do back continua intacto). Use como molde o módulo desktop do projeto do professor.

| Arquivo (dentro de `admin-desktop/`) | O que fazer |
| --- | --- |
| `pom.xml` | Dependências `javafx-controls` e `javafx-fxml` e o `javafx-maven-plugin` para `mvn javafx:run`; mesma versão de Java do back (25) |
| `src/main/java/.../App.java` | `extends Application`; `start(Stage)` carrega a primeira tela |
| `src/main/java/.../ConfigDesktop.java` | Lê `API_URL` (padrão `http://localhost:8080`) de variável de ambiente ou de `config.properties`; nada de URL nem chave fixas no código |
| `src/main/java/.../BootstrapClient.java` | `java.net.http.HttpClient` fazendo `POST {API_URL}/auth/bootstrap` com `Content-Type: application/json` e o cabeçalho `X-Bootstrap-Key`; devolve sucesso, 409 ("já inicializado") ou erro de conexão em mensagens legíveis |
| `src/main/resources/.../chave.fxml` + controller | Tela 1: campo da chave e botão "Continuar" |
| `src/main/resources/.../primeira-configuracao.fxml` + controller | Tela 2: nome, e-mail, CPF e senha do administrador; ao salvar, chama o `BootstrapClient` e mostra o resultado; faça a chamada fora da thread da interface (`Task`) para a janela não congelar |

**Pronto quando:** você roda `docker compose down -v`, `docker compose up -d`, sobe o back e o front (login), cria o admin pelo desktop e consegue logar com ele no front. Repetir o desktop devolve 409.

### B4. Integração externa com o ViaCEP

Por quê: o Teste 5 cadastra uma biblioteca só com o CEP e mostra o endereço preenchido pela API externa. A chamada fica atrás de uma porta (`ConsultaCepPort`), o que de quebra já entrega parte da inversão de dependência que o PDF pede.

| Arquivo | O que fazer |
| --- | --- |
| `…/resources/db/migration/V10__endereco_biblioteca.sql` (novo) | `ALTER TABLE biblioteca ADD COLUMN IF NOT EXISTS` para `cep VARCHAR(8)`, `logradouro VARCHAR(255)`, `bairro VARCHAR(255)`, `cidade VARCHAR(255)` e `uf VARCHAR(2)` |
| `…/domain/entities/Biblioteca.java` | Os mesmos cinco campos, como `String` |
| `…/application/dto/EnderecoCep.java` (novo) | Record com `cep`, `logradouro`, `bairro`, `cidade`, `uf` |
| `…/application/port/ConsultaCepPort.java` (novo, pacote novo `port`) | Interface com `Optional<EnderecoCep> consultar(String cep)` |
| `…/infrastructure/http/ViaCepClient.java` (novo) | `@Component` que implementa a porta (código abaixo) |
| `…/application/exception/ServicoExternoIndisponivelException.java` (novo) | `extends RuntimeException`; trata timeout e serviço fora do ar |
| `…/presentation/ApiExceptionHandler.java` | Handler de `ServicoExternoIndisponivelException` devolvendo 503 |
| `…/application/dto/BibliotecaRequest.java` | Acrescente `String cep` com `@Pattern(regexp = "\\d{8}", message = "O CEP deve ter 8 dígitos")`; a validação devolve 400 sozinha, antes de chamar o ViaCEP |
| `…/application/dto/BibliotecaResponse.java` | Acrescente os cinco campos de endereço |
| `…/application/service/BibliotecaService.java` | No `criar`, se vier CEP, chame `consultaCepPort.consultar(...)`; `Optional` vazio vira `RecursoNaoEncontradoException("CEP não encontrado")` (404); preencha logradouro, bairro, cidade e uf a partir da resposta e nunca do que o cliente mandou. Método novo `consultarCep(String cep)` para a prévia |
| `…/presentation/BibliotecaController.java` | `@GetMapping("/cep/{cep}")` devolvendo `EnderecoCep` (a prévia que o front usa ao sair do campo) e documente 400, 404 e 503 com `@ApiResponses` |

```java
// …/infrastructure/http/ViaCepClient.java
@Component
public class ViaCepClient implements ConsultaCepPort {

    private final RestClient http;

    public ViaCepClient() {
        // Timeouts: sem eles, um ViaCEP lento prende a thread da requisição para sempre.
        var cliente = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
        var fabrica = new JdkClientHttpRequestFactory(cliente);
        fabrica.setReadTimeout(Duration.ofSeconds(3));
        // RestClient.builder() (estático) em vez de injetar o Builder: não dependemos de autoconfiguração.
        this.http = RestClient.builder().baseUrl("https://viacep.com.br/ws").requestFactory(fabrica).build();
    }

    @Override
    public Optional<EnderecoCep> consultar(String cep) {
        try {
            ViaCepResposta r = http.get().uri("/{cep}/json/", cep).retrieve().body(ViaCepResposta.class);
            // CEP bem formado mas inexistente: o ViaCEP responde 200 com {"erro": true}, NÃO 404.
            if (r == null || Boolean.TRUE.equals(r.erro())) return Optional.empty();
            return Optional.of(new EnderecoCep(cep, r.logradouro(), r.bairro(), r.localidade(), r.uf()));
        } catch (RestClientException e) {
            throw new ServicoExternoIndisponivelException("Consulta de CEP indisponível no momento", e);
        }
    }

    // Formato da resposta do ViaCEP (só o que usamos). Campo desconhecido é ignorado.
    private record ViaCepResposta(String logradouro, String bairro, String localidade, String uf, Boolean erro) {}
}
```

Se algum import de `RestClient` ou `JdkClientHttpRequestFactory` não resolver na IDE, o `spring-boot-starter-web` do seu `pom.xml` já traz o `spring-web`; me avise antes de mexer no `pom.xml` e eu ajusto o caminho.

**Pronto quando:** com um CEP real (anotado antes do ensaio), `GET /bibliotecas/cep/{cep}` devolve o endereço e o `POST /bibliotecas` só com `nome`, `cnpj` e `cep` grava o endereço preenchido; com `00000000` a API responde 404 com mensagem clara; com `123` responde 400; sem internet, 503.

### B5. Portas e inversão de dependência

Por quê: o PDF exige que as camadas conversem por interfaces. O caminho mínimo defensável é: a `application` só conhece interfaces (portas) que ela mesma declara, e a `infrastructure` as implementa. Faça depois da multilocação e do ViaCEP, porque os mesmos services são tocados.

**1. Portas de segurança** (pacote novo `…/application/port/`):

| Arquivo (novo) | Conteúdo |
| --- | --- |
| `TokenPort.java` | `TokenGerado gerar(Usuario u)`, `UsuarioLogado verificar(String token)`, `void revogar(String token)`; mova para cá o record `TokenGerado` que hoje mora dentro do `TokenService` |
| `SenhaPort.java` | `String codificar(String senhaPura)` e `boolean confere(String senhaPura, String hash)` |
| `UsuarioLogadoPort.java` | `Optional<UsuarioLogado> atual()` |

**2. Adaptadores** (em `…/infrastructure/security/`):

| Arquivo | O que fazer |
| --- | --- |
| `JwtTokenAdapter.java` | É o `TokenService` atual: mova o arquivo (use o refactor Move do IntelliJ), renomeie e faça `implements TokenPort` |
| `BCryptSenhaAdapter.java` (novo) | `implements SenhaPort`, delegando ao `PasswordEncoder` que o `PasswordEncoderConfig` já expõe |
| `SecurityContextUsuarioLogadoAdapter.java` (novo) | `implements UsuarioLogadoPort`; leve para cá o corpo de `usuarioLogadoOuNulo()` do `UsuarioService` |

**3. Quem passa a depender das portas:** `AuthService` (de `TokenService` e `PasswordEncoder` para `TokenPort` e `SenhaPort`); `UsuarioService` (de `PasswordEncoder` e `SecurityContextHolder` para `SenhaPort` e `UsuarioLogadoPort`); `SecurityConfiguration` e `JwtFilter` (de `TokenService` para `TokenPort`).

**4. Repositories como portas.** Hoje `domain/repository/LivroRepository` estende `JpaRepository`, ou seja, o domínio conhece o Spring Data. Inverta: a interface do domínio fica pura e o Spring Data vira detalhe da infraestrutura. Faça primeiro Livro, Usuario e Token (são os que o professor mais provavelmente abre); depois Autor, Exemplar, Emprestimo, Leitor e Biblioteca, do mesmo jeito.

```java
// …/domain/repository/LivroRepository.java: porta pura, sem import do Spring
public interface LivroRepository {
    Livro salvar(Livro livro);
    Optional<Livro> buscarPorId(Long id);
    Optional<Livro> buscarPorIdNaBiblioteca(Long id, Long bibliotecaId);
    List<Livro> listarTodos();
    List<Livro> listarDaBiblioteca(Long bibliotecaId);
    void remover(Long id);
}

// …/infrastructure/persistence/LivroJpaRepository.java: detalhe do Spring Data (pacote-privado)
interface LivroJpaRepository extends JpaRepository<Livro, Long> {
    List<Livro> findAllByBiblioteca_Id(Long bibliotecaId);
    Optional<Livro> findByIdAndBiblioteca_Id(Long id, Long bibliotecaId);
}

// …/infrastructure/persistence/LivroRepositoryAdapter.java: liga a porta ao Spring Data
@Repository
@RequiredArgsConstructor
class LivroRepositoryAdapter implements LivroRepository {
    private final LivroJpaRepository jpa;
    public Livro salvar(Livro l) { return jpa.save(l); }
    public Optional<Livro> buscarPorId(Long id) { return jpa.findById(id); }
    public Optional<Livro> buscarPorIdNaBiblioteca(Long id, Long b) { return jpa.findByIdAndBiblioteca_Id(id, b); }
    public List<Livro> listarTodos() { return jpa.findAll(); }
    public List<Livro> listarDaBiblioteca(Long b) { return jpa.findAllByBiblioteca_Id(b); }
    public void remover(Long id) { jpa.deleteById(id); }
}
```

Nos services, troque as chamadas (`findById` por `buscarPorId`, `save` por `salvar` e assim por diante). A entidade JPA continua sendo o modelo de domínio; separar um modelo de domínio puro com mappers é opcional, e a justificativa oral é: "o domínio ainda usa as anotações do JPA por pragmatismo, mas nenhuma regra depende do Spring Data".

**5. Exceções próprias.** Em `UsuarioService.atualizarPerfil`, troque `new ResponseStatusException(HttpStatus.FORBIDDEN, ...)` por `AcessoNegadoException` (a do B2); em `LivroService`, o que sobrar de `ResponseStatusException` vira `RecursoNaoEncontradoException`. Depois remova os imports de `HttpStatus` e `ResponseStatusException` da camada `application`.

**6. Nomes e documentação.**

```bash
# O Windows ignora diferença de maiúscula; sem a etapa intermediária o Git não registra o rename.
git mv src/main/java/br/com/senac/bibliotech/domain/valueObject src/main/java/br/com/senac/bibliotech/domain/tmp
git mv src/main/java/br/com/senac/bibliotech/domain/tmp src/main/java/br/com/senac/bibliotech/domain/valueobject
git mv src/main/java/br/com/senac/bibliotech/presentation/ExemplarCOntroller.java src/main/java/br/com/senac/bibliotech/presentation/Tmp.java
git mv src/main/java/br/com/senac/bibliotech/presentation/Tmp.java src/main/java/br/com/senac/bibliotech/presentation/ExemplarController.java
```

Dentro do arquivo, renomeie a classe para `ExemplarController` (no IntelliJ, Shift+F6 sobre o nome). Reescreva `docs/arquitetura.md` com a estrutura real (`presentation`, `application` com `dto`, `exception`, `port` e `service`, `domain` com `entities`, `enums`, `exception`, `repository` e `valueobject`, `infrastructure` com `config`, `security`, `http` e `persistence`) e apague o trecho sobre bounded contexts que não existem. Um teste ArchUnit é opcional e exigiria adicionar uma dependência de teste no `pom.xml`; faça só com a sua autorização.

**Pronto quando:** `grep -rnE "HttpStatus|ResponseStatusException|SecurityContextHolder|PasswordEncoder|JpaRepository" src/main/java/br/com/senac/bibliotech/application src/main/java/br/com/senac/bibliotech/domain` não devolve nada, e a aplicação sobe e os testes passam.

### B6. Relacionamentos JPA e limpeza

Por quê: o PDF cita `@OneToMany` e o professor pede para explicar os relacionamentos.

| Arquivo | O que fazer |
| --- | --- |
| `…/domain/entities/Livro.java` | Acrescente o lado inverso, sem cascade de propósito: apagar um livro não deve apagar exemplares em silêncio (a FK já faz o `DELETE` falhar e o `ApiExceptionHandler` devolve 409). Código abaixo |
| `…/domain/entities/Leitor.java` | Acrescente `@OneToMany(mappedBy = "leitor") @Builder.Default private List<Emprestimo> emprestimos = new ArrayList<>();` e **apague** `perfil` e `status` redeclarados (as colunas só existem em `usuario`, conforme a V3). Suba a aplicação com `ddl-auto=validate` para confirmar |
| `…/domain/entities/Biblioteca.java` | `@OneToMany(mappedBy = "biblioteca")` para `List<Usuario> usuarios` e `List<Livro> livros` |
| `application.properties` | Apague `spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true` e o comentário acima dela. Abra cada tela; onde aparecer `LazyInitializationException`, ponha `@Transactional(readOnly = true)` no método do service que monta o DTO. Se preferir manter o atalho, esteja pronto para justificar |

```java
// Livro.java: lado inverso do ManyToOne de Exemplar. Não cria coluna; quem manda é Exemplar.livro.
@OneToMany(mappedBy = "livro")
@Builder.Default
private List<Exemplar> exemplares = new ArrayList<>();
```

Os DTOs (`LivroResponse` etc.) não incluem essas listas, então não há laço de serialização. Mantenha assim.

**Pronto quando:** `docker compose up -d` e `./mvnw verify` passam e você consegue desenhar no papel o mapa: `Usuario` (herança JOINED) para `Leitor`, `Livro` 1-N `Exemplar`, `Leitor` 1-N `Emprestimo`, `Autor` N-N `Livro`, `Biblioteca` 1-N dono de tudo.

## Guia do front, arquivo por arquivo

Caminhos relativos a `front/`; a pasta `app/` é a do Next. Faça um commit por passo e rode `npm run lint` e `npm run build` ao fim de cada um. O B1 do back precisa estar pronto antes do F1, porque o login passa a devolver o usuário.

### F1. Token, vencimento e usuário só na store (sem cookie)

Por quê: o PDF exige token e dados do usuário exclusivamente na store, o interceptor de requisição lendo da store e o 401 despachando logout. Os trechos de `authSlice`, `api.ts` e `ExpiracaoDoToken` estão na seção "Token: expirar e voltar ao login sozinho"; abaixo estão os arquivos onde cada um entra, na ordem de trabalho.

| Arquivo | O que fazer |
| --- | --- |
| `app/types/auth.ts` | Acrescente `UsuarioLogado` (`id`, `nome`, `email`, `perfil: Perfil`, `bibliotecaId: number ou null`; importe `Perfil` de `types/usuario.ts`) e faça `LoginResponse` ser `{ token, tipo, expiraEm, usuario: UsuarioLogado }` |
| `app/store/authSlice.ts` | Substitua o arquivo inteiro pelo do passo 1 da seção do token (`login` e `logout`). Apague `sessaoIniciada`, `sessaoRestaurada` e `sessaoEncerrada` |
| `app/services/api.ts` | Apague os imports de `js-cookie` e de `./sessao`; troque os dois interceptors pelos do passo 2; exporte `configurarApi`. Mantenha o bloco que lê `corpo.detail` e monta `new Error(mensagem)` |
| `app/store/store.ts` | Importe `configurarApi` de `../services/api` e chame `configurarApi(store);` logo depois do `configureStore`. Os tipos `RootState` e `AppDispatch` ficam como estão |
| `app/services/authService.ts` | Acrescente `export async function logoutNoServidor(): Promise<void> { try { await api.post("/auth/logout"); } catch { /* token já inválido: tudo bem */ } }` |
| `app/components/ExpiracaoDoToken.tsx` (novo) | O componente do passo 3 da seção do token |
| `app/components/StoreProvider.tsx` | Reescreva como abaixo |
| `app/components/TelaLogin.tsx` (novo) | Mova para cá o formulário que hoje está em `app/login/page.tsx` e troque o `entrar()` como abaixo |
| `app/login/page.tsx` | Fica só com `export default function Page() { return <TelaLogin />; }` |
| `app/(sistema)/layout.tsx` | Reescreva como abaixo |
| `app/components/Header.tsx` | Troque o `sair()` como abaixo |
| `app/(sistema)/usuarios/page.tsx`, `app/(sistema)/usuarios/[id]/editar/page.tsx`, `app/(sistema)/livros/[id]/page.tsx` | Apague o import de `obterEmailLogado`. No lugar, `const usuarioLogado = useAppSelector((s) => s.auth.usuario);` e `const souAdmin = usuarioLogado?.perfil === "ADMINISTRADOR";`; onde estava `emailLogado`, use `usuarioLogado?.email`. Hoje eles descobrem o perfil procurando o e-mail na lista de usuários; agora o perfil já está na store |
| `app/cadastro/` | Apague a pasta inteira (decisão 3: sem registro público) e tire os links para `/cadastro` do `TelaLogin` e da landing (`app/page.tsx`) |
| `app/services/sessao.ts` | Apague o arquivo |
| terminal | `cd front` e `npm uninstall js-cookie @types/js-cookie` |

```tsx
// app/components/StoreProvider.tsx
"use client";
import { Provider } from "react-redux";
import { store } from "../store/store";
import ExpiracaoDoToken from "./ExpiracaoDoToken";

export default function StoreProvider({ children }: { children: React.ReactNode }) {
  // Sem restauração de cookie: a store nasce vazia, então recarregar a página desloga.
  // É a consequência direta de "token só na store" (PDF).
  return (
    <Provider store={store}>
      <ExpiracaoDoToken /> {/* precisa estar DENTRO do Provider para usar os hooks */}
      {children}
    </Provider>
  );
}
```

```tsx
// app/components/TelaLogin.tsx: dentro do componente, o novo entrar()
// O service e a ação do Redux têm o mesmo nome (login): renomeie um deles no import.
import { login as loginNoBack } from "@/app/services/authService";
import { login as sessaoIniciada } from "../store/authSlice";

const pathname = usePathname(); // de next/navigation

async function entrar(e: React.FormEvent) {
  e.preventDefault();
  setErro(""); setCarregando(true);
  try {
    const r = await loginNoBack(email, senha);
    // Tudo na store: token, vencimento e usuário. Nada de cookie.
    dispatch(sessaoIniciada({ token: r.token, expiraEm: r.expiraEm, usuario: r.usuario }));
    // Se o login foi mostrado pelo layout do painel (token vencido), a URL já é a da tela
    // certa e o layout se redesenha sozinho; só navegue quando estiver em /login.
    if (pathname === "/login") router.push("/home");
  } catch (err) {
    setErro(err instanceof Error ? err.message : "Falha ao entrar");
  } finally {
    setCarregando(false);
  }
}
```

```tsx
// app/(sistema)/layout.tsx
"use client";
import { useState } from "react";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import Footer from "../components/Footer";
import TelaLogin from "../components/TelaLogin";
import { useAppSelector } from "../store/hooks";

export default function SistemaLayout({ children }: { children: React.ReactNode }) {
  const token = useAppSelector((s) => s.auth.token);
  const [aberta, setAberta] = useState(true); // todos os hooks ANTES do return condicional (regra do React)

  // Sem token na store, o login aparece NO LUGAR do painel (texto do PDF: renderizar
  // condicionalmente a tela de login). Isso também elimina o useEffect com setState
  // que causava o erro do ESLint neste arquivo.
  if (!token) return <TelaLogin />;

  return (
    <div className="flex min-h-screen flex-col">
      <Header onToggle={() => setAberta(!aberta)} />
      <div className="flex flex-1">
        <Sidebar aberta={aberta} />
        <main className="flex-1 p-6">{children}</main>
      </div>
      <Footer />
    </div>
  );
}
```

```tsx
// app/components/Header.tsx: troque o sair() e a leitura do e-mail
const usuario = useAppSelector((e) => e.auth.usuario);
const email = usuario?.email ?? "";

async function sair() {
  await logoutNoServidor(); // apaga a linha da tabela token (erro ignorado)
  dispatch(logout());       // a store esvazia e o layout mostra o login; sem router.push
}
```

No `Header`, remova também `useRouter`, `limparSessao` e `sessaoEncerrada`.

**Pronto quando:** `npm run lint` e `npm run build` passam sem nenhum `js-cookie`; ao logar, o DevTools mostra Cookies vazios e `localStorage` sem token; recarregar a página volta ao login; com `JWT_EXPIRATION_MINUTES=1` o login aparece sozinho; apagar a linha da tabela `token` e recarregar a lista faz o 401 virar login.

### F2. Segundo slice: favoritos de livros

Por quê: o PDF pede Redux para a autenticação e para mais uma funcionalidade do projeto, e o professor sugeriu favoritos. Na apresentação você mostra a estrela funcionando e explica store, slice e reducers com ela.

| Arquivo | O que fazer |
| --- | --- |
| `app/store/favoritosSlice.ts` (novo) | Código abaixo |
| `app/store/store.ts` | Importe `favoritosReducer` e use `reducer: { auth: authReducer, favoritos: favoritosReducer }` |
| `app/(sistema)/livros/page.tsx` | Mudanças abaixo: leitura da store, filtro "só favoritos", estrela em cada card e botão de limpar |

```ts
// app/store/favoritosSlice.ts
import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
import { logout } from "./authSlice";

interface FavoritosState { ids: number[] }
const initialState: FavoritosState = { ids: [] };

const favoritosSlice = createSlice({
  name: "favoritos",
  initialState,
  reducers: {
    adicionarFavorito(state, action: PayloadAction<number>) {
      // O Redux Toolkit usa o Immer: aqui "mutar" o state é seguro, ele gera um estado novo por baixo.
      if (!state.ids.includes(action.payload)) state.ids.push(action.payload);
    },
    removerFavorito(state, action: PayloadAction<number>) {
      state.ids = state.ids.filter((id) => id !== action.payload);
    },
    limparFavoritos: () => initialState,
  },
  // Ao sair, os favoritos também somem. A store é uma só no navegador: sem isto, o usuário B
  // veria as estrelas do A depois de um logout e login.
  extraReducers: (builder) => {
    builder.addCase(logout, () => initialState);
  },
});

export const { adicionarFavorito, removerFavorito, limparFavoritos } = favoritosSlice.actions;
export default favoritosSlice.reducer;
```

```tsx
// app/(sistema)/livros/page.tsx: o que muda
const dispatch = useAppDispatch();
const favoritos = useAppSelector((s) => s.favoritos.ids);
const [soFavoritos, setSoFavoritos] = useState(false);

// 1) no filtro que já existe, como primeira linha dentro do filter:
if (soFavoritos && !favoritos.includes(l.id)) return false;

// 2) acima da grade, junto da busca:
<label className="mb-4 mr-4 inline-flex items-center gap-2">
  <input type="checkbox" checked={soFavoritos} onChange={(e) => setSoFavoritos(e.target.checked)} />
  Só favoritos ({favoritos.length})
</label>
<button type="button" onClick={() => dispatch(limparFavoritos())} className="rounded border border-ouro px-3 py-1">
  Limpar favoritos
</button>

// 3) no .map, troque o <Link> solto por um wrapper com a estrela AO LADO do Link.
//    Um <button> dentro de um <a> é HTML inválido (elemento interativo dentro de outro).
{filtrados.map((l) => {
  const favorito = favoritos.includes(l.id);
  return (
    <div key={l.id} className="relative">
      <Link href={`/livros/${l.id}`} className="...as classes que o Link já tem...">
        {/* conteúdo atual do card */}
      </Link>
      <button
        type="button"
        aria-label={favorito ? "Remover dos favoritos" : "Adicionar aos favoritos"}
        onClick={() => dispatch(favorito ? removerFavorito(l.id) : adicionarFavorito(l.id))}
        className="absolute right-2 top-2 z-10 text-3xl neon-ouro"
      >
        {favorito ? "★" : "☆"}
      </button>
    </div>
  );
})}
```

Importe `useAppDispatch` e `useAppSelector` de `../../store/hooks` e as três ações de `../../store/favoritosSlice`. Passe a `key` para o `div` (e tire do `Link`). Os favoritos não sobrevivem a um recarregamento, igual ao token; isso é consistente e evita `redux-persist`.

**Pronto quando:** você favorita dois livros, marca "Só favoritos" e vê só eles, limpa e a lista zera, sai e entra de novo e as estrelas voltam vazias.

### F3. Multilayout e telas só de administrador

Por quê: o PDF pede que a tela de login e o painel sejam renderizados por layouts diferentes (no seu código só existe o raiz e o `(sistema)`). Na explicação oral a hierarquia é: `app/layout.tsx` (fontes e `StoreProvider`) contém `(publico)/layout.tsx` (sem menu) ou `(sistema)/layout.tsx` (cabeçalho, menu e rodapé, e login no lugar quando não há token).

```bash
# Pasta com parênteses é um "grupo de rotas" do Next: organiza layouts sem entrar na URL.
# /login e / continuam com o mesmo endereço. Use as aspas, por causa dos parênteses.
cd front/app
mkdir "(publico)"
git mv login "(publico)/login"
git mv page.tsx "(publico)/page.tsx"
```

| Arquivo | O que fazer |
| --- | --- |
| `app/(publico)/layout.tsx` (novo) | `export default function PublicoLayout({ children }: { children: React.ReactNode }) { return <div className="min-h-screen">{children}</div>; }`; sem Header nem Sidebar |
| `app/(publico)/page.tsx` (a landing, movida) | Os imports relativos quebram com a mudança de pasta: troque `./components/...` por `@/app/components/...` (o alias que o projeto já usa) |
| `app/components/SomenteAdmin.tsx` (novo) | Código abaixo |
| `app/(sistema)/usuarios/layout.tsx` (novo) | `export default function UsuariosLayout({ children }: { children: React.ReactNode }) { return <SomenteAdmin>{children}</SomenteAdmin>; }` |
| `app/(sistema)/bibliotecas/layout.tsx` (novo) | O mesmo, com o nome `BibliotecasLayout` (a pasta `bibliotecas` nasce no F4) |
| `app/components/Sidebar.tsx` | Troque a lista e o `map` como abaixo |

```tsx
// app/components/SomenteAdmin.tsx
"use client";
import { useAppSelector } from "../store/hooks";

// Bloqueia a ROTA, não só o item de menu: quem digita /usuarios na barra de endereço também é barrado.
// Atenção: isto é conforto de interface. Quem protege de verdade é o back (403 no SecurityConfiguration).
export default function SomenteAdmin({ children }: { children: React.ReactNode }) {
  const perfil = useAppSelector((s) => s.auth.usuario?.perfil);
  if (perfil !== "ADMINISTRADOR") {
    return <p className="mx-auto max-w-xl p-6 text-lg">Acesso restrito a administradores.</p>;
  }
  return <>{children}</>;
}
```

```tsx
// app/components/Sidebar.tsx
const itens = [
  { href: "/home", icone: "🏛", nome: "Início" },
  { href: "/usuarios", icone: "👥", nome: "Usuários", soAdmin: true },
  { href: "/livros", icone: "📖", nome: "Livros" },
  { href: "/autores", icone: "🖋", nome: "Autores" },
  { href: "/emprestimos", icone: "📜", nome: "Empréstimos" },
  { href: "/bibliotecas", icone: "🏰", nome: "Bibliotecas", soAdmin: true },
];

// dentro do componente:
const perfil = useAppSelector((s) => s.auth.usuario?.perfil);
const visiveis = itens.filter((i) => !i.soAdmin || perfil === "ADMINISTRADOR");
// ...e no JSX, troque itens.map por visiveis.map
```

Custo a considerar: com `Usuários` só para administrador, o bibliotecário deixa de cadastrar leitores pela tela, embora o back (`POST /usuarios`) ainda permita. Se o seu fluxo de empréstimo precisa disso, use `perfil !== "LEITOR"` no lugar de `=== "ADMINISTRADOR"` para essa tela e mantenha `Bibliotecas` só para admin.

**Pronto quando:** `/` e `/login` abrem sem menu; após logar, o painel mostra cabeçalho e menu; logado como leitor, o menu não mostra `Usuários` nem `Bibliotecas` e digitar `/usuarios` mostra "Acesso restrito"; logado como admin, tudo aparece.

### F4. Telas de bibliotecas, com CEP

Por quê: o Teste 5 acontece pela tela, e o Teste 3 precisa de duas bibliotecas criadas pelo admin. Siga o molde das telas de autores (`autores/page.tsx`, `autores/novo/page.tsx`, `autores/components/AutorForm.tsx`), que já têm o estilo do projeto.

| Arquivo | O que fazer |
| --- | --- |
| `app/types/biblioteca.ts` (novo) | `interface Biblioteca { id: number; nome: string; cnpj: string; cep?: string; logradouro?: string; bairro?: string; cidade?: string; uf?: string }` e `interface EnderecoCep { cep: string; logradouro: string; bairro: string; cidade: string; uf: string }` |
| `app/services/bibliotecaService.ts` (novo) | `listarBibliotecas()` com `GET /bibliotecas`, `criarBiblioteca(corpo)` com `POST /bibliotecas` e `consultarCep(cep)` com `GET /bibliotecas/cep/${cep}`, no mesmo formato do `livroService.ts` (use sempre o `api`, nunca `axios` direto) |
| `app/(sistema)/bibliotecas/page.tsx` (novo) | Lista com nome, CNPJ e cidade/UF; botão "Nova biblioteca" para `/bibliotecas/nova` |
| `app/(sistema)/bibliotecas/nova/page.tsx` (novo) | Formulário com nome, CNPJ e CEP, mais a prévia do endereço (código abaixo) |
| `app/(sistema)/bibliotecas/layout.tsx` | Já criado no F3 (só admin) |

```tsx
// app/(sistema)/bibliotecas/nova/page.tsx: o trecho do CEP
const [cep, setCep] = useState("");
const [endereco, setEndereco] = useState<EnderecoCep | null>(null);
const [erroCep, setErroCep] = useState("");

async function buscarEndereco() {
  const limpo = cep.replace(/\D/g, ""); // aceita "88801-000" e "88801000"
  setEndereco(null); setErroCep("");
  if (limpo.length !== 8) { setErroCep("O CEP deve ter 8 dígitos"); return; }
  try {
    setEndereco(await consultarCep(limpo));
  } catch (err) {
    // 404, 400 e 503 chegam aqui como Error(mensagem): o interceptor já extrai o detail do back
    setErroCep(err instanceof Error ? err.message : "Não foi possível consultar o CEP");
  }
}

// no JSX
<input value={cep} onChange={(e) => setCep(e.target.value)} onBlur={buscarEndereco} inputMode="numeric" />
{erroCep && <p className="text-red-400">{erroCep}</p>}
{endereco && <p>{endereco.logradouro}, {endereco.bairro}, {endereco.cidade}/{endereco.uf}</p>}

// ao salvar: criarBiblioteca({ nome, cnpj, cep: cep.replace(/\D/g, "") });
// o back grava o endereço a partir do próprio ViaCEP, não do que a tela mostrou.
```

O endereço da prévia é só leitura: o back consulta o ViaCEP de novo ao salvar, então o cliente não consegue gravar um endereço inventado.

**Pronto quando:** o admin cria as bibliotecas A e B digitando só o CEP e vê o endereço aparecer; um CEP inexistente mostra o erro do back em vermelho.

### F5. Limpeza e checagem final

| Item | O que fazer |
| --- | --- |
| Cadastro de usuário pelo admin | Em `usuarios/novo`, com admin global acrescente um seletor de biblioteca (alimentado por `listarBibliotecas()`) para criar o usuário de A e o de B pela tela; para os demais perfis, não mostre o seletor |
| Mensagens de erro | Em qualquer chamada nova use `try/catch` e mostre `err.message`; o interceptor já converte o `detail` do back em `Error` |
| Referências mortas | `npx tsc --noEmit` e `npm run lint` sem erros; procure por `sessao`, `Cookies` e `obterEmailLogado` com `grep -rn` em `front/app` e não deve restar nada |
| `front/README.md` | Troque o texto padrão do create-next-app pelos comandos `npm install`, `npm run dev` e a variável `NEXT_PUBLIC_API_URL` |
| Arquivos soltos na raiz | `arquivos.txt`, `arvore_front.txt` (vazio) e `links_front.txt` (26 KB) parecem notas de trabalho; decida se saem do repositório antes da apresentação, para não distrair quem abre o projeto |

**Pronto quando:** `npm run build` passa, e no navegador o roteiro dos Testes 3, 4 e 5 funciona de ponta a ponta com o banco recém-criado.
