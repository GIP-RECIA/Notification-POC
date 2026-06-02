package fr.recia.expander.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ldap-mail")
public class LdapMailRequestProperties {
    private String branchBase;
    private String retrievedAttribute;
    private String filter;
}
