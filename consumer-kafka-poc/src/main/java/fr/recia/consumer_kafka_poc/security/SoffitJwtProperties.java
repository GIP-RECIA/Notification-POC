package fr.recia.consumer_kafka_poc.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "soffit.jwt")
@Data
public class SoffitJwtProperties {
    private String signatureKey;
}

