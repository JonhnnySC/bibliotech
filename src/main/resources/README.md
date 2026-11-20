# BiblioTech

## Como subir do zero?
1. `cp .env.example .env` preencha (JWT_SECRET com 32+ caracteres; BOOTSTRAP_KEY: `openssl rand -hex 32`)
2. `docker compose up -d`
3. `./mvnw spring-boot:run` (JDK 25)
4. `cd front && npm install && npm run dev`
5. `cd admin-desktop && ./mvnw clean javafx:run` (JDK 21+) — crie a biblioteca e o administrador
6. Logue no front (http://localhost:3000) com o usuário criado

Swagger: http://localhost:8080/swagger-ui.html
Banco do zero: `docker compose down -v && docker compose up -d` (apaga os dados)