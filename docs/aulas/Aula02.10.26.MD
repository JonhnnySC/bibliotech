Specs, clean arc deve ser pedido pra IA EXPLICITAMENTE, NÃO VAI SALVAR NO FINAL DO SEMESTRE, SOLID E CA 

Padrão é NECESSÁRIO.

O padrão de projeto é Desing Patterns

envez de atualizar, coloque metodoDeAtualizarStatus, ao pedir pra ia esxplicar, colocar explicitamente o que o metodo faz como nome.

Single Responsability Principle (SRP) é um dos princípios do SOLID, 

- que afirma que uma classe deve ter apenas uma razão para mudar, ou seja, deve ter apenas uma responsabilidade. 
Isso significa que cada classe deve ser responsável por uma única parte da funcionalidade do sistema, tornando o código mais modular, fácil de entender e manter.


    ATENTAR PARA UTILIZAR pra falar na afvaliacao
- Liskov Substitution Principle (LSP) é outro princípio do SOLID, 
que afirma que objetos de uma classe derivada devem poder ser substituídos por objetos da classe base sem alterar o comportamento do programa.

    ISP (Interface Segregation Principle) é outro princípio do SOLID.
- as interfaces devem ser específicas para cada cliente, 
- evitando que uma interface seja forçada a implementar métodos que não são relevantes para ela.
    
    
    DIP (Dependency Inversion Principle)
- O princípio da inversão de dependência afirma que módulos de alto nível não devem depender de módulos de baixo nível, 
- mas ambos devem depender de abstrações.
- usado em repositorios extwernmos, qual a perguntsa mesmo wque ele fazer?

--------------------------------------------------

Presentation Layer (Camada de Apresentação) é responsável por lidar com a interface do usuário e a interação com o sistema, 
as controllers que tem a responsabilidade de receber as requisições do usuário, processá-las e retornar a resposta adequada.
coloca-se os DTOs (Data Transfer Objects) nessa camada, que são objetos usados para transferir dados entre a camada de apresentação e a camada de aplicação.




Application Layer (Camada de Aplicação) é responsável por coordenar a lógica de negócios e orquestrar as operações do sistema,
gerenciando a comunicação entre a camada de apresentação e a camada de domínio.
Coloca-se os casos de uso (use cases) nessa camada, que representam as ações que o sistema pode realizar em resposta às solicitações do usuário.

exemplo de caso de uso: CreateUserUseCase, UpdateProductUseCase, PlaceOrderUseCase, e os dtos

Dommain Layer (Camada de Domínio) é responsável por representar o núcleo do sistema,
contendo as entidades, regras de negócio e lógica de domínio.
Coloca-se os agregados, entidades e objetos de valor nessa camada, que são os elementos fundamentais do modelo de domínio.

exemplo de entidade: User, Product, Order, etc.

Infra Layer (Camada de Infraestrutura) é responsável por fornecer suporte técnico e serviços auxiliares para as outras camadas do sistema,
como acesso a banco de dados, serviços externos, envio de e-mails, etc.
Coloca-se os repositórios, serviços de integração e adaptadores nessa camada, 
que são responsáveis por interagir com recursos externos e fornecer funcionalidades adicionais para o sistema.

exemplo de repositório: UserRepository, ProductRepository, OrderRepository, etc.

----------------------------------------------------------------------------------------------------------------

PRESENTATION pode apenas fazer import de APPLICATION, mas não pode fazer import de DOMAIN ou INFRA.

AO contrário, APPLICATION pode fazer import de DOMAIN, mas não pode fazer import de INFRA.

DOMAIN pode fazer import de INFRA, mas não pode fazer import de APPLICATION ou PRESENTATION.

INFRA pode fazer import de DOMAIN, mas não pode fazer import de APPLICATION ou PRESENTATION.
---------------------------------

se tem um pacote dentro de um pacote é ruim

Pesquisar onde colocasr o exceptions, provavel que fique na aplication 

-------------
Modificados, auth controller,m token sercice, usuario service, usuario controller, feito dto, 

Streams: :: chame o construtor da classe que tenha a entrada do objeto do tipo da stream

lista de stream para lista de dto
