package com.arsalan.tenanttable.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenApi() {
        final String securitySchemeName = "Bearer Authentication";
        return new OpenAPI()
                .info(
                        new Info()
                                .title("TenantTable API")
                                .version("v1.0.0")
                                .description("""
                                        TenantTable is a multi-tenant Restaurant POS backend API.
                                        
                                        ## Features
                                        - Authentication & Authorization
                                        - Tenant Management
                                        - Employee Management
                                        - Category & Menu Management
                                        - Dining Table Management
                                        - Order Management
                                        - Billing & Payments
                                        
                                        ## Authentication
                                        All protected endpoints require a JWT Bearer token.
                                        
                                        ## Multi-tenancy
                                        Every request is isolated to the authenticated tenant, ensuring complete data separation between restaurants.
                                        """)
                                .contact(
                                        new Contact()
                                                .name("Arsalan Rather")
                                                .email("arsalanrather.dev@gmail.com")
                                                .url("https://github.com/Arsalanamin404")
                                )
                                .license(
                                        new License()
                                                .name("MIT License")
                                )
                )
                .addSecurityItem(
                        new SecurityRequirement().addList(securitySchemeName)
                ).components(
                        new Components()
                                .addSecuritySchemes(
                                        "Bearer Authentication",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }
}
