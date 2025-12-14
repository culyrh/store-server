package com.bookstore.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "JWT";

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(jwtSchemeName);

        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT 토큰을 입력해주세요. (Bearer 제외)")
                );

        return new OpenAPI()
                .addServersItem(new Server()
                        .url("http://113.198.66.75:" + serverPort)
                        .description("Production Server")
                )
                .addServersItem(new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Local Server")
                )
                .info(new Info()
                        .title("Bookstore API")
                        .description("온라인 서점 API 문서")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Bookstore Team")
                                .email("support@bookstore.com")
                        )
                )
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}