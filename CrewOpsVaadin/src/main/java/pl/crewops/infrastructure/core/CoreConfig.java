package pl.crewops.infrastructure.core;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(CoreProperties.class)
class CoreConfig {

    @Bean
    public CoreClient coreClient(CoreProperties coreProperties) {
        var restClient = RestClient.builder().baseUrl(coreProperties.baseUrl()).build();

        return new CoreClient(restClient);
    }
}
