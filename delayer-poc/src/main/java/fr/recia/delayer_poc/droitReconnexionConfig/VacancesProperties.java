package fr.recia.delayer_poc.droitReconnexionConfig;

import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@ToString
@Setter
@Getter
@ConfigurationProperties(prefix = "vacances")
@Configuration
public class VacancesProperties {
    private CalendrierRegion centre;
    private CalendrierRegion reunion;
}
