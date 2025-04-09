package pl.crewops.infrastructure.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("core")
record CoreProperties(
        //        String apiKey,
        String baseUrl) {}
