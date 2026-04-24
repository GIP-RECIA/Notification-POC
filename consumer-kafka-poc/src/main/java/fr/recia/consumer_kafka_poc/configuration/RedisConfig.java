package fr.recia.consumer_kafka_poc.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.recia.model_kafka_poc.model.StoredNotification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, StoredNotification> notificationRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, StoredNotification> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        // Nouvelle version de GenericJacksonJsonRedisSerializer -> obligation de setup son objectMapper customisé
        BasicPolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder().allowIfSubType("fr.recia").allowIfSubType("java").build();
        ObjectMapper objectMapper = JsonMapper.builder().activateDefaultTyping(ptv, DefaultTyping.NON_FINAL_AND_ENUMS, JsonTypeInfo.As.PROPERTY).build();
        template.setValueSerializer(new GenericJacksonJsonRedisSerializer(objectMapper));
        return template;
    }

    @Bean
    public RedisTemplate<String, String> userIndexRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

}

