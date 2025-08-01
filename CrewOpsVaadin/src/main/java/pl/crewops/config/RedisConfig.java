package pl.crewops.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfig {

    @Bean
    @Profile("dev")
    public RedisConnectionFactory lettuceConnectionFactoryDev() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379));
    }

    @Bean
    @Profile("prod")
    public RedisConnectionFactory lettuceConnectionFactoryProd() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("crewops-redis", 6379));
    }
}
