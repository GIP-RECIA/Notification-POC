package fr.recia.notifications.delayer.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@ConfigurationProperties(prefix = "frequency")
@Configuration
public class FrequencyDuration {
    long duration;
}
