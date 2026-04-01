package com.ecommerce.airlineproject.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import org.springdoc.core.customizers.OpenApiCustomizer;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info().title("Airline API System").version("v1"))
                                .addSecurityItem(new SecurityRequirement().addList("bearerAuth")) // TÜM ENDPOINT'LERE
                                                                                                  // TOKEN ŞARTI EKLER!
                                .components(new Components()
                                                .addSecuritySchemes("bearerAuth",
                                                                new SecurityScheme()
                                                                                .name("bearerAuth")
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")
                                                                                .in(SecurityScheme.In.HEADER)));
        }

        @Bean
        public OpenApiCustomizer customServer() {
                // "/" URL'si browser üzerinden gelen isteğin (origin) host ve portunu dinamik olarak kullanır!
                // Yani 8080'den girerseniz istekler 8080'e gider, 8081'den girerseniz 8081'e gider.
                return openApi -> openApi.setServers(List.of(
                                new Server().url("/").description("Default Server (Dynamic based on port)")
                ));
        }
}