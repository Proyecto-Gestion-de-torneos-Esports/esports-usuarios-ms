package com.torneos.usuarios.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI().info(
                new Info()
                        .title("API de Gestión de Usuarios - Gestión de Torneos Esports")
                        .version("2.0")
                        .description("Documentación de los endpoints para el microservicio de gestión de usuarios " +
                                "Permite la gestión completa de perfiles, roles y asignación de equipos (opcional)")
        );
    }
}
