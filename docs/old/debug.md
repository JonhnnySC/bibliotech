25/09/26 - O swager deu erro 500 - fetch error - estarei vendo oqyue deu errado 
    
    solution - springdoc.api-docs.version=OPENAPI_3_0 - força a versão a ser 3.0
Erros de cannot find symbol method builder(), getStatus(), setStatus(), etc.
Causa: Suas entidades Emprestimo.java e Exemplar.java já possuem as anotações @Getter, @Setter e @Builder corretamente escritas. O erro ocorre porque o Maven não está processando as anotações do Lombok durante a compilação.
Correção: Atualize a seção <build> do seu pom.xml para garantir que o compilador do Maven processe o Lombok:

     <!-- No seu pom.xml, substitua a tag <build> por esta: -->
    <build>
    <plugins>
        <!-- Plugin do Compilador com processamento de anotações do Lombok -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>1.18.38</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>

        <!-- Plugin do Spring Boot -->
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
    </build>











