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

