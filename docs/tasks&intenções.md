14/09/26 23:26 - Task > Colocar as entidades e controller

15/09/26 19:10 - Erros - 

@SUPERBUILDER deve estar em ambas as classes que estão realizando a heraça
senão a a propria IDE nao sabe op que fazer

- UsuarioController - faltou "return ResponseEntity.notFound().build();"
- NÃO era exatamente isso, o erro foi por que repositories são interfaces,
eu criei como classe normal, dando erro, pois não é possivel instanciar uma interface, 
então a IDE não sabe o que fazer, e da erro de compilação.
- 