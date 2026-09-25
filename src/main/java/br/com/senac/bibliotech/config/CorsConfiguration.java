package br.com.senac.bibliotech.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    /*
    cros origm resource sharing

    pra que preciso disso:
    navegadiores bloqueiam equisicoes de dominios diferentes por seg.
    front e back rodam em portas diferentes por isso precisa, 3000 e 8080
     */

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // aplica toas as rotas de api,
                            //  (aparentremente em produção é bom usar variaveis de ambiente e listar domínios)
                .allowedOrigins("http://localhost:3000") // - ORIGEM DO FRONT
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD") //METODOS HTTPS PERMITIDOS que calor do cacete
                .allowedHeaders("*") // permite headers personalizados (nao sei oque é)
                .allowCredentials(true) //permite os malditos coockies
                .maxAge(3600); // definir quanto  tempo o navegador slembra dessa config (entender pra que serve)
    }
}
