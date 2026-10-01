ada tarefa (uma feature, um bugfix, uma configuração) ganha sua própria branch, 
geralmente com um prefixo tipo 

feature/, fix/, chore/, docs/, etc.

Então na prática o ciclo é: criar branch → trabalhar → commit → PR/merge → deletar branch → repetir para a próxima tarefa.

-------------------

PRECISO FAZER UM .ENV PRA MAQUINA DA FACULDADE.

------------------
PADRÃO REST É COLOCAR OS NOMES DOS NEGOCIOS EM PULURAL

-------------------SERVICES---------------------------------

A convenção para fazer é - um service por responsabiidade de negócio, e não por tabela nem por método.

O critério é o "motivo pra mudar"

No meu projeto é:

AutorService - regra do cadastro de autores.

UsuarioService - Regras do cadastro e dados do usuario

AuthService - o fluxo de login

TokenService - Formado e assinatura do teken

- A separação que mais importa é TokenService fora do AuthService. Os dois usam o token, mas por motivos diferentes:


    AuthController ──► AuthService ──► UsuarioRepository
    │
    └──► TokenService ◄── JwtFilter (próximo passo)

O JwtFilter precisa só validar tokens. Se a validação morasse no AuthService, o filtro dependeria de todo o fluxo de login, e isso cria dependência circular. Se você trocar de JWT para outro mecanismo, só um arquivo muda.

----------------------------
FUNCIONAMENTO JWT FILTER

    1. Requisição HTTP chega no Tomcat.
    ↓
    2. Tomcat chama: public doFilter() (Interface padrão do Java)
    ↓
    3. OncePerRequestFilter (Spring) intercepta:
    "Já executei isso? Não? Ok, deixa passar."
    ↓
    4. OncePerRequestFilter chama internamente: protected doFilterInternal()
    ↓
    5. JwtFilter (SEU CÓDIGO) executa a lógica de validar o Token JWT.

------------------------------------ RECORDS ---------------------------

Como o record não tem setters, o Jackson vai usar o construtor canônico. Isso significa que o JSON que o seu Frontend (Next.js/Axios) enviar precisa ter os nomes exatamente iguais aos parâmetros do record.
No seu Frontend, a chamada do Axios deve ser exatamente assim:

    Os nomes "leitorId" e "exemplarId" devem ser idênticos aos do record

    await axios.post("http://localhost:8080/emprestimos", {
    leitorId: 1,
    exemplarId: 5
    });

------------------------------------PATCHS NO USUARIO -------------------

1. Semântica HTTP: PUT substitui, PATCH altera uma parte

   PUT /usuarios/{id} → substitui o recurso inteiro (os dados cadastrais: nome, email, cpf)
   PATCH /usuarios/{id}/... → altera uma parte específica, uma "intenção"

Cada PATCH seu é uma intenção diferente de negócio, não um "update genérico".


2. Cada PATCH tem regras e permissões DIFERENTES

Se fosse um endpoint só, você teria que checar campo por campo quem pode mexer em quê

PATCH /usuarios/{id}/status        → intenção: ativar/bloquear
JWT válido?        ──não──> 401
É ADMIN?           ──não──> 403
status ∈ {ATIVO, BLOQUEADO, INATIVO}? ──não──> 400
usuário existe?    ──não──> 404
atualiza status    ─────────> 200 + UsuarioResponse

PATCH /usuarios/{id}/perfil        → intenção: promover/rebaixar
JWT válido?        ──não──> 401
É ADMIN?           ──não──> 403
perfil ∈ EnumPerfil? ──não──> 400
usuário existe?    ──não──> 404
atualiza perfil    ─────────> 200 + UsuarioResponse

PATCH /usuarios/{id}/senha         → intenção: trocar a própria senha
JWT válido?        ──não──> 401
É o PRÓPRIO id?    ──não──> 403
senhaAtual confere? ──não──> 400
usuário existe?    ──não──> 404
novo hash BCrypt   ─────────> 204 (NUNCA devolve senha/hash)

PUT /usuarios/{id}                 → intenção: substituir cadastro
JWT válido?        ──não──> 401
É o PRÓPRIO id?    ──não──> 403
email/cpf únicos?  ──não──> 409
usuário existe?    ──não──> 404
substitui dados    ─────────> 200 + UsuarioResponse