package com.weatheria.weatheria.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI weatherApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Weather API")
                        .description("Simple Weather API using Open-Meteo (free, no key required)")
                        .version("1.0.0")
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .externalDocs(new ExternalDocumentation()
                        .description("Open-Meteo API Docs")
                        .url("https://open-meteo.com/en/docs"));
    }
}
