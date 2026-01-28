package fr.recia.consumer_mail_poc.kafka;

import fr.recia.consumer_mail_poc.services.LdapMailQueryService;
import fr.recia.consumer_mail_poc.services.MailSendingService;
import fr.recia.model_kafka_poc.model.RoutedNotification;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.FixedBackOff;

@Component
@Slf4j
public class MailNotificationConsumer {

    private final MailSendingService mailSendingService;
    private final LdapMailQueryService ldapMailQueryService;
    private static final String MAIL_FROM = "notification@mail.fr";

    public MailNotificationConsumer(MailSendingService mailSendingService, LdapMailQueryService ldapMailQueryService){
        this.mailSendingService = mailSendingService;
        this.ldapMailQueryService = ldapMailQueryService;
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, ex) -> new TopicPartition("notifications.replay.mail", record.partition()));
        return new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 0));
    }

    @KafkaListener(topics = "notifications.mail", groupId = "mail-consumer")
    public void consume(RoutedNotification routedNotification) {
        log.debug("Notification mail reçue : {}", routedNotification);
        String mail_to = this.ldapMailQueryService.getPersonMail(routedNotification.getNotification().getHeader().getUserId());
        if(mail_to == null){
            log.error("No valid email address found for {}", routedNotification.getNotification().getHeader().getUserId());
        } else {
            mailSendingService.sendTextMail(MAIL_FROM, mail_to, routedNotification.getNotification().getContent().getTitle(),
                    routedNotification.getNotification().getContent().getMessage());
        }
    }

}

