package pl.crewops.infrastructure.core;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import pl.crewops.security.config.SecurityConfigProperties;
import pl.crewops.util.RoleResolver;

@Configuration
@EnableConfigurationProperties(CoreProperties.class)
@RequiredArgsConstructor
class CoreConfig {

    private final SecurityConfigProperties securityConfigProperties;
    private final RoleResolver roleResolver;

    @Bean
    public CoreClient coreClient(CoreProperties coreProperties) {
        var restClient = RestClient.builder()
                .baseUrl(coreProperties.baseUrl())
                .defaultHeader("Client-Id", securityConfigProperties.getClientIdInput())
                .build();

        return new CoreClient(restClient, roleResolver);
    }
}
