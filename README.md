# BiblioTech

Sistema de biblioteca multi-empresa: API Java/Spring Boot, front Next.js/TypeScript
com Redux Toolkit e aplicativo desktop JavaFX para a primeira configuração.

## Subir do zero

1. `cp .env.example .env` e preencha.
   `JWT_SECRET` e `BOOTSTRAP_KEY`: `openssl rand -hex 32` (mínimo 32 caracteres).
2. `docker compose up -d` — sobe o PostgreSQL.
3. `./mvnw spring-boot:run` — API em http://localhost:8080 (JDK 25).
4. `cd front && npm install && npm run dev` — http://localhost:3000.
5. `cd admin-desktop && ./mvnw clean javafx:run` — informe a `BOOTSTRAP_KEY` e
   crie a biblioteca + o primeiro administrador.
6. Entre no front com o usuário recém-criado.

Swagger: http://localhost:8080/swagger-ui.html

## Banco do zero

`docker compose down -v && docker compose up -d`

Não há migrations nem INSERT de seed: o schema é gerado pelo Hibernate a partir
das entidades (`ddl-auto=update`) e o primeiro administrador nasce pelo
`POST /auth/bootstrap`, chamado pelo app JavaFX.

## Arquitetura

| Camada | Conteúdo |
|---|---|
| `presentation` | controllers finos + `ApiExceptionHandler` (exceção → HTTP) |
| `application` | services (orquestração), DTOs, exceções e **portas** |
| `domain` | entidades JPA, enums, value object `CPF`, regras de negócio |
| `infrastructure` | segurança (JWT/BCrypt), persistência, ViaCEP, config |

A `application` só conhece interfaces (`TokenPort`, `SenhaPort`, `UsuarioLogadoPort`,
`ConsultaCepPort`, `LivroRepository`, `BibliotecaRepository`); a `infrastructure`
as implementa.

## Multilocação

Toda entidade de negócio tem `biblioteca_id`, definido pelo token — nunca pelo corpo
da requisição. Toda consulta filtra pela biblioteca do usuário logado, e acesso por id
a registro de outra biblioteca responde **404** (não 403, que confirmaria a existência).

## Token

JWT assinado + registro na tabela `token`. A tabela é zerada a cada boot
(`TokenClearer`), então reiniciar a API invalida todos os tokens.
No front, token e usuário vivem **exclusivamente na store do Redux**.