package com.example.basketball.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("AI篮球训练平台 API")
                .version("1.0.0")
                .description("基于AI的科学智能篮球训练Web平台 API文档")
                .contact(new Contact()
                    .name("开发团队")
                    .email("support@example.com")));
    }

}
