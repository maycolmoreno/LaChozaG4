package com.lachozag4.pisip.infraestructura.configuracion;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI openAPI() {
        final String schemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Pisip API - La Choza")
                        .description("API REST para el sistema de pedidos del restaurante La Choza. " +
                                "Autenticarse con POST /api/usuarios/login para obtener el token JWT.")
                        .version("0.0.1-SNAPSHOT"))
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components()
                        .addSecuritySchemes(schemeName, new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
