package fr.recia.smtp_proxy.configuration;

import fr.recia.smtp_proxy.handler.NextCloudNotificationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.subethamail.smtp.server.SMTPServer;

@Configuration
public class SMTPServerConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public SMTPServer smtpServer(SMTPServerProperties smtpServerProperties, NextCloudNotificationHandler nextCloudNotificationHandler) {
        return SMTPServer.port(smtpServerProperties.getPort()).simpleMessageListener(nextCloudNotificationHandler).build();
    }

}
