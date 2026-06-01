package e_commerce.search_service.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

// 👇 Đồng bộ 100% sử dụng gói tools.jackson của Jackson 3
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

@Configuration
public class RedisCacheConfig {

  @Bean
  public RedisCacheConfiguration cacheConfiguration() {
    PolymorphicTypeValidator ptv =
        BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build();

    // 1. Tạo ObjectMapper của thế hệ mới
    ObjectMapper objectMapper =
        JsonMapper.builder().activateDefaultTyping(ptv, DefaultTyping.NON_FINAL).build();

    // 2. Sử dụng Serializer thế hệ mới để tương thích hoàn toàn
    JacksonJsonRedisSerializer<Object> serializer =
        new JacksonJsonRedisSerializer<>(objectMapper, Object.class);

    // 3. Trả về cấu hình Cache
    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(60))
        .disableCachingNullValues()
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(serializer));
  }
}
