Enquanto todo local host, ou melhor, todo endpoint nao tiver chamado api então ainda precisa migrar codigo ainda.

Agora tem que imaginar que as request vao virar um só, o post loginresponse é o contexo de autenticação.

qua a controller que bate no back end? é o auth no login response no var login resposta.
para cada controller, preciso de um contra parte no service no front, se tiver 10 controllers, há 10 services.

basicamente replicar o back no front com tecnica de typescript se não tem, só adicionar o metodo, modelo mais simples de fazer, por que pra fazer um por um mais seguro e rapido.

busca > service> novo arquivo > authservicets >
    
    export async function loginBack(paramns:type) o tipo do var loginResposta

dois campos de string, mas iremos trasformar em objeto, como o axios está recebemndo duas constanmtes, então coloca no login back(emaill:string, senha:string)

a saida dos metodos axios há sempre antes do protocolo e o nome do metodo, a saida do metodo sempre termina em promise<loginresponse>

só olhando os dados do que ja tem

copimos o axios que esta ali, login resposta, e jogar dentro do metodo auth services, troxa axios por api, e enves de emeil tela vai ser email email e senha senha, e então daremos return loginResposta.data

oque a gente faz se o status da outro que 200? precisamos fazer com que data só tem o objeto que o backend retornou, entao precisamos colocar o if(loginresposta.statos DIFERENTE DE 200)

nisso colocamos o alerta = por que tudo que valida o protocolo status ou colocar no service ou verificar se é ulo ou não, uma boa forma é o try catch

voltamos pro page tsx do login reposta, trocamos o await e colocamos no login back, agr dá werro por quer nao temos status nem outra coisa.


com local host, desapareceu no zntrl f, e com localhost, se basicamente fizemos o login, está tudo funcioinando, mantwemos a mesma funcionalidade com strutura melhor

----------------------------------------------------------

toda role que não seja adminm, precisa de uym filtro

ele vai deletar a base de dados, como testar? como funciona o multilayout, qual a hierarquia delayout, configuração de layolt?
css nunca com painel admin,

e os slices no front? ten qye saver cini avukutar iy desavukludar is endpoints de segurança, o backend pode chamar outros backends, tipo cep.

pergunta tipo isso é oque? é clean code e dd?