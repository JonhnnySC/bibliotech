14/09/26 23:26 - Task > Colocar as entidades e controller

15/09/26 19:10 - Erros - 

@SUPERBUILDER deve estar em ambas as classes que estão realizando a heraça
senão a a propria IDE nao sabe op que fazer

- UsuarioController - faltou "return ResponseEntity.notFound().build();"
- NÃO era exatamente isso, o erro foi por que repositories são interfaces,
eu criei como classe normal, dando erro, pois não é possivel instanciar uma interface, 
então a IDE não sabe o que fazer, e da erro de compilação.
- 
- -----------------------------

    Entidade Leitor está em hierarquia single table (herdando de outra entidade, usuario)
    Usuario tem @Entity mas não declara @Inheritance, e o JPA assume SIngle_table, quando não especifica.

    -  _Leitor extends Usuario_ chega !Table(name = "leitor") 

    -  SOLUÇÕES table da leitor resolveria = SINGLE_TABLE jogaria LEITOR e outros tipos de usuarios em uma unica tabela 
     com varias colunas nulas dependendo do tipo, o melhor caso é Joined Usuario vira a tabela base, e 
     Leitor  vira uma tabela separada com FK par ausuario id.

17/09/26 14:42 - Fluxo de uso dos database e flyway como produção correta
    
- colocado flyway no pom (Como seu parent é spring-boot-starter-parent, o Spring Boot já gerencia a versão do Flyway compatível — não precisa especificar <version>.)
- trocar ddl-auto no aplicatioon.propreties para validate.
- criado pasta de migrations

ERROS: 

criado flyway e não estava sendo detyctado pois o spring já tem isso natursalmente,

colocado - @Column(name = "data_impressao") em cima do atributo exemplar - por que ?

O Hibernate, quando não recebe instrução explícita, usa uma estratégia de nomenclatura implícita: 

ele pega o nome do campo Java (dataImpressão, por exemplo) e converte automaticamente pra snake_case — só que ele faz essa conversão preservando qualquer caractere que já esteja lá, acento incluso. 

Por isso lá no log original do Hibernate (antes de qualquer migration existir) apareceu:
create table exemplar 

(..., data_impressão date, ...)

Ou seja: o nome do campo Exemplar tinha o acento (dataImpressão), e o Hibernate propagou isso pro nome da coluna esperada.

- retirado o ID de Leitor por que ele já herda o id do usuario

15:52 - Colocar swagger

- colocar autor repository, editora, livro, exemplar, leitor e empresitmo.

- ERRO: "Could not autowire. No beans of 'JpaRepository<Autor, Long>' type found":

Spring ele tem dificuldade de resolver genéricos (JpaRepository<Autor, Long>) quando existem vários beans diferentes desse mesmo tipo genérico base no contexto (UsuarioRepository, AutorRepository, LivroRepository, etc — todos são, no fundo, "um JpaRepository de alguma coisa"). Em tempo de execução o Spring resolve isso certinho (ele guarda a informação de tipo genérico do bean via ResolvableType)

correção: mais correto architeturalmente: em vez de injetar JpaRepository<Autor, Long> genérico, injeta direto o AutorRepository que você já criou (a interface específica):

    private final AutorRepository autorRepository;

    public AutorController(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

18/09/26 14:27 - Fazer enuns Colocar os relacionamentos das entidades;

Questões para se ter em mente:
    
    Senha em texto puro. Ela precisa de hash BCrypt.
    
    Exemplar:

    O dataImpressão com acento e o @Column são um remendo. 
    Renomeie para dataImpressao e remova o @Column, porque o Spring Boot já converte camelCase para snake_case.
    O quantidadeDisponivel (String) contradiz sua própria decisão. 
    Cada exemplar deve ter um status (enum), exatamente como você escreveu no comentário do Livro.

ENUMS: 
- enum genero
- enum perfil
- enum status exemplar
- enum satatus usuario
- enum status emprestimo
-------------------------

19/09/26 - Realizações dos restos dos controladores após fazer um service bem feito.

20/09/26 - Realizações de services , FIZ MUITAS ALTERAÇÕES NOS CONTROLLERS - FAZER UMA NVA BRANCH


    

