package com.product.inventory.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for openAPI/Swagger.
 * This will add title, version and description to
 * <a href="http://localhost:8080/swagger-ui/index.html">...</a> page
 */
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Inventory API")
                        .version("1.0")
                        .description("API for managing products inventory"));
    }
}
