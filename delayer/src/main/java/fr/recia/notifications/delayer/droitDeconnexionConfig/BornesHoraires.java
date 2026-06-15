package fr.recia.notifications.delayer.droitDeconnexionConfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bornes")
@Data
public class BornesHoraires {
    int inf;
    int sup;
}
