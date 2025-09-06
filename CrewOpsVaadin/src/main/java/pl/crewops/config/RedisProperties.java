package pl.crewops.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cache")
public record RedisProperties(String url) {}
