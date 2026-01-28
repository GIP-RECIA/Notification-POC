package fr.recia.producer_api_poc.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "security")
@Data
public class ApiKeyProperties {
    private Map<String, String> apiKeys = new HashMap<>();
    private List<String> allowedIps = new ArrayList<>();
}
