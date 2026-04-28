package fr.recia.delayer.droitReconnexionConfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "vacances")
@Configuration
public class VacancesProperties {
    private CalendrierRegion centre;
    private CalendrierRegion reunion;
}
