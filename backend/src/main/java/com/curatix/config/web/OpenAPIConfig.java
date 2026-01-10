package com.curatix.config.web;

import com.curatix.config.properties.ApplicationAPIProperties;
import com.curatix.config.properties.ApplicationProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

/**
 * OpenAPI (Swagger-doc) configuration
 */
@Configuration
@RequiredArgsConstructor
public class OpenAPIConfig {
    private static final String BEARER_AUTH = "bearerAuth";

    private final ApplicationProperties applicationProperties;
    private final ApplicationAPIProperties apiProperties;


    /**
     * OpenAPI configuration for non-production environment
     */
    @Bean
    @Profile("!prod")
    public OpenAPI nonProductionOpenAPI() {
        return createOpenAPI(getDevelopmentServers(apiProperties.servers().dev()));
    }

    /**
     * OpenAPI configuration for production environment
     */
    @Bean
    @Profile("prod")
    public OpenAPI productionOpenAPI() {
        return createOpenAPI(getProductionServers(apiProperties.servers().prod()));
    }


    // Helper methods

    /**
     * Configuration for open api
     */
    private OpenAPI createOpenAPI(List<Server> servers) {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationProperties.name())
                        .version(applicationProperties.version())
                        .description("An enterprise-grade platform for intelligent aggregation, curation, and distribution of digital content.")
                        .contact(new Contact()
                                .name(applicationProperties.name())
                                .email(applicationProperties.support().email())
                        )
                        .license(new License()
                                .name(applicationProperties.license().name())
                                .url(applicationProperties.license().url())
                        )
                )
                .servers(servers)
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Bearer Token")
                        )
                );
    }

    /**
     * Returns a list of development servers
     */
    private List<Server> getDevelopmentServers(List<String> urls) {
        return urls.stream()
                .map(url -> new Server().url(url).description("Development Environment"))
                .toList();
    }

    /**
     * Returns a list of production servers
     */
    private List<Server> getProductionServers(List<String> urls) {
        return urls.stream()
                .map(url -> new Server().url(url).description("Production Environment"))
                .toList();
    }
}
