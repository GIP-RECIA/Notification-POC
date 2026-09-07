package fr.recia.notifications.smtp_proxy.security;

import fr.recia.notifications.smtp_proxy.configuration.SmtpProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.auth.LoginFailedException;
import org.subethamail.smtp.auth.UsernamePasswordValidator;

@Slf4j
@Component
public class SMTPCredentialsValidator implements UsernamePasswordValidator {

    private final SmtpProperties smtpProperties;

    public SMTPCredentialsValidator(SmtpProperties smtpProperties) {
        this.smtpProperties = smtpProperties;
    }

    @Override
    public void login(String username, String password, MessageContext context) throws LoginFailedException {
        String expectedPassword = smtpProperties.getCredentials().get(username);
        if (expectedPassword == null || !expectedPassword.equals(password)) {
            log.warn("Invalid username or password");
            throw new LoginFailedException("Invalid username or password");
        }
        log.debug("Authentication successful for user : {}", username);
    }
}
