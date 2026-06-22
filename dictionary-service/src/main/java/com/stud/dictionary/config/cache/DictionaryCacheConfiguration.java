package com.stud.dictionary.config.cache;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stud.dictionary.web.dto.DictionaryDtos.CurrencyResponse;
import com.stud.dictionary.web.dto.DictionaryDtos.FactionResponse;
import com.stud.dictionary.web.dto.DictionaryDtos.OrderCategoryResponse;
import com.stud.dictionary.web.dto.DictionaryDtos.PlanetResponse;
import com.stud.dictionary.web.dto.DictionaryDtos.SectorResponse;
import com.stud.dictionary.web.dto.DictionaryDtos.SkillResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.util.List;
import java.util.Map;

import static com.stud.dictionary.config.cache.DictionaryCacheNames.CURRENCIES;
import static com.stud.dictionary.config.cache.DictionaryCacheNames.FACTIONS;
import static com.stud.dictionary.config.cache.DictionaryCacheNames.ORDER_CATEGORIES;
import static com.stud.dictionary.config.cache.DictionaryCacheNames.PLANETS;
import static com.stud.dictionary.config.cache.DictionaryCacheNames.SECTORS;
import static com.stud.dictionary.config.cache.DictionaryCacheNames.SKILLS;

@Configuration(proxyBeanMethods = false)
@EnableCaching
@EnableConfigurationProperties(DictionaryCacheProperties.class)
public class DictionaryCacheConfiguration implements CachingConfigurer {

    @Bean
    RedisCacheManagerBuilderCustomizer dictionaryCacheManagerCustomizer(
            DictionaryCacheProperties properties,
            ObjectMapper objectMapper
    ) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
                FACTIONS, listCacheConfiguration(properties, objectMapper, FactionResponse.class),
                SECTORS, listCacheConfiguration(properties, objectMapper, SectorResponse.class),
                PLANETS, listCacheConfiguration(properties, objectMapper, PlanetResponse.class),
                ORDER_CATEGORIES, listCacheConfiguration(properties, objectMapper, OrderCategoryResponse.class),
                CURRENCIES, listCacheConfiguration(properties, objectMapper, CurrencyResponse.class),
                SKILLS, listCacheConfiguration(properties, objectMapper, SkillResponse.class)
        );

        return builder -> builder
                .withInitialCacheConfigurations(cacheConfigurations)
                .disableCreateOnMissingCache()
                .transactionAware();
    }

    private RedisCacheConfiguration listCacheConfiguration(
            DictionaryCacheProperties properties,
            ObjectMapper objectMapper,
            Class<?> elementType
    ) {
        JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, elementType);
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, listType);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(properties.getTtl())
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> properties.getKeyPrefix() + cacheName + "::")
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        serializer
                ));
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new LoggingCacheErrorHandler();
    }

    private static final class LoggingCacheErrorHandler implements CacheErrorHandler {

        private static final Logger log = LoggerFactory.getLogger(LoggingCacheErrorHandler.class);

        @Override
        public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
            log.warn("Cache read failed for cache '{}' and key '{}'; using the database",
                    cache.getName(), key, exception);
        }

        @Override
        public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
            log.warn("Cache write failed for cache '{}' and key '{}'", cache.getName(), key, exception);
        }

        @Override
        public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
            log.warn("Cache eviction failed for cache '{}' and key '{}'", cache.getName(), key, exception);
        }

        @Override
        public void handleCacheClearError(RuntimeException exception, Cache cache) {
            log.warn("Cache clear failed for cache '{}'", cache.getName(), exception);
        }
    }
}
