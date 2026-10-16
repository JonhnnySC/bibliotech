Tecnicas ate ofinal

back - Srping security e ddd
fron - redux e service
geralk - ci/cd

AULA DE SABADO - COLOCAR OS CONTEUDOS

conceito de multiusuarisos e multiempresas


teremos dus semanas trabalhar no projeto ou seja - 4 aulas - 


----------

Regras de negocio orientado a ddd

Service é para orquestracao, o dominio é para regras de negocio, e o repositorio é para persistencia
diferença entre service e dominio - service é para orquestracao, o dominio é para regras de negocio, e o repositorio é para persistencia

Metodos de dominio - regra de negocio, validação, calculo, etc
Metodos de service - orquestracao, chamar outros services, chamar repositorio, etc


Uma classe pode e deve ter um relacionamento bem definido e seus metodos devem ser coesos, ou seja, devem ter uma responsabilidade única e clara. Isso facilita a manutenção e a evolução do sistema.

EXEMPLO DE DD

Service de Dominio: Regras puras, sem dependencia externa - gerarProgresso

VALUE OBKJECT [É NOSSAS ENTIDADES QUE VAO TER REGRAS DE NECOCIOS QUE NAO SERTAO PERSISTIDSAS NO BANCO DE DADOS
TUDO AQUILO QUE TEM UMA REGRA DE NEGOCIO BEM ESPECIFICA, POR EXEMPLO, VALIDAÇÃO DE TELEFONE NÃO PRECISA ESTAR NA ENTIDADE, PROGRAMA UMA VEZ SO PARA NAO FAZER DENOVO

Criamos value object de cpf, 

metodo nao existente, lampaDA E CRIAR PREVIOSAMENTE.
 VER HERANÇA QUANTO AOS CONTRUCTOURS E ROLER DOS USUIARIOS 