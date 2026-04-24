package fr.recia.delayer_poc.droitReconnexionConfig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ToString
@Setter
@Getter
@ConfigurationProperties(prefix = "domaines")
@Configuration
public class DomainesProperties {
    private List<String> centre;
}

