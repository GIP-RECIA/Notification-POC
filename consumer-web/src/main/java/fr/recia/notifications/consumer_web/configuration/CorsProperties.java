package fr.recia.notifications.consumer_web.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "cors-configuration")
@Configuration
public class CorsProperties {
    List<String> allowedOrigins;
}
