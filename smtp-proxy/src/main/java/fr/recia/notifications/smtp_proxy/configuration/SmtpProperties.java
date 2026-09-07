package fr.recia.notifications.smtp_proxy.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "smtp")
@Configuration
public class SmtpProperties {
    private boolean requireAuth;
    private Map<String, String> credentials;
    private boolean requireTls;
    private String keystorePath;
    private String keystorePassword;
    private String keyPassword;
}
