package fr.recia.notifications.smtp_proxy.configuration;

import fr.recia.notifications.smtp_proxy.handler.MailNotificationHandler;
import fr.recia.notifications.smtp_proxy.security.SMTPCredentialsValidator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.subethamail.smtp.auth.EasyAuthenticationHandlerFactory;
import org.subethamail.smtp.server.SMTPServer;

import javax.net.ssl.SSLContext;

@Configuration
public class SMTPServerConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public SMTPServer smtpServer(SMTPServerProperties smtpServerProperties,
                                 MailNotificationHandler mailNotificationHandler,
                                 SmtpProperties smtpProperties,
                                 SMTPCredentialsValidator smtpCredentialsValidator,
                                 ObjectProvider<SSLContext> sslContext) {
        SMTPServer.Builder builder = SMTPServer.port(smtpServerProperties.getPort()).simpleMessageListener(mailNotificationHandler);

        if (smtpProperties.isRequireAuth()) {
            builder.authenticationHandlerFactory(new EasyAuthenticationHandlerFactory(smtpCredentialsValidator));
        }

        if (smtpProperties.isRequireTls()) {
            builder.startTlsSocketFactory(sslContext.getObject()).requireTLS();
        }

        return builder.build();
    }

}
