package fr.recia.smtp_proxy.configuration;

import fr.recia.smtp_proxy.handler.MailNotificationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.subethamail.smtp.server.SMTPServer;

@Configuration
public class SMTPServerConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public SMTPServer smtpServer(SMTPServerProperties smtpServerProperties, MailNotificationHandler mailNotificationHandler) {
        return SMTPServer.port(smtpServerProperties.getPort()).simpleMessageListener(mailNotificationHandler).build();
    }

}
