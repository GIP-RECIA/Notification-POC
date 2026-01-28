package fr.recia.routing_kafka_poc.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "streams")
public class KafkaStreamProperties {
    private String bootstrapServers;
    private String applicationId;
    private int replicationFactor;
    private String securityProtocol;
    private String saslMechanism;
    private String jaasConfig;
}

