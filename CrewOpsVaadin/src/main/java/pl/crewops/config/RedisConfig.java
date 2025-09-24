package pl.crewops.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Log4j2
@Configuration
@EnableCaching
@EnableConfigurationProperties(RedisProperties.class)
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    @Profile("dev")
    public RedisConnectionFactory redisConnectionFactoryDev() {
        log.info("Connecting to Redis DEV at: {}", redisProperties.url());

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisProperties.url());
        LettuceClientConfiguration clientConfig =
                LettuceClientConfiguration.builder().build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    @Bean
    @Profile("production")
    public RedisConnectionFactory redisConnectionFactoryProd() {
        log.info("Connecting to Redis PROD at: {}", redisProperties.url());

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisProperties.url());
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .useSsl()
                .disablePeerVerification()
                .build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    @Bean
    @Profile("production")
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheWriter cacheWriter =
                RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory, BatchStrategies.scan(1000));

        RedisCacheConfiguration cacheConfig =
                RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues();

        return RedisCacheManager.builder(cacheWriter).cacheDefaults(cacheConfig).build();
    }
}
