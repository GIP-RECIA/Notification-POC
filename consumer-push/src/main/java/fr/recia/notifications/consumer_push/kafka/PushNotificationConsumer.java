package fr.recia.notifications.consumer_push.kafka;

import fr.recia.notifications.consumer_push.configuration.KafkaNotificationProperties;
import fr.recia.notifications.consumer_push.services.FcmService;
import fr.recia.notifications.consumer_push.services.TokenService;
import fr.recia.notifications.model_kafka.model.DeviceTokenSet;
import fr.recia.notifications.model_kafka.model.RoutedNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class PushNotificationConsumer {

    private final KafkaNotificationProperties kafkaNotificationProperties;
    private final TokenService tokenService;
    private final FcmService fcmService;
    private final KafkaTemplate<String, RoutedNotification> kafkaTemplate;

    public PushNotificationConsumer(KafkaNotificationProperties kafkaNotificationProperties, TokenService tokenService, FcmService fcmService, KafkaTemplate<String, RoutedNotification> kafkaTemplate){
        this.kafkaNotificationProperties = kafkaNotificationProperties;
        this.tokenService = tokenService;
        this.fcmService = fcmService;
        this.kafkaTemplate = kafkaTemplate;
    }


    @KafkaListener(topics = "notifications.push")
    public void consume(RoutedNotification routedNotification) {
        log.debug("Push notification received : {}", routedNotification);
        try {
            final String uid = routedNotification.getNotification().getHeader().getUserId();
            DeviceTokenSet tokensForUser = tokenService.getTokens(uid);
            if(tokensForUser.isEmpty()){
                log.info("User {} has configured push notifications but has no token !", uid);
            }
            for(String token : tokensForUser){
                if(token != null){
                    log.debug("Creating notification for token {} for user {}", token, uid);
                    try {
                        fcmService.sendNotification(routedNotification.getNotification(), token);
                    } catch (Exception e){
                        log.warn("Couldn't send notification for token {}", token, e);
                    }
                } else {
                    log.warn("Token for user {} is null ! Can't send notification.", uid);
                }
            }
        } catch (Exception e) {
            int retryCount = routedNotification.getRetryNumber();
            routedNotification.setRetryNumber(++retryCount);
            kafkaTemplate.send(kafkaNotificationProperties.getTopicReplayer(), routedNotification.getNotification().getHeader().getUserId(), routedNotification);
            log.warn("An error occured while sending the notification to firebase", e);
        }
    }
}

