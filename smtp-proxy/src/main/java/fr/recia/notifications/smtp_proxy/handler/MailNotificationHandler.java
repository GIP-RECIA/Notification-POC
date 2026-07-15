package fr.recia.notifications.smtp_proxy.handler;

import fr.recia.notifications.smtp_proxy.configuration.SMTPRoutingProperties;
import fr.recia.notifications.smtp_proxy.configuration.SMTPRoutingRule;
import fr.recia.notifications.smtp_proxy.registry.ProcessorRegistry;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.subethamail.smtp.helper.SimpleMessageListener;

import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailNotificationHandler implements SimpleMessageListener {

    private final ProcessorRegistry processorRegistry;
    private final SMTPRoutingProperties smtpRoutingProperties;
    private final JavaMailSender javaMailSender;

    @Override
    public boolean accept(String from, String mail) {
        return mail != null && mail.contains("@");
    }

    @Override
    public void deliver(String from, String dest, InputStream data) {
        log.info("[SMTP-PROXY] New mail intercepted ! From: {} | To: {}", from, dest);
        Session session = Session.getDefaultInstance(new Properties());
        try {
            MimeMessage mimeMessage = new MimeMessage(session, data);

            for (int i=0 ; i < smtpRoutingProperties.getRules().size(); i++){

                SMTPRoutingRule rule = smtpRoutingProperties.getRules().get(i);
                String header = mimeMessage.getHeader(rule.getHeader())[0];
                Pattern pattern = Pattern.compile(rule.getRegex());
                Matcher matcher = pattern.matcher(header);

                if (matcher.find()){
                    String processor = rule.getProcessor();
                    processorRegistry.get(processor).process(from, dest, mimeMessage);
                    return;
                }
            }

            log.info("No matching rule found. Forwarding email to original recipient : {}", dest);
            log.debug("Data du message associé : {}", mimeMessage.getContent());
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            log.error("An error occured while intercepting an email", e);
        }
    }

}
