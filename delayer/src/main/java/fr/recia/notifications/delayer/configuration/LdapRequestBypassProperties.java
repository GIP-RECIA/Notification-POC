package fr.recia.notifications.delayer.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "bypass-request")
@Configuration
public class LdapRequestBypassProperties {
    private String branchBase;
    private String retrievedAttribute;
    private String filter;
}
