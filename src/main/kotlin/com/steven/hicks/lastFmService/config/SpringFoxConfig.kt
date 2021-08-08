package com.steven.hicks.lastFmService.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpringFoxConfig {

    @Bean
    fun api(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("shicks255.com Last FM API")
                    .version("1")
                    .description("Test")
                    .termsOfService("https://swagger.io/terms/") // todo
                    .license(
                        License().name("Apache 2.0") // todo
                            .url("https://springdoc.org")
                    )
            )
    }
}