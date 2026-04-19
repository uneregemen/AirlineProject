package com.ecommerce.airlineproject;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(servers = { @Server(url = "/", description = "AWS Server ") })
@SpringBootApplication
public class AirlineProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirlineProjectApplication.class, args);
    }

}
