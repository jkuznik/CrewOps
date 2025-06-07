package pl.crewops.redis;

import java.time.Duration;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

@Configuration
public class RedisConfig {
    //      more global config way
    //    @Bean
    //    public RedisCacheConfiguration cacheConfiguration() {
    //        return RedisCacheConfiguration.defaultCacheConfig()
    //                .entryTtl(Duration.ofMinutes(5))
    //                .disableCachingNullValues()
    //                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new
    // GenericJackson2JsonRedisSerializer()));
    //    }
    //      more specific config way
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder.withCacheConfiguration(
                        "itemCache",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration(
                        "customerCache",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)));
    }
}
