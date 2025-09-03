package com.example.hello_friends.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // JWT 보안 스키마 설정
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        // 보안 요구사항 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        // OpenAPI 객체 생성 및 보안 설정 적용
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement)
                .info(new Info()
                        .title("Happy-Friends")
                        .description("Happy-Friends REST API")
                        .version("1.0.0"));
    }

    @Bean
    public GroupedOpenApi commonApi() {
        return GroupedOpenApi.builder()
                .group("common-api") // API 그룹 이름 설정
                .pathsToMatch("/api/**")
                .pathsToExclude("/api/user/**", "/api/admin/**")
                .addOpenApiCustomizer(openApi -> {
                    openApi.setInfo(new Info()
                            .title("Happy-Friends") // API 제목
                            .description("Happy-Friends REST API")
                            .version("1.0.0") // API 버전
                    );
                })
                .build();
    }

    @Bean
    public GroupedOpenApi kioskApi() {
        return GroupedOpenApi.builder()
                .group("user-api") // API 그룹 이름 설정
                .pathsToMatch("/api/user/**")
                .addOpenApiCustomizer(openApi -> {
                    openApi.setInfo(new Info()
                            .title("Happy-Friends") // API 제목
                            .description("Happy-Friends REST API")
                            .version("1.0.0") // API 버전
                    );
                })
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin-api") // API 그룹 이름 설정
                .pathsToMatch("/api/admin/**")
                .addOpenApiCustomizer(openApi -> {
                    openApi.setInfo(new Info()
                            .title("Happy-Friends") // API 제목
                            .description("Happy-Friends REST API")
                            .version("1.0.0") // API 버전
                    );
                })
                .build();
    }
}
