package fr.recia.consumer_web.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ldap-group")
@Data
public class LdapGroupRequestProperties {
    private String branchBase;
    private String retrievedAttribute;
    private String filter;
}
