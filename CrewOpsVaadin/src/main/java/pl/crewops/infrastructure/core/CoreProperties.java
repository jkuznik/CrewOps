package pl.crewops.infrastructure.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("core")
public record CoreProperties(
        //        String apiKey,
        String baseUrl) {}
