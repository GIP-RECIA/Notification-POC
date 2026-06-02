package fr.recia.smtp_proxy.configuration;

import fr.recia.smtp_proxy.handler.NextCloudNotificationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.subethamail.smtp.server.SMTPServer;


@Configuration
public class SMTPServerConfig {
    @Value("${proxy.smtp-port}")
    private int smtpPort;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public SMTPServer smtpServer(NextCloudNotificationHandler nextCloudNotificationHandler) {
        return SMTPServer.port(smtpPort).simpleMessageListener(nextCloudNotificationHandler).build();
    }
}
