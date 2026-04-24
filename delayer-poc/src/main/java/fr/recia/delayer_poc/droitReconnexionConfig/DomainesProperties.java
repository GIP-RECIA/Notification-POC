package fr.recia.delayer_poc.droitReconnexionConfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "domaines")
@Configuration
public class DomainesProperties {
    private List<String> centre;
}

