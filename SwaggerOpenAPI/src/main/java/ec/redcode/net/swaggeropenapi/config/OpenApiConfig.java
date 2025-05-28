package ec.redcode.net.swaggeropenapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI para la documentación de la API.
 *
 * @author Jorge Murillo
 * @version 1.0
 * @since 2025
 */
@Configuration
public class OpenApiConfig {
    /**
     * Configuración personalizada de OpenAPI
     *
     * @return instancia configurada de OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Management API")
                        .version("1.0.0")
                        .description("API para gestión de usuarios con Spring Boot 3")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("jorge.murillo@redcode.net.ec")
                                .url("https://www.redcode.net.ec"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
