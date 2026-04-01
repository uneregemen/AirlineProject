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
                return openApi -> openApi.setServers(List.of(
                                new Server().url("http://16.171.6.106:8081").description("AWS Gateway (Production)"),
                                new Server().url("http://localhost:8081").description("Local Gateway (Development)")
                ));
        }
}