package fr.recia.notifications.delayer.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "domaines-request")
@Data
public class LdapRequestDomainesProperties {
    private String branchBase;
    private String retrievedAttribute;
    private String filter;
}
