package fr.recia.consumer_mail_poc.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "request")
@Data
public class LdapRequestProperties {
    private String branchBase;
    private String retrievedAttribute;
    private String filter;
}
