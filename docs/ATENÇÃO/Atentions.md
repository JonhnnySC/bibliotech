 pom tem spring-boot-devtools. 
 Cada hot-restart dele roda esse runner de novo 
 → 
 toda vez que recompilar, token do Swagger morre e 
 precisa logar de novo. 
 
DESKTOP 

Config.java: fique só com apiUrl() (env BIBLIOTECH_API_URL, padrão http://localhost:8080). Apague adminKey() e bootstrapKey().

Sessao.java (novo, 2 linhas): campo estático guardando a chave digitada.

Tela 1 (LoginController): troque a comparação local por Sessao.setChave(campo.getText()) e siga pra tela 2. Quem valida é o back — checagem local é enfeite (qualquer um extrai string de um .class).

Tela 2: dois grupos de campos — Empresa (nome, CNPJ) e Administrador (nome, e-mail, CPF, senha). ⚠️ Ajuste o cadastro-usuario-view.fxml existente; CPF/CNPJ aceitam máscara, o app tira não-dígitos antes de enviar.

Chamada fora da thread da UI (Task, como no UsuarioController atual — o aspas() já existe lá):

 