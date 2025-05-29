package pl.crewops.openAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI() {
        OpenAPI openAPI;
        try {
            Path path = Paths.get("CrewOpsCore/src/main/resources/static/swagger.yaml");
            //            Path path = Paths.get("src/main/resources/static/swagger.yaml");
            InputStream inputStream = Files.newInputStream(path);
            Yaml yaml = new Yaml();
            Map<String, Object> yamlMap = yaml.load(inputStream);

            openAPI = convertToOpenAPI(yamlMap);

        } catch (Exception e) {
            e.printStackTrace();
            openAPI = new OpenAPI();
        }

        openAPI.components(new Components()
                        .addSecuritySchemes(
                                "bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token"))
                        .addSecuritySchemes(
                                "Client-Id",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Client-Id")
                                        .description("Secret client ID")))
                .info(new Info().title("CrewOps API").version("1.0.0").description("API Documentation"))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt").addList("Client-Id"))
                .servers(List.of(new io.swagger.v3.oas.models.servers.Server()
                        .url("http://localhost:8080")
                        .description("API Server (Dev)")));

        return openAPI;
    }

    private OpenAPI convertToOpenAPI(Map<String, Object> yamlMap) {
        ObjectMapper mapper = Json.mapper();
        return mapper.convertValue(yamlMap, OpenAPI.class);
    }
}
