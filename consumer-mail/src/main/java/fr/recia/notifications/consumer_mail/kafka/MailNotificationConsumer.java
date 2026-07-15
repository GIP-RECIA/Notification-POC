package fr.recia.notifications.consumer_mail.kafka;

import fr.recia.notifications.consumer_mail.services.LdapMailQueryService;
import fr.recia.notifications.consumer_mail.services.MailSendingService;
import fr.recia.notifications.model_kafka.model.RoutedNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class MailNotificationConsumer {

    private final MailSendingService mailSendingService;
    private final LdapMailQueryService ldapMailQueryService;
    private static final String MAIL_FROM = "notification@mail.fr";
    private final static String TOPIC_OUT_REPLAY = "notifications.replayer";
    private final KafkaTemplate<String, RoutedNotification> kafkaTemplate;

    public MailNotificationConsumer(MailSendingService mailSendingService, LdapMailQueryService ldapMailQueryService, KafkaTemplate<String, RoutedNotification> kafkaTemplate){
        this.mailSendingService = mailSendingService;
        this.ldapMailQueryService = ldapMailQueryService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "notifications.mail")
    public void consume(RoutedNotification routedNotification) {
        try {
            log.debug("Email notification received : {}", routedNotification);
            Optional<String> mailTo = this.ldapMailQueryService.getPersonMail(routedNotification.getNotification().getHeader().getUserId());
            if(mailTo.isEmpty()){
                log.error("No valid email address found for {}", routedNotification.getNotification().getHeader().getUserId());
            } else {
                mailSendingService.sendTextMail(MAIL_FROM, mailTo.get(), routedNotification.getNotification().getContent().getTitle(),
                        routedNotification.getNotification().getContent().getMessage());
            }
        } catch (Exception e) {
            log.warn("Unable to send a notification {} via mail, forwarded to replayer", routedNotification);
            int retryCount = routedNotification.getRetryNumber();
            routedNotification.setRetryNumber(++retryCount);
            kafkaTemplate.send(TOPIC_OUT_REPLAY, routedNotification.getNotification().getHeader().getUserId(), routedNotification);
        }
    }
}