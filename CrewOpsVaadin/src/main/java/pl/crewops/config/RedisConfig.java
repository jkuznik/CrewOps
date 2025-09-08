package pl.crewops.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Log4j2
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory lettuceConnectionFactoryDev() {
        log.info("Current cache url: " + redisProperties.url());

        var redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisProperties.url());

        var clientConfig = LettuceClientConfiguration.builder().useSsl().build();

        return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
    }
}
