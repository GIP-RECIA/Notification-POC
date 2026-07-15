package fr.recia.notifications.smtp_proxy.processor.impl;

import fr.recia.notifications.event_rest_client_kafka.HttpNotificationClient;
import fr.recia.notifications.model_kafka.model.Channel;
import fr.recia.notifications.model_kafka.model.Priority;
import fr.recia.notifications.model_kafka.model.TargetType;
import fr.recia.notifications.smtp_proxy.processor.MailProcessor;
import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class NextcloudProcessor implements MailProcessor {

    private static final Pattern URL_PATTERN = Pattern.compile("(https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])", Pattern.CASE_INSENSITIVE);
    private final HttpNotificationClient httpNotificationClient;

    public NextcloudProcessor(String apiUrl, String service, String apiKey){
        this.httpNotificationClient = new HttpNotificationClient(apiUrl, service, apiKey);
    }

    @Override
    public void process(String from, String dest, MimeMessage mimeMessage) throws Exception {

        String title = mimeMessage.getSubject();
        String message = extractPlainBody(mimeMessage);
        String link = extractUrl(message);

        log.info("Data succesfully retrieved. Recipient {}, Message content : {} , Link : {}", dest, message, link);

        httpNotificationClient.sendNotification(
                title,
                message.trim(),
                link,
                dest,
                List.of(Channel.WEB, Channel.MAIL, Channel.PUSH),
                Priority.NORMAL,
                TargetType.EMAIL
        );
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
