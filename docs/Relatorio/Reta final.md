fase 1: base do back

- login devolve o usuario logado junto do token (dto publico, sem cpf nem senha)
- principal de seguranca volta a se chamar usuariologado
- logout revoga o token no banco (post /auth/logout, 204)
- expiracao do jwt configuravel por JWT_EXPIRATION_MINUTES no .env
- tabela token apagada na subida: reiniciar o back invalida tokens antigos
- get /livros/generos protegido para o teste de seguranca
- log do spring security de debug para info

fase 2: 


