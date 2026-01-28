package fr.recia.consumer_mail_poc.services;

import fr.recia.consumer_mail_poc.configuration.MailProperties;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@Slf4j
public class MailSendingService {

    private final Session mailSession;

    public MailSendingService(MailProperties mailProperties) {
        this.mailSession = createMailSession(mailProperties);
    }

    private Session createMailSession(MailProperties mailProperties){
        log.trace("Creating a mail session with properties {}", mailProperties);
        Properties properties = new Properties();
        properties.put("mail.smtp.host", mailProperties.getHost());
        properties.put("mail.smtp.port", mailProperties.getPort());
        properties.put("mail.smtp.auth", mailProperties.isAuth());
        properties.put("mail.smtp.starttls.enable", mailProperties.isStarttls());
        return Session.getInstance(properties);
    }

    public void sendTextMail(String from, String to, String subject, String body) {
        try {
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            log.trace("Sending mail {} {} from {} to {}", subject, body, from, to);
            Transport.send(message);
        } catch (Exception e) {
            log.error("Unable to send mail to {}", to, e);
            throw new RuntimeException("Erreur lors de l'envoi du mail", e);
        }
    }
}
