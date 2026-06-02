package fr.recia.smtp_proxy.handler;

import fr.recia.event_rest_client_kafka.HttpNotificationClient;
import fr.recia.model_kafka.model.Channel;
import fr.recia.model_kafka.model.Priority;
import fr.recia.model_kafka.model.TargetType;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.subethamail.smtp.helper.SimpleMessageListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class NextCloudNotificationHandler implements SimpleMessageListener {

    private final HttpNotificationClient notificationClient;
    private static final Pattern URL_PATTERN = Pattern.compile("(https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean accept(String from, String mail) {
        return mail != null && mail.contains("@");
    }

    @Override
    public void deliver(String from, String dest, InputStream data) {
        log.info("[SMTP-PROXY] Nouveau mail intercepté ! De : {} | Pour : {}", from, dest);
        log.info("[SMTP-PROXY] Nouveau mail intercepté ! Data : {}", new BufferedReader(new InputStreamReader(data)).lines().collect(Collectors.joining("\n")));

        try {
            Session session = Session.getDefaultInstance(new Properties());
            MimeMessage mimeMessage = new MimeMessage(session, data);

            String title = mimeMessage.getSubject();
            String message = extractPlainBody(mimeMessage);
            String link = extractUrl(message);

            log.info("Les données ont été récupérée avec succés. La notification est pour {}, le contenu du message est : {} et le lien est : {}", dest, message, link);

            notificationClient.sendNotification(
                    title,
                    message.trim(),
                    link,
                    dest,
                    List.of(Channel.WEB),
                    Priority.NORMAL,
                    TargetType.EMAIL
            );
        } catch (Exception e) {
            log.error("erreur : Les données n'ont pas été interceptées");
        }
    }

    private String extractPlainBody(MimeMessage mimeMessage) throws Exception {
        if (mimeMessage.isMimeType("text/plain")) {
            return mimeMessage.getContent().toString();
        } else if (mimeMessage.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) mimeMessage.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    return bodyPart.getContent().toString();
                }
            }
        }
        return "";
    }

    private String extractUrl(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        Matcher matcher = URL_PATTERN.matcher(text);
        return matcher.find() ? matcher.group(1) : "";
    }
}
