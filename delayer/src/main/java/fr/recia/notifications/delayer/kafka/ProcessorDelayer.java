package fr.recia.notifications.delayer.kafka;

import fr.recia.notifications.delayer.configuration.FrequencyDuration;
import fr.recia.notifications.delayer.configuration.KafkaNotificationProperties;
import fr.recia.notifications.delayer.droitDeconnexionConfig.Region;
import fr.recia.notifications.delayer.services.DroitDeconnexionService;
import fr.recia.notifications.delayer.services.LdapRegionService;
import fr.recia.notifications.delayer.services.LdapBypassDroitDeconnexionService;
import fr.recia.notifications.model_kafka.model.Priority;
import fr.recia.notifications.model_kafka.model.RoutedNotification;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueStore;


import java.time.Duration;

@Slf4j
@Data
public class ProcessorDelayer implements Processor<String, RoutedNotification, String, RoutedNotification> {

    private ProcessorContext<String, RoutedNotification> context;
    private KeyValueStore<String, RoutedNotification> stateStore;
    private final DroitDeconnexionService droitDeconnexionService;
    private final LdapRegionService ldapRegionService;
    private final LdapBypassDroitDeconnexionService ldapBypassDroitDeconnexionService;
    private PunctuatorTopology topology;
    private FrequencyDuration frequencyDuration;
    private KafkaNotificationProperties kafkaNotificationProperties;
    private Duration scanFrequency;

    public ProcessorDelayer(DroitDeconnexionService droitDeconnexionService, LdapRegionService ldapRegionService, LdapBypassDroitDeconnexionService ldapBypassDroitDeconnexionService) {
        this.droitDeconnexionService = droitDeconnexionService;
        this.ldapRegionService = ldapRegionService;
        this.ldapBypassDroitDeconnexionService = ldapBypassDroitDeconnexionService;
    }

    @Override
    public void process(Record<String, RoutedNotification> record) {
        String userId = record.key();
        RoutedNotification notification = record.value();
        long now = record.timestamp();
        long nowReplay = now + Duration.ofMinutes(30).toMillis();
        int replayCount = record.value().getRetryNumber();
        Region region = ldapRegionService.getRegionByUid(userId);
        Priority priority = notification.getNotification().getHeader().getEventHeader().getPriority();

        if (replayCount == 0) {
            if (!droitDeconnexionService.peutRecevoirNotif(userId, now, region)  && !ldapBypassDroitDeconnexionService.canBypass(userId) && priority != Priority.EXTREME) {
                log.debug("The user {} cannot bypass the 'droit à la deconnexion', notification sent with delay.", userId);

                Duration delai = droitDeconnexionService.calculDelai(now, region);
                long deliveryTime = now + delai.toMillis();
                log.trace("Successfully found region for user {}, it is {}", userId, region);

                notification.setDeliveryTime(deliveryTime);
                log.trace("Notification sent at {}", deliveryTime);

                String clePrefix = String.format("%d_%s", deliveryTime, notification.getNotification().getHeader().getNotificationId());
                stateStore.put(clePrefix, notification);
                log.debug("Added notification {} to the state store {}", notification, stateStore);
            } else {
                context.forward(record, getSink(notification));
                log.debug("Notification {} transferred to topic {}", notification, notification.getRoutedTopic());
            }
        }else {
            if(replayCount >= kafkaNotificationProperties.getRetries()){
                log.debug("Notification {} has already been replayed {} times. Putting it to dead letter topic.", notification, replayCount);
                context.forward(record, kafkaNotificationProperties.getSinkDlt());
            }else {
                if (!droitDeconnexionService.peutRecevoirNotif(userId, nowReplay, region)  && !ldapBypassDroitDeconnexionService.canBypass(userId) && priority != Priority.EXTREME) {
                    log.debug("Notification {} has been replayed {} times. Putting it in Store to be replayed.", notification, replayCount);

                    Duration delai = droitDeconnexionService.calculDelai(now, region);
                    long deliveryTime = now + delai.toMillis();

                    String clePrefix = String.format("%d_%s", deliveryTime, notification.getNotification().getHeader().getNotificationId());
                    notification.setDeliveryTime(deliveryTime);
                    stateStore.put(clePrefix, notification);
                }else {
                    log.debug("Notification {} added to the store {} for replay. Replayed {} times",notification, stateStore, notification.getRetryNumber());
                    notification.setDeliveryTime(nowReplay);
                    String clePrefix = String.format("%d_%s", nowReplay, notification.getNotification().getHeader().getNotificationId());
                    stateStore.put(clePrefix, notification);
                }
            }
        }
    }

    @Override
    public void init(ProcessorContext<String, RoutedNotification> context) {
        this.context = context;
        this.stateStore = context.getStateStore(kafkaNotificationProperties.getStore());

        context.schedule(scanFrequency,
                PunctuationType.WALL_CLOCK_TIME,
                timestamp -> {
                    String from = String.format("%d", 0L);
                    String to = String.format("%d", timestamp) + "_\uFFFF";
                    try (var iterator = stateStore.range(from, to)) {
                        while (iterator.hasNext()) {
                            var entry = iterator.next();
                            if (entry.value.getDeliveryTime() <= timestamp){
                                context.forward(new Record<>(entry.value.getNotification().getHeader().getUserId(), entry.value, timestamp), getSink(entry.value));
                                stateStore.delete(entry.key);
                                log.debug("Une notification a été supprimée du store");
                            }
                        }
                    }
                }
                );
    }

    public String getSink(RoutedNotification notification) {
        String sink = null;
        if (notification.getRoutedTopic().equals(kafkaNotificationProperties.getWeb())) {
            sink = kafkaNotificationProperties.getSinkWeb();
        } else if (notification.getRoutedTopic().equals(kafkaNotificationProperties.getMail())) {
            sink = kafkaNotificationProperties.getSinkMail();
        } else if (notification.getRoutedTopic().equals(kafkaNotificationProperties.getPush())) {
            sink = kafkaNotificationProperties.getSinkPush();
        }
        return sink;
    }
}
