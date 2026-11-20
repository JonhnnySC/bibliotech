mkdir -p docs && cat > docs/codigo-modificado.md << 'PARTE1_FIM'
# BiblioTech — Código modificado e adicionado

Compêndio do código das Fases 1 a 6 + remoção do Flyway.
Estado: ESPECIFICADO (não compilado nem executado). Aplicar na ordem, um commit por passo.
Marcacao: ⚠️ = depende de arquivo nao lido; ajuste ao que a IDE mostrar.

Caminhos do back: src/main/java/br/com/senac/bibliotech/ (abreviado .../)
Caminhos do front: front/app/
Recursos: src/main/resources/

---

# 0. PRE-REQUISITO — Flyway removido e auditoria das entidades

Fazer ANTES das fases: sem isso o CRUD nao roda em banco vazio.

## 0.1 pom.xml — remover as duas dependencias

    <!-- APAGAR -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-flyway</artifactId>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-database-postgresql</artifactId>
    </dependency>

## 0.2 Apagar as migrations

    git rm -r src/main/resources/db/migration

## 0.3 application.properties

Trocar:

    spring.jpa.hibernate.ddl-auto=validate

Por:

    spring.jpa.hibernate.ddl-auto=update

NUNCA usar create ou create-drop: apagariam o administrador no restart do Teste 4.

## 0.4 ⚠️ Token — comprimento da coluna (B1, critico)

O JWT tem 300-500 caracteres. Sem length, o Hibernate cria varchar(255) e o LOGIN estoura.

    @Column(nullable = false, length = 1024)
    private String token;

## 0.5 ⚠️ Livro — unicidade por biblioteca (B4) + dono

    @Entity
    @Table(name = "livro",
           uniqueConstraints = @UniqueConstraint(name = "uk_livro_biblioteca_isbn",
                                                 columnNames = {"biblioteca_id", "isbn"}),
           indexes = @Index(name = "idx_livro_biblioteca", columnList = "biblioteca_id"))
    public class Livro {

        @Column(nullable = false, length = 20)   // SEM unique = true
        private String isbn;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "biblioteca_id", nullable = false, updatable = false)
        private Biblioteca biblioteca;
    }

Imports: jakarta.persistence.UniqueConstraint, jakarta.persistence.Index.

## 0.6 ⚠️ Usuario / CPF / Biblioteca — UNIQUE que so existia no SQL (B5)

    // Usuario
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "biblioteca_id", nullable = false, updatable = false)
    private Biblioteca biblioteca;   // apagar o comentario "Nulo = acesso a todas"

    // CPF (@Embeddable)
    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    private String cpf;

    // Biblioteca
    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

## 0.7 ⚠️ Biblioteca — campos de endereco (a V10 nao existe mais)

    @Column(length = 8)   private String cep;
    @Column(length = 150) private String logradouro;
    @Column(length = 20)  private String numero;
    @Column(length = 100) private String complemento;
    @Column(length = 100) private String bairro;
    @Column(length = 100) private String cidade;
    @Column(length = 2)   private String uf;

## 0.8 Autor, Exemplar, Emprestimo — o dono (Fase 4, ja anotar agora)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "biblioteca_id", nullable = false, updatable = false)
    private Biblioteca biblioteca;

## 0.9 ⚠️ Editora orfa (B2) — escolher UMA opcao

Problema: Editora nao tem controller e o seed sumiu. Se Livro.editora for
obrigatoria, POST /livros falha no banco vazio.

Opcao A (recomendada, se LivroRequest manda o NOME da editora):

    // EditoraRepository
    Optional<Editora> findByNomeIgnoreCase(String nome);

    // LivroService
    private Editora resolverEditora(String nome) {
        return editoraRepository.findByNomeIgnoreCase(nome.trim())
                .orElseGet(() -> {
                    Editora e = new Editora();
                    e.setNome(nome.trim());
                    return editoraRepository.save(e);
                });
    }

Opcao B (mais rapida): tornar opcional em Livro

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editora_id")
    private Editora editora;

Opcao C (se LivroRequest manda editoraId): criar EditoraController com POST/GET.
Editora e catalogo compartilhado, sem biblioteca_id — justificar isso.

## 0.10 ⚠️ Dois checks rapidos

Regex do e-mail em Usuario.setEmail — o certo em Java e:

    if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
        throw new IllegalArgumentException("Email Invalido!");
    }

Se estiver [^@s] (sem as barras), rejeita maria@senac.com.

POST /usuarios com perfil LEITOR precisa instanciar Leitor (heranca JOINED),
senao nao existe linha em leitor e o emprestimo falha.

## 0.11 Regra de operacao

update NAO altera tipo, tamanho nem constraint antiga.
Mudou isso -> docker compose down -v && docker compose up -d.

---

# 1. FASE 1 — base do back

## 1.0 Renomear o principal (Shift+F6 no IntelliJ)

O record UsuarioLogadoResponse atual e o PRINCIPAL de seguranca (tem CPF e
GrantedAuthority). Renomeie a classe para UsuarioLogado. A IDE atualiza
TokenService e demais referencias (o JwtFilter usa var, nao sente).

## 1.1 NOVO .../application/dto/UsuarioLogadoResponse.java

    package br.com.senac.bibliotech.application.dto;

    import br.com.senac.bibliotech.domain.entities.Usuario;
    import br.com.senac.bibliotech.domain.enums.EnumPerfil;

    /*
     * Dados PUBLICOS de quem esta logado: e o que vai no corpo do login e o que
     * a store do Redux guarda. Ficam fora CPF, senha e authorities.
     */
    public record UsuarioLogadoResponse(Long id, String nome, String email,
                                        EnumPerfil perfil, Long bibliotecaId) {

        public static UsuarioLogadoResponse from(Usuario u) {
            Long bibliotecaId = u.getBiblioteca() == null ? null : u.getBiblioteca().getId();
            return new UsuarioLogadoResponse(u.getId(), u.getNome(), u.getEmail(),
                                             u.getPerfil(), bibliotecaId);
        }
    }

## 1.2 SUBSTITUIR .../application/dto/LoginResponse.java

    package br.com.senac.bibliotech.application.dto;

    import java.time.Instant;

    /*
     * Resposta do login.
     * "tipo" = "Bearer" diz ao cliente como enviar o token:
     *     Authorization: Bearer <token>
     * "expiraEm" deixa o front saber quando pedir novo login, sem decodificar o JWT.
     * "usuario" sao os dados publicos que a store do Redux vai guardar.
     */
    public record LoginResponse(
            String token,
            String tipo,
            Instant expiraEm,
            UsuarioLogadoResponse usuario
    ) {
    }

## 1.3 MODIFICAR .../application/service/AuthService.java

Import novo:

    import br.com.senac.bibliotech.application.dto.UsuarioLogadoResponse;

Ultima linha do login() passa a ser:

    TokenService.TokenGerado token = tokenService.gerar(usuario);
    return new LoginResponse(token.valor(), "Bearer", token.expiraEm(),
                             UsuarioLogadoResponse.from(usuario));

Metodo novo (qualquer lugar abaixo do login):

    public void logout(String token) {
        tokenService.revogar(token);
    }

NAO por @Transactional no login (BCrypt ~100ms seguraria conexao do pool).
MANTER o hashFalso: e a resposta pronta sobre timing attack.

## 1.4 TokenService.revogar — JA EXISTE, nao mexer

    @Transactional
    public void revogar(String token) {
        tokenRepository.findTokenByToken(token).ifPresent(tokenRepository::delete);
    }

## 1.5 MODIFICAR .../presentation/AuthController.java

Imports novos:

    import org.springframework.web.bind.annotation.RequestHeader;
    import org.springframework.web.bind.annotation.ResponseStatus;

Endpoint:

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader("Authorization") String cabecalho) {
        authService.logout(cabecalho.substring("Bearer ".length())); // COM espaco = 7
    }

O guia tem um bug aqui: substring("Bearer".length()) sem espaco deixaria um
espaco antes do token e o findTokenByToken nunca acharia a linha.

NAO por /auth/logout no permitAll nem no shouldNotFilter.

## 1.6 MODIFICAR src/main/resources/application.properties

Trocar:

    api.security.token.expiration-minutes=60
    logging.level.org.springframework.security=DEBUG

Por:

    api.security.token.expiration-minutes=${JWT_EXPIRATION_MINUTES:60}
    logging.level.org.springframework.security=INFO

O construtor do TokenService ja le essa property com default 60.

## 1.7 NOVO .../infrastructure/config/LimpaTokensNaSubida.java

    package br.com.senac.bibliotech.infrastructure.config;

    import br.com.senac.bibliotech.domain.repository.TokenRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.boot.ApplicationArguments;
    import org.springframework.boot.ApplicationRunner;
    import org.springframework.stereotype.Component;
    import org.springframework.transaction.annotation.Transactional;

    /*
     * O PDF pede token "em memoria": reiniciar o back desloga todo mundo.
     * Como o token mora na tabela token (pra permitir revogacao no logout),
     * apagamos a tabela na subida — o efeito e o de token so em memoria.
     */
    @Component
    @RequiredArgsConstructor
    class LimpaTokensNaSubida implements ApplicationRunner {

        private final TokenRepository tokenRepository;

        @Override
        @Transactional
        public void run(ApplicationArguments args) {
            tokenRepository.deleteAllInBatch();
        }
    }

AVISO: com spring-boot-devtools no pom, cada hot-restart roda isso de novo.
Toda recompilacao desloga o Swagger. Nao e bug.

## 1.8 MODIFICAR .../presentation/LivroController.java

Import:

    import br.com.senac.bibliotech.domain.enums.EnumGeneroLiterario;

Metodo (List e GetMapping ja estao importados):

    @GetMapping("/generos")
    @Operation(summary = "Listar generos literarios", description = "Lista fixa do enum; exige token")
    public List<EnumGeneroLiterario> generos() {
        return List.of(EnumGeneroLiterario.values());
    }

EnumGeneroLiterario ja tem 60+ constantes. Sem conflito com /{id}: o Spring
prefere caminho literal.

## Checagem da Fase 1

    docker compose up -d
    ./mvnw verify

1. POST /auth/login -> 200 com token, expiraEm e usuario (sem CPF)
2. Authorize -> GET /livros/generos -> 200
3. POST /auth/logout -> 204; repetir o GET com o mesmo token -> 401
4. Login de novo, reiniciar o back, GET com token antigo -> 401
5. JWT_EXPIRATION_MINUTES=1 -> expiraEm = +1min; esperar e ver 401. Voltar pra 60
6. Sem token -> 401

---

# 2. FASE 2 — front com Redux

## 2.1 MODIFICAR front/app/types/auth.ts

    export interface UsuarioLogado {
      id: number;
      nome: string;
      email: string;
      perfil: string;
      bibliotecaId: number | null;
    }

    export interface LoginResponse {
      token: string;
      tipo: string;
      expiraEm: string;   // Instant do back vira string ISO
      usuario: UsuarioLogado;
    }

## 2.2 SUBSTITUIR front/app/store/authSlice.ts

    import { createSlice, type PayloadAction } from "@reduxjs/toolkit";

    export interface Usuario {
      id: number;
      nome: string;
      email: string;
      perfil: string;
      bibliotecaId: number | null;
    }

    export interface AuthState {
      token: string | null;
      expiraEm: string | null;
      usuario: Usuario | null;
    }

    const initialState: AuthState = { token: null, expiraEm: null, usuario: null };

    const authSlice = createSlice({
      name: "auth",
      initialState,
      reducers: {
        login: (_state, action: PayloadAction<AuthState>) => action.payload,
        logout: () => initialState,
      },
    });

    export const { login, logout } = authSlice.actions;
    export default authSlice.reducer;

## 2.3 MODIFICAR front/app/services/api.ts

MANTER o axios.create(...) e o bloco que le corpo.detail e monta new Error(mensagem).
APAGAR imports de js-cookie / ./sessao e os dois interceptors antigos.

    import type { store } from "../store/store";
    import { logout } from "../store/authSlice";

    let storeRef: typeof store;
    export function configurarApi(s: typeof store) {
      storeRef = s;
    }

    api.interceptors.request.use((config) => {
      const token = storeRef.getState().auth.token;
      if (token) config.headers.Authorization = `Bearer ${token}`;
      return config;
    });

    api.interceptors.response.use(
      (resposta) => resposta,
      (erro) => {
        // So desloga se HAVIA sessao: o 401 de senha errada no login nao derruba nada.
        // 403 (falta de perfil) NAO desloga.
        if (erro.response?.status === 401 && storeRef.getState().auth.token) {
          storeRef.dispatch(logout());
        }
        // MANTER AQUI o bloco existente que monta new Error(mensagem) do ProblemDetail
        return Promise.reject(erro);
      }
    );

A store e injetada por funcao para evitar import circular
(store -> slice -> api -> store). import type some na compilacao.

## 2.4 SUBSTITUIR front/app/store/store.ts

    import { configureStore } from "@reduxjs/toolkit";
    import authReducer from "./authSlice";
    import favoritosReducer from "./favoritosSlice"; // entra no passo 2.10
    import { configurarApi } from "../services/api";

    export const store = configureStore({
      reducer: { auth: authReducer, favoritos: favoritosReducer },
    });

    configurarApi(store);

    export type RootState = ReturnType<typeof store.getState>;
    export type AppDispatch = typeof store.dispatch;

## 2.5 front/app/store/hooks.ts (criar se nao existir)

    import { useDispatch, useSelector } from "react-redux";
    import type { AppDispatch, RootState } from "./store";

    export const useAppDispatch = useDispatch.withTypes<AppDispatch>();
    export const useAppSelector = useSelector.withTypes<RootState>();

## 2.6 MODIFICAR front/app/services/authService.ts

    export async function logoutNoServidor(): Promise<void> {
      try {
        await api.post("/auth/logout");
      } catch {
        /* token ja invalido: tudo bem */
      }
    }

## 2.7 NOVO front/app/components/ExpiracaoDoToken.tsx

    "use client";
    import { useEffect } from "react";
    import { useAppDispatch, useAppSelector } from "../store/hooks";
    import { logout } from "../store/authSlice";

    export default function ExpiracaoDoToken() {
      const dispatch = useAppDispatch();
      const expiraEm = useAppSelector((s) => s.auth.expiraEm);

      useEffect(() => {
        if (!expiraEm) return;
        const venceEm = new Date(expiraEm).getTime();
        const verificar = () => {
          if (Date.now() >= venceEm) dispatch(logout());
        };
        const timer = setTimeout(verificar, Math.max(venceEm - Date.now(), 0));
        // aba em segundo plano tem timer atrasado: ao voltar, confere de novo
        document.addEventListener("visibilitychange", verificar);
        return () => {
          clearTimeout(timer);
          document.removeEventListener("visibilitychange", verificar);
        };
      }, [expiraEm, dispatch]);

      return null;
    }

## 2.8 SUBSTITUIR front/app/components/StoreProvider.tsx

    "use client";
    import { Provider } from "react-redux";
    import { store } from "../store/store";
    import ExpiracaoDoToken from "./ExpiracaoDoToken";

    export default function StoreProvider({ children }: { children: React.ReactNode }) {
      // sem restauracao de cookie: recarregar a pagina desloga (decisao fechada)
      return (
        <Provider store={store}>
          <ExpiracaoDoToken />
          {children}
        </Provider>
      );
    }

## 2.9 NOVO front/app/components/TelaLogin.tsx

⚠️ Substituir o JSX do return pelo formulario que hoje esta em app/login/page.tsx,
com as classes do projeto, sem links para /cadastro.

    "use client";
    import { useState } from "react";
    import { useRouter, usePathname } from "next/navigation";
    import { useAppDispatch } from "../store/hooks";
    import { login as loginNoBack } from "@/app/services/authService";
    import { login as sessaoIniciada } from "../store/authSlice";

    export default function TelaLogin() {
      const [email, setEmail] = useState("");
      const [senha, setSenha] = useState("");
      const [erro, setErro] = useState("");
      const [carregando, setCarregando] = useState(false);
      const dispatch = useAppDispatch();
      const router = useRouter();
      const pathname = usePathname();

      async function entrar(e: React.FormEvent) {
        e.preventDefault();
        setErro("");
        setCarregando(true);
        try {
          const r = await loginNoBack(email, senha);
          dispatch(sessaoIniciada({ token: r.token, expiraEm: r.expiraEm, usuario: r.usuario }));
          if (pathname === "/login") router.push("/home");
        } catch (err) {
          setErro(err instanceof Error ? err.message : "Falha ao entrar");
        } finally {
          setCarregando(false);
        }
      }

      return (
        <form onSubmit={entrar} className="mx-auto max-w-sm space-y-3 p-6">
          <input value={email} onChange={(e) => setEmail(e.target.value)}
                 type="email" placeholder="E-mail" required />
          <input value={senha} onChange={(e) => setSenha(e.target.value)}
                 type="password" placeholder="Senha" required />
          <button type="submit" disabled={carregando}>
            {carregando ? "Entrando..." : "Entrar"}
          </button>
          {erro && <p className="text-red-600">{erro}</p>}
        </form>
      );
    }

E app/login/page.tsx vira:

    import TelaLogin from "../components/TelaLogin";
    export default function Page() {
      return <TelaLogin />;
    }

## 2.10 SUBSTITUIR front/app/(sistema)/layout.tsx

⚠️ Ajustar as props de Header/Sidebar se forem diferentes.

    "use client";
    import { useState } from "react";
    import Header from "../components/Header";
    import Sidebar from "../components/Sidebar";
    import Footer from "../components/Footer";
    import TelaLogin from "../components/TelaLogin";
    import { useAppSelector } from "../store/hooks";

    export default function SistemaLayout({ children }: { children: React.ReactNode }) {
      const token = useAppSelector((s) => s.auth.token);
      const [aberta, setAberta] = useState(true); // hooks ANTES do return condicional

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

## 2.11 MODIFICAR front/app/components/Header.tsx

Remover useRouter, limparSessao e sessaoEncerrada.

    const usuario = useAppSelector((e) => e.auth.usuario);
    const email = usuario?.email ?? "";

    async function sair() {
      await logoutNoServidor(); // apaga a linha na tabela token (erro ignorado)
      dispatch(logout());       // a store esvazia e o layout mostra o login
    }

Imports: logoutNoServidor do authService, logout do authSlice,
useAppSelector/useAppDispatch dos hooks.

## 2.12 NOVO front/app/store/favoritosSlice.ts (segunda funcionalidade Redux)

    import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
    import { logout } from "./authSlice";

    interface FavoritosState { ids: number[] }
    const initialState: FavoritosState = { ids: [] };

    const favoritosSlice = createSlice({
      name: "favoritos",
      initialState,
      reducers: {
        adicionarFavorito(state, action: PayloadAction<number>) {
          // Immer: "mutar" o state aqui e seguro, ele gera um estado novo por baixo
          if (!state.ids.includes(action.payload)) state.ids.push(action.payload);
        },
        removerFavorito(state, action: PayloadAction<number>) {
          state.ids = state.ids.filter((id) => id !== action.payload);
        },
        limparFavoritos: () => initialState,
      },
      // ao sair, os favoritos somem: um usuario nao herda os do anterior
      extraReducers: (builder) => {
        builder.addCase(logout, () => initialState);
      },
    });

    export const { adicionarFavorito, removerFavorito, limparFavoritos } = favoritosSlice.actions;
    export default favoritosSlice.reducer;

## 2.13 MODIFICAR front/app/(sistema)/livros/page.tsx

⚠️ Tres insercoes, aproveitando o filter e o map que ja existem.

    const dispatch = useAppDispatch();
    const favoritos = useAppSelector((s) => s.favoritos.ids);
    const [soFavoritos, setSoFavoritos] = useState(false);

    // 1) primeira linha do filter que ja existe:
    if (soFavoritos && !favoritos.includes(l.id)) return false;

    // 2) acima da grade, junto da busca:
    <label className="mb-4 mr-4 inline-flex items-center gap-2">
      <input type="checkbox" checked={soFavoritos}
             onChange={(e) => setSoFavoritos(e.target.checked)} />
      Só favoritos ({favoritos.length})
    </label>
    <button type="button" onClick={() => dispatch(limparFavoritos())}>Limpar favoritos</button>

    // 3) no .map — a estrela fica AO LADO do Link (botao dentro de <a> e HTML invalido)
    {filtrados.map((l) => {
      const favorito = favoritos.includes(l.id);
      return (
        <div key={l.id} className="relative">
          <Link href={`/livros/${l.id}`} className="...classes que o Link ja tem...">
            {/* card atual */}
          </Link>
          <button type="button"
                  aria-label={favorito ? "Remover dos favoritos" : "Adicionar aos favoritos"}
                  onClick={() => dispatch(favorito ? removerFavorito(l.id) : adicionarFavorito(l.id))}
                  className="absolute right-2 top-2 z-10 text-3xl">
            {favorito ? "★" : "☆"}
          </button>
        </div>
      );
    })}

## 2.14 Multilayout — grupos de rota

    cd front/app
    mkdir "(publico)"
    git mv login "(publico)/login"
    git mv page.tsx "(publico)/page.tsx"

Pasta com parenteses e grupo de rotas: organiza layouts sem entrar na URL.
Use aspas por causa dos parenteses.

NOVO front/app/(publico)/layout.tsx:

    export default function PublicoLayout({ children }: { children: React.ReactNode }) {
      return <div className="min-h-screen">{children}</div>;
    }

⚠️ Na landing movida, trocar imports ./components/... por @/app/components/...
⚠️ Em (publico)/login/page.tsx o import vira @/app/components/TelaLogin

## 2.15 NOVO front/app/components/SomentePerfis.tsx

    "use client";
    import { useAppSelector } from "../store/hooks";

    export default function SomentePerfis({ perfis, children }:
      { perfis: string[]; children: React.ReactNode }) {
      const perfil = useAppSelector((s) => s.auth.usuario?.perfil);
      if (!perfil || !perfis.includes(perfil)) {
        return <p className="mx-auto max-w-xl p-6 text-lg">Acesso restrito.</p>;
      }
      return <>{children}</>;
    }

NOVO front/app/(sistema)/usuarios/layout.tsx:

    import SomentePerfis from "@/app/components/SomentePerfis";
    export default function Layout({ children }: { children: React.ReactNode }) {
      return <SomentePerfis perfis={["ADMINISTRADOR", "BIBLIOTECARIO"]}>{children}</SomentePerfis>;
    }

NOVO front/app/(sistema)/minha-biblioteca/layout.tsx: o mesmo com perfis={["ADMINISTRADOR"]}

## 2.16 MODIFICAR front/app/components/Sidebar.tsx

    const itens = [
      { href: "/home", icone: "🏛", nome: "Início" },
      { href: "/usuarios", icone: "👥", nome: "Usuários", perfis: ["ADMINISTRADOR", "BIBLIOTECARIO"] },
      { href: "/livros", icone: "📖", nome: "Livros" },
      { href: "/autores", icone: "🖋", nome: "Autores" },
      { href: "/emprestimos", icone: "📜", nome: "Empréstimos" },
      { href: "/minha-biblioteca", icone: "🏰", nome: "Minha biblioteca", perfis: ["ADMINISTRADOR"] },
    ];
    const perfil = useAppSelector((s) => s.auth.usuario?.perfil);
    const visiveis = itens.filter((i) => !i.perfis || (perfil && i.perfis.includes(perfil)));
    // no JSX, trocar itens.map por visiveis.map

## 2.17 Limpeza do front

⚠️ Em usuarios/page.tsx, usuarios/[id]/editar/page.tsx e livros/[id]/page.tsx,
trocar obterEmailLogado() por leitura da store:

    const usuario = useAppSelector((s) => s.auth.usuario);
    const email = usuario?.email ?? "";
    const perfil = usuario?.perfil;

Depois apagar a funcao obterEmailLogado do service (codigo morto).

    cd front
    npm uninstall js-cookie @types/js-cookie
    git rm app/services/sessao.ts
    git rm -r app/cadastro

Tirar os links para /cadastro da landing e do TelaLogin.

## Checagem da Fase 2

    cd front && npm run lint && npm run build
    grep -rn "js-cookie\|sessao\|obterEmailLogado" app   # vazio

1. DevTools: Cookies e localStorage sem token; Redux DevTools mostra auth/login
2. F5 volta ao login
3. JWT_EXPIRATION_MINUTES=1 -> login aparece sozinho em ~1min
4. Reiniciar o back e recarregar a lista -> 401 -> login sozinho
5. Senha errada -> mostra erro e NAO desloga
6. Favoritar 2 livros, "So favoritos", limpar, sair e entrar -> estrelas vazias
7. / e /login sem menu; leitor em /usuarios ve "Acesso restrito"

---

# 3. FASE 3 — bootstrap e desktop

## 3.1 Chave do operador

    openssl rand -hex 32

.env (raiz, fora do Git):

    BOOTSTRAP_KEY=cole-a-saida-aqui

application.properties:

    app.bootstrap.chave=${BOOTSTRAP_KEY:}

## 3.2 NOVO .../application/exception/AcessoNegadoException.java

    package br.com.senac.bibliotech.application.exception;

    public class AcessoNegadoException extends RuntimeException {
        public AcessoNegadoException(String mensagem) { super(mensagem); }
    }

## 3.3 MODIFICAR .../presentation/ApiExceptionHandler.java

    @ExceptionHandler(AcessoNegadoException.class)
    public ProblemDetail acessoNegado(AcessoNegadoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    }

## 3.4 NOVOS DTOs

.../application/dto/BootstrapRequest.java:

    package br.com.senac.bibliotech.application.dto;

    import jakarta.validation.constraints.*;

    public record BootstrapRequest(
            @NotBlank @Size(max = 150) String empresaNome,
            @NotBlank @Pattern(regexp = "\\d{14}", message = "CNPJ deve ter 14 digitos") String empresaCnpj,
            @NotBlank @Size(max = 150) String adminNome,
            @NotBlank @Email String adminEmail,
            @NotBlank @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 digitos") String adminCpf,
            @NotBlank @Size(min = 8, max = 72) String adminSenha) {}

.../application/dto/BootstrapResponse.java:

    package br.com.senac.bibliotech.application.dto;

    public record BootstrapResponse(Long bibliotecaId, String bibliotecaNome,
                                    Long usuarioId, String email) {}

## 3.5 MODIFICAR repositorios

    // BibliotecaRepository
    boolean existsByCnpj(String cnpj);

    // UsuarioRepository — ⚠️ conferir se ja existem
    boolean existsByEmail(String email);
    boolean existsByCpf_Cpf(String cpf);

⚠️ O "_" navega no CPF embutido. Se o campo dentro de CPF nao se chama cpf,
ajuste o nome do metodo (a IDE mostra).

## 3.6 NOVO .../application/service/BootstrapService.java

    package br.com.senac.bibliotech.application.service;

    import br.com.senac.bibliotech.application.dto.BootstrapRequest;
    import br.com.senac.bibliotech.application.dto.BootstrapResponse;
    import br.com.senac.bibliotech.application.exception.AcessoNegadoException;
    import br.com.senac.bibliotech.domain.entities.Biblioteca;
    import br.com.senac.bibliotech.domain.entities.Usuario;
    import br.com.senac.bibliotech.domain.enums.EnumPerfil;
    import br.com.senac.bibliotech.domain.enums.EnumStatusUsuario;
    import br.com.senac.bibliotech.domain.exception.ConflitoException;
    import br.com.senac.bibliotech.domain.repository.BibliotecaRepository;
    import br.com.senac.bibliotech.domain.repository.UsuarioRepository;
    import br.com.senac.bibliotech.domain.valueObject.CPF;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.nio.charset.StandardCharsets;
    import java.security.MessageDigest;
    import java.util.Locale;

    @Service
    @RequiredArgsConstructor
    public class BootstrapService {

        private final BibliotecaRepository bibliotecaRepository;
        private final UsuarioRepository usuarioRepository;
        private final PasswordEncoder passwordEncoder;

        @Value("${app.bootstrap.chave:}")
        private String chaveConfigurada;

        @Transactional
        public BootstrapResponse provisionar(String chaveInformada, BootstrapRequest r) {
            // a chave vem PRIMEIRO: quem nao a conhece recebe 403 sem descobrir nada
            if (!chaveConfere(chaveInformada)) {
                throw new AcessoNegadoException("Chave de bootstrap invalida");
            }
            CPF cpf = new CPF(r.adminCpf());
            String email = r.adminEmail().trim().toLowerCase(Locale.ROOT);

            if (usuarioRepository.existsByEmail(email))
                throw new ConflitoException("Ja existe um usuario com este e-mail");
            if (usuarioRepository.existsByCpf_Cpf(cpf.getCpf()))
                throw new ConflitoException("Ja existe um usuario com este CPF");
            if (bibliotecaRepository.existsByCnpj(r.empresaCnpj()))
                throw new ConflitoException("Ja existe uma biblioteca com este CNPJ");

            Biblioteca biblioteca = new Biblioteca(); // ⚠️ conferir setters/construtor vazio
            biblioteca.setNome(r.empresaNome().trim());
            biblioteca.setCnpj(r.empresaCnpj());
            bibliotecaRepository.save(biblioteca);

            Usuario admin = Usuario.builder()
                    .nome(r.adminNome().trim())
                    .email(email)
                    .cpf(cpf)
                    .senha(passwordEncoder.encode(r.adminSenha()))
                    .perfil(EnumPerfil.ADMINISTRADOR)
                    .status(EnumStatusUsuario.ATIVO)
                    .biblioteca(biblioteca)
                    .build();
            usuarioRepository.save(admin);

            return new BootstrapResponse(biblioteca.getId(), biblioteca.getNome(),
                                         admin.getId(), admin.getEmail());
        }

        // comparacao em tempo constante; chave nao configurada = bootstrap desligado
        private boolean chaveConfere(String informada) {
            if (chaveConfigurada.isBlank() || informada == null) return false;
            return MessageDigest.isEqual(
                    informada.getBytes(StandardCharsets.UTF_8),
                    chaveConfigurada.getBytes(StandardCharsets.UTF_8));
        }
    }

⚠️ Se CPF invalido lancar IllegalArgumentException e nao houver handler 400,
acrescente um no ApiExceptionHandler.

## 3.7 MODIFICAR .../presentation/AuthController.java

Injetar BootstrapService e acrescentar:

    @PostMapping("/bootstrap")
    @ResponseStatus(HttpStatus.CREATED)
    public BootstrapResponse bootstrap(
            @RequestHeader(value = "X-Bootstrap-Key", required = false) String chave,
            @Valid @RequestBody BootstrapRequest request) {
        return bootstrapService.provisionar(chave, request);
    }

Header opcional de proposito: sem ele a resposta e 403, nao 400.

## 3.8 MODIFICAR seguranca

SecurityConfiguration — na lista do permitAll(), acrescentar "/auth/bootstrap".

JwtFilter.shouldNotFilter — acrescentar ao lado do /auth/login:

    || uri.equals("/auth/bootstrap")

## 3.9 REMOVER o registro publico

- AuthController: apagar registrar(...) e o @PostMapping("/registro")
- SecurityConfiguration: tirar "/auth/registro" do permitAll()
- JwtFilter.shouldNotFilter: tirar a linha do /auth/registro

UsuarioService.cadastrar CONTINUA: serve ao POST /usuarios.

## 3.10 NOVO .env.example (raiz)

    DB_USER=
    DB_PASSWORD=
    DB_NAME=bibliotech
    JWT_SECRET=
    JWT_EXPIRATION_MINUTES=60
    ADMIN_PROMOTION_PASSWORD=
    BOOTSTRAP_KEY=

## 3.11 NOVO README.md (raiz)

    # BiblioTech

    ## Subir do zero
    1. cp .env.example .env e preencha
       JWT_SECRET com 32+ caracteres; BOOTSTRAP_KEY: openssl rand -hex 32
    2. docker compose up -d
    3. ./mvnw spring-boot:run   (JDK 25)
    4. cd front && npm install && npm run dev
    5. cd admin-desktop && ./mvnw clean javafx:run   (JDK 21+)
       Crie a biblioteca e o administrador
    6. Logue no front (http://localhost:3000) com o usuario criado

    Swagger: http://localhost:8080/swagger-ui.html
    Banco do zero: docker compose down -v && docker compose up -d (apaga os dados)

    O schema e gerado pelo Hibernate a partir das entidades (ddl-auto=update).
    O banco nasce vazio e o primeiro administrador e criado pelo app JavaFX
    via POST /auth/bootstrap.

## 3.12 Desktop JavaFX

    git mv back admin-desktop

1. Config.java: ficar so com apiUrl() (env BIBLIOTECH_API_URL, padrao
   http://localhost:8080). Apagar adminKey() e bootstrapKey().

2. NOVO Sessao.java:

   public final class Sessao {
   private static String chave = "";
   public static String chave() { return chave; }
   public static void setChave(String c) { chave = c == null ? "" : c; }
   }

3. Tela 1 (LoginController): trocar a comparacao local por
   Sessao.setChave(campo.getText()) e seguir para a tela 2.
   Quem valida e o back; checagem local e enfeite (string sai de um .class).

4. Tela 2: dois grupos — Empresa (nome, CNPJ) e Administrador (nome, e-mail,
   CPF, senha). ⚠️ Ajustar cadastro-usuario-view.fxml. CPF/CNPJ aceitam mascara;
   o app tira nao-digitos antes de enviar.

5. Chamada fora da thread da UI (Task, como no UsuarioController atual;
   o metodo aspas() ja existe la):

   String json = "{"
   + "\"empresaNome\":" + aspas(empresaNome) + ","
   + "\"empresaCnpj\":" + aspas(cnpj) + ","
   + "\"adminNome\":" + aspas(nome) + ","
   + "\"adminEmail\":" + aspas(email) + ","
   + "\"adminCpf\":" + aspas(cpf) + ","
   + "\"adminSenha\":" + aspas(senha) + "}";

   HttpRequest request = HttpRequest.newBuilder(URI.create(Config.apiUrl() + "/auth/bootstrap"))
   .timeout(Duration.ofSeconds(10))
   .header("Content-Type", "application/json; charset=UTF-8")
   .header("X-Bootstrap-Key", Sessao.chave())
   .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
   .build();

6. Respostas: 201 -> "Biblioteca X criada com o administrador Y" e LIMPA o
   formulario (pra criar a 2a empresa sem reabrir o app); 403 -> "Chave invalida";
   409/400 -> o detail da resposta; falha de conexao -> "Nao foi possivel falar
   com a API em ...".

7. Apagar MenuController e menu-view.fxml.

8. Rodar: cd admin-desktop && ./mvnw clean javafx:run

## Checagem da Fase 3

    docker compose down -v && docker compose up -d
    ./mvnw spring-boot:run

1. Front abre no login (banco sem usuarios)
2. Desktop cria biblioteca + admin -> 201
3. Login no front funciona
4. CNPJ ou e-mail repetido -> 409; chave errada -> 403
5. Rodar de novo com outros dados cria a SEGUNDA biblioteca (Teste 3 usa)

   docker compose exec postgres psql -U postgres -d bibliotech -c "SELECT count(*) FROM usuario;"

---

# PARTE 2 (a anexar): Fases 4, 5 e 6
PARTE1_FIMagonegro@JonhnnyPC:~/dev/projects/java/bibliotech$ git push origin main
remote: Invalid username or token. Password authentication is not supported for Git operations.
fatal: Authentication failed for 'https://github.com/JonhnnySC/bibliotech.git/'