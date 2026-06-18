package fr.recia.notifications.consumer_push.kafka;

import fr.recia.notifications.consumer_push.services.FcmService;
import fr.recia.notifications.consumer_push.services.TokenService;
import fr.recia.notifications.model_kafka.model.RoutedNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class PushNotificationConsumer {

    private final TokenService tokenService;
    private final FcmService fcmService;

    public PushNotificationConsumer(TokenService tokenService, FcmService fcmService){
        this.tokenService = tokenService;
        this.fcmService = fcmService;
    }

    KafkaTemplate<String, RoutedNotification> kafkaTemplate;

    private final static String TOPIC_OUT_REPLAY = "notifications.replayer";

    @KafkaListener(topics = "notifications.push")
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
            kafkaTemplate.send(TOPIC_OUT_REPLAY, routedNotification.getNotification().getHeader().getUserId(), routedNotification);
            log.warn("An error occured while sending the notification to firebase", e);
        }
    }
}

