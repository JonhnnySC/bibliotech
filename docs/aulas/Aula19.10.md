TEMA ANTERIOR: MULTIEMPRESA E MULTI USUARIO

TEMA AGORA: CONTEXT NATIVE/REDUX E ARQUITETURA DE FRONT

por que usar contexto no front, como o front react é baseado em componentes?:

Um componente pai manda pros filhos heritantes, mas não tem como um filho mandar pra o pai, como faço um add carrinho atualizar o valor pai.
ex: ou o botão sair fazer o deslog.

não usar local estrorage, usamos contexto, então armazenamos no cooklie o token, utilizamos o contexto pois o componente filho manda info pra o contexto,e alimenta o componente pai.


Redux realiza a centralização do estado da aplicaçã, services isolam as chamadas http para melhor organização e reaproveitamento

(pra avaliação final, ele sugere o favoritos, para adicionar os contextos.)

As services no front end, são as responsailidades de isolar partes do sistemas, mas o principal é reaproveitamento de codigo, pois ele vai chamar o backend em algumas especificidade, o comum é que todas as regras controle de teal e requisição, por que? voce vai precisar usuario por id em algum contexto, não é correto ficar criando um axio get usuarios em varias telas, quando apenas um comando poderia, em mudanças, teria que mudar tudo, melhor para manutenção.

Agora tertemos pagetestes, teremos service que ter´~a cada consulta do nossos sistyemmas, e para nao dificultar muito, pode fazer a pasta tipos, para isolar os tipos em lugares especificos.

REDUX: slices são cada funcionalidade/biblioteca, por exemplo, há um slice de autenticação que faça apenas a gestão do usuario loogado, poderiamos ter muitos slides diferentes pdentro da aplicação, o que irá centralizar isso, é o store, que é como uma biblioteca de slices. STORE.

COMO APLICAR?

ex: 

AuthSlice e Store.js

Cinco hipoteses quenão precisa se utilizar um token
pra usar sem token.

Temos agora quer todo lugar precisa ter um token, o redux vai controlar nosso token, mesmo se implementar, imagina ter que modigicar todo metodo http no front pra adicionar o token um por um?

faremos de uma forma mais simples, precisamos criar a service, o priomeiro é o usuarioService.ts, nosso usuario em si agora porderá ter apenas funcções de isolamento http, a funcção vai receber uma chamada que vai mudar nosso axios;

----------------------------------------------

Arquivos criados: usuarioservice, api.ts.

Promises é uma função asincrona, e que dentor dessa funcao terá um type de typescript, por que é uma lista principalmente PromiseMUsuario[]

- api.ts vai ser onde vai estar os receptadores que tera a responsabilidade de use client. 
    
    ex: se por exemplo mudar uma variavel de ambeite, um endpoint, e se quisessemos trocar o url em producao, nao precisar mudar em varios lugaqres, a token fica na request.
    bloco de config serve pra saida e entrada, o nosso login colocou o token aonde? em nenhum lugar, por isso precisaremos fazer uma utilização de js cookies (npm js-cookie)

npm i js-cookie e
npm install @reduxjs/toolkit e
npm install -D @types/js-cookie
--------------------------------------------

precisamos na autenticação, devemos salvar o token cookie, pra isso, vamos chamar apos o 200 (do hanbdlelogin) colocar o medodo de chamada dos cookies

setamos o token onde coockies.set(token, loginResposta.data.token,{secure: true, expires:)
dentro do nosso cookie, podemos pegar dentro da config api, criar uma constrate que vai ser igual ao nosso token que vai ser igual o cookies.get("') que é basicamente busquye o token

ai diz, if token existir, coloca config.headers.authorization = a crase bearer, cifrão abre chaves token; e agora toda vida que fizemos a requeste iremos aqnexar o token na requeste, toda vez que o token for apagado, ai nfaz as questões de segurança.
e colocar export default api

iremos chamar a api em await no const dados  no buscar lista usuarios() no usuarioServicets.

a lista de usuario está na api de usuarios que é o carregar dados. vam,os pegar o .get para frente e colar no const dados api.get.

ir ao final e fazer o tratamento, pegamos a lista, então coloca'mos um : if dados.status == 200 se os dados forem 200, se forem retornamos dados.data, senão, retorna array vazio.

 - oque estamos fazendo, coloquei a implementação e chamei na funcao buiscar usuario, no lugar que eu consultava a lsita de usuario, agora precisamos chamar o buscar lista de usuario, nao precisamos passar parametros, é so apagar o .data do set usuarios.
 - agora iremos admitir isso tudo para todos os metodos do uisuario e colocar nos serviços, a partir do momento, ao ir no url /usuarios e ir em entrar e abrir o a tela dos usuario vai retornar tudo, api anexou o token, o cookie esta controlado peloi tolken, e se nao tem usuario logado não retorna nenhuma informaç~~ao, por que não tem usuawrio logado, agora fazer isso deletar, inativar desativar, editart.

depois de tudo isso, nãao prtecisamos mais mexer em muito lugar, apenas onde chama o axios, se o axio está em desuso, fazer ordem de manutwnção é, semrpre gerar erros pra eu corrigir, por exemplo, se eu apagar o axios, só vai dar erro onde ele é buscado.
iremos fazer então, criar os metodos pra fazer essas modificações, então precisamos do que pra fazer essas informações? novas funciones, como no usuairo service colocar um deletar metodo, com async function deletar(id:number)  : promise<void> {
  
  pra deletar um usuario, precisamos chamar o vart result, o ujsuario precisa ser nuloi quando estiver criando-o

peguei a validação do await, os alertas que ele dá, o novo status e trocar o axios por api, e no handleAlterarStatusUsaro colocmos apenas await atualizarstatususuario recebe usuario e coloca o carregar dados

criar uma pasta logica para colocar todos os gandlers, quanto mais crecer, dividir

o front me manmtem por 7 dias, mas o backend é pouco, se se expirar, tem que vboltar pra tela de login 

URGENTE, NA AVALIAÇÃO FINAL, SE EXPIRAR O TOKEN, DEVE-SE VOLTAR PRA A TELA DE LOGIN

------------------------------------------------------------------
ATIVIDADE - MIGRAR O FRONT END PRA SERVICE

