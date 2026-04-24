package fr.recia.consumer_push_poc.kafka;

import fr.recia.consumer_push_poc.services.FcmService;
import fr.recia.consumer_push_poc.services.TokenService;
import fr.recia.model_kafka_poc.model.RoutedNotification;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Set;

@Component
@Slf4j
public class PushNotificationConsumer {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private FcmService fcmService;

    KafkaTemplate<String, RoutedNotification> kafkaTemplate;

    @KafkaListener(topics = "ok.push", groupId = "push-consumer")
    public void consume(RoutedNotification routedNotification) {
        log.debug("Notification push reçue : {}", routedNotification);
        try {
            final String uid = routedNotification.getNotification().getHeader().getUserId();
            Set<String> tokensForUser = tokenService.getTokens(uid);
            if(tokensForUser.isEmpty()){
                log.info("User {} has configured push notifications but has no token !", uid);
            }
            for(String token : tokensForUser){
                fcmService.sendNotification(routedNotification.getNotification(), token);
            }
        } catch (Exception e) {
            int retryCount = routedNotification.getRetryNumber();
            routedNotification.setRetryNumber(++retryCount);
            kafkaTemplate.send("notifications.replayer", routedNotification.getNotification().getHeader().getUserId(), routedNotification);
            log.warn("An error occured while sending the notification to firebase", e);
        }
    }

}

