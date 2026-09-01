package fr.recia.notifications.consumer_web.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "redis-properties")
@Configuration
public class RedisProperties {
    int ttl;
    int scanCount;
}
