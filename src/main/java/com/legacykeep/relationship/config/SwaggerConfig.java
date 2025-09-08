package com.legacykeep.relationship.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI configuration for the Relationship Service.
 * 
 * Provides API documentation and interactive testing interface.
 * Follows the same Swagger configuration pattern as other LegacyKeep services.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LegacyKeep Relationship Service API")
                        .description("Ultra-flexible relationship management microservice for LegacyKeep. " +
                                   "Manages multi-dimensional relationships between users with support for " +
                                   "family, social, professional, and custom relationship types.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("LegacyKeep Team")
                                .email("support@legacykeep.com")
                                .url("https://legacykeep.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8083")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.legacykeep.com/relationship")
                                .description("Production Server")
                ));
    }
}

