informação compartilada por todo o sistema 

alimentar tabelas que vao ajudar o cvontexto de segurança do projeto, o usuario logado precisa ter mais informações, precisa ter 100% do tempo o registro total dos usuarios

o MERCADO USA QUE, TEM QUE GARTANTIR QUE O TOKEN GERADO FOI TU QUE GEROU, simnples.
gero e salvo o token no banco de dados, e quando o usuario logar novamente, 
eu verifico se o token que ele me enviou é o mesmo que eu gerei e salvei no banco de dados, se for, ele é valido, se não for, ele é invalido.

Token gerado as tanto, acessou tais endpoints, token expirou, se token fez muitas chamadas entao se bloquea

Para aumentar a seguranla precisa de mais informações, ]

METODO DE FOREIN KEY PARA ENTIDADE

QUERRY FILTER EM INTERFACES, ISSO É AVANÇADOL MAS APRENDEWR 
-p

feito token entity, token serviucem usuarioLogado, SecurityConfiguration

depois de verificar o usuario logado , depois clocar um optionals de token que vai retornar um finTokenbyToken, abre parentresis string token, basicamento buscar o token pelo token, onde chama o token repository, findtokenbytokn(token or els null

e depois if(tokenbanco == null) 
throw new Ilegal ArgumentException("Token inválido");

E PEGGAR NO RETURN NEW USAUARIO LOGADO(TOKENbANCO.GETUSUARIO()))

validei se o token gerado foi o token salvo no banco de dados, o verify verifica varias coisas do token

consultei no banco se o token existe, se ele é valido, se ele não expirou, se ele não foi bloqueado por muitas chamadas, e se tudo estiver certo, então eu retorno o usuário logado.

se nao for null, registro ele como usuaio logado, assim vai dar um erro, pq no jwfilter vai dar errado, poque o ger subject nao funciona, o jwtvalidator,agor a é usuario logado, os rooms agora precisamos pegar as autorizações e setar nas funções

as autorizações estão na entidade de usuario com injection de collection extends granted autority get autorities
e agora return list.of(new SimpleGrantedAuthority(this.role));

Ouque é legal, agora em qualquer parte no sistema, podemos criar um método que vai pegar o usuario logado, e com isso podemos pegar as autorizações dele, e com isso podemos fazer validações de segurança em qualquer parte do sistema.

tipo usuario logado getusuariologado( {
return (UsuarioLogado) SecurityContextHolder.getContext().getAuthentication().getPrincipal();)
}
isso salva no objectprincipal o usuario logado, e com isso podemos pegar as autorizações dele, e com isso podemos fazer validações de segurança em qualquer parte do sistema. (podemos fazer tipo um objeto e colocar aqpenas uma notação? ou algo sutil para pegar o usuario logado?)


a gente usa isso por que?? eu qwuero no meu usuario um private string nomeUsuarioCadastro ou seja, quero saber o nome de usuario qqe cadastrou, então coloca no service qe orquestra essa entidade, voce coloca la o 

var usuariologado get usuario lgado(). 

1 L se coloca quando nao tem tabela ainda pro sistema, apenas pra exemplificar

cada usuario que fopr logar, eu saberei sem precisar retorno do fronmt end, o front nao informa quem que ta fazendo, o resto é tudo pelo token, 

pra o qacesso a endpoints, a gente só faqz isso pelos roles, com request matrchers, dizemos que o httpmethod.get do usuarios eles so pode acessar se ele eem a role admim


JTW DECRIPT, O ROLE NAO VAI RETORNAR., APENAS O EMAIL, POIS É MAIS SEGURO POR QUE OS TOKNESOIA COMPLEWMTAMERNTE INSEGUROS SE VAZADOS, ELE DA ENCRIPT DEVNOVO E TUIDO SE LASCA

O MAIS DIFICIL AGORA É SABER QUEM ETSA AUTORIZADO A OQUER

Controles de dados, a empresa por exemplo vai ter m curso, quem vai pegaro  ide da empresa e apenas o back end com logicvas.


