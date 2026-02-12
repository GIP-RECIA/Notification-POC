package fr.recia.consumer_push_poc.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cas")
@Data
public class CASProperties {
    private String serviceUrl;
    private String validateUrl;
}
