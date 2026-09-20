Mudei spring dockl de 2.1.0 para 3.1.1

coloquei BCRYPT - 
<dependency>
<groupId>org.springframework.security</groupId>
<artifactId>spring-security-crypto</artifactId>
<version>7.2.0-M1</version>
<scope>compile</scope>
</dependency>

------
Implementado questões que os principais codigos do fulçlstack apresentaram.

-----------------------
UsuarioCOntroler recebe Usuario no Post er no Put

- O cliente pode enviar "perfil": "ADMINISTRADOR" e ele é gravado.]
- Senha em txto puro : ela nunca vai bater com o mathces do bcrypt no login
- Put devolve 200 com o corpo vazioo quando o id não existe, por que:
  - ResponseEntity.ok(usuarioBanco) roda mesmoo com usuarioBanco == null. Deveria ser 404.
    - Put também pode gerar 500: ele copia status do json, e se vier nulo viola o not null.
    

        SOLUÇÕES:
      usar o UsuarioRequest e o UsuarioService que te enviei, onde o perfil é decidido pelo servidor. 

      O PATCH de status e o soft delete viram métodos do service (atualizarStatus, inativar). 

       DELETE /{id}/excluir por DELETE /usuarios/{id}, porque o verbo HTTP já diz "excluir".

Mudanças no UsuarioCOntroller colocando valid e outro request body