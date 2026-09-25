2026-09-18T15:12:40.112-03:00  WARN 29599 --- [bibliotech] [           main] o.s.core.events.SpringDocAppInitializer  : SpringDoc /v3/api-docs endpoint is enabled by default. To disable it in production, set the property 'springdoc.api-docs.enabled=false'

2026-09-18T15:12:40.112-03:00  WARN 29599 --- [bibliotech] [          main] o.s.core.events.SpringDocAppInitializer  : SpringDoc /swagger-ui.html endpoint is enabled by default. To disable it in production, set the property 'springdoc.swagger-ui.enabled=false'




CORS
1. allowedOrigins("*") - NUNCA USE EM PRODUÇÃO Isso permite qlqr site fazer
   req ao seu backend.

2. Se precisar de configurações diferentes por ambiente, use @Value ou @Profile:
    - @Value("${app.cors.allowed-origins}")
    - @Profile("dev") / @Profile("prod")

3. Alternativa: Use @CrossOrigin diretamente nos controllers para controle granular:
   @RestController
   @CrossOrigin(origins = "http://localhost:3000")
   public class MeuController { ... }

^^ 

pesquisar por que da 2 e 3.


* Enquanto o JwtFilter não existir, TODAS as rotas estão abertas, inclusive
* PATCH /perfil (qualquer um viraria ADMINISTRADOR). Use somente na sua máquina.
* Quando o filtro entrar, cada rota ganha uma regra de quem pode chamar
* (a tabela de permissões está na resposta do chat)