package fr.recia.notifications.smtp_proxy.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SMTPRoutingRule {
    private String header;
    private String regex;
    private String processor;
    private String service;
    private String apiKey;
}
