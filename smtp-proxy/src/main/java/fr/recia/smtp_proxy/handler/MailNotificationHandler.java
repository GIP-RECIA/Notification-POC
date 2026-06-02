package fr.recia.smtp_proxy.handler;

import fr.recia.smtp_proxy.registry.ProcessorRegistry;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.subethamail.smtp.helper.SimpleMessageListener;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailNotificationHandler implements SimpleMessageListener {

    private final ProcessorRegistry processorRegistry;

    @Override
    public boolean accept(String from, String mail) {
        return mail != null && mail.contains("@");
    }

    @Override
    public void deliver(String from, String dest, InputStream data) {
        log.info("[SMTP-PROXY] Nouveau mail intercepté ! De : {} | Pour : {}", from, dest);
        Session session = Session.getDefaultInstance(new Properties());
        try {
            MimeMessage mimeMessage = new MimeMessage(session, data);
            // TODO : logique pour choisir le bon processor en fonction des règles de routage
            String processor = "NextcloudProcessor";
            log.info("Debug From - {}", Arrays.toString(mimeMessage.getHeader("From")));
            log.info("Debug Received - {}", Arrays.toString(mimeMessage.getHeader("Received")));
            processorRegistry.get(processor).process(from, dest, mimeMessage);
        } catch (Exception e) {
            log.error("erreur : Les données n'ont pas été interceptées");
        }
    }

}
