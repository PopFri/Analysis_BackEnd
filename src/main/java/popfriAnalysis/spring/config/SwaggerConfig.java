package popfriAnalysis.spring.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version("v1.0")
                .title("PopFri Analysis API")
                .description("SWAcademy 프로젝트 API");

        // JWT 인증 방식 설정
        String jwtScheme = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtScheme);
        Components components = new Components()
                .addSecuritySchemes(jwtScheme, new SecurityScheme()
                        .name("Authorization")
                        .type(SecurityScheme.Type.HTTP)
                        .in(SecurityScheme.In.HEADER)
                        .scheme("Bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .addServersItem(new Server().url("http://localhost:8082"))
                .components(components)
                .info(info)
                .addSecurityItem(securityRequirement);
    }

}