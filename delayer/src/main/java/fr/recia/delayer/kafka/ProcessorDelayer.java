package fr.recia.delayer.kafka;

import fr.recia.delayer.configuration.FrequencyDuration;
import fr.recia.delayer.droitReconnexionConfig.Region;
import fr.recia.delayer.services.DroitDeconnexionService;
import fr.recia.delayer.services.LdapRegionService;
import fr.recia.model_kafka.model.RoutedNotification;
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
    private PunctuatorTopology topology;
    private FrequencyDuration frequencyDuration;

    private final static String SINK_WEB = "sink.web";
    private final static String SINK_MAIL = "sink.mail";
    private final static String SINK_PUSH = "sink.push";

    private final static String ROUTED_TOPIC_WEB = "notifications.web";
    private final static String ROUTED_TOPIC_MAIL = "notifications.mail";
    private final static String ROUTED_TOPIC_PUSH = "notifications.push";

    private final static String STORE = "delayer-store";

    private final static String SINK_DLT = "sink.dlt";
    private final static int NUM_RETRIES = 5;

    private Duration scanFrequency;

    public ProcessorDelayer(DroitDeconnexionService droitDeconnexionService, LdapRegionService ldapRegionService) {
        this.droitDeconnexionService = droitDeconnexionService;
        this.ldapRegionService = ldapRegionService;
    }

    @Override
    public void process(Record<String, RoutedNotification> record) {
        String userId = record.key();
        RoutedNotification notification = record.value();
        long now = record.timestamp();
        long nowReplay = now + Duration.ofMinutes(30).toMillis();
        int replayCount = record.value().getRetryNumber();
        Region region = ldapRegionService.getRegionByUid(userId);

        if (replayCount == 0) {
            if (!droitDeconnexionService.peutRecevoirNotif(userId, now, region)) {
                Duration delai = droitDeconnexionService.calculDelai(now, region);
                long deliveryTime = now + delai.toMillis();
                log.trace("La région de l'utilisateur {} a bien été trouvée, c'est la région {}", userId, region);

                notification.setDeliveryTime(deliveryTime);
                log.trace("La notification a été envoyée à {}", deliveryTime);

                String clePrefix = String.format("%d_%s", deliveryTime, notification.getNotification().getHeader().getNotificationId());
                stateStore.put(clePrefix, notification);
                log.debug("Une notification {} envoyée dans le Store {}", notification, stateStore);
            } else {
                context.forward(record, getSink(notification));
                log.debug("Une notification {} a été transférée vers le topic {}", notification, notification.getRoutedTopic());
            }
        }else {
            if(replayCount >= NUM_RETRIES){
                log.debug("Notification {} has already been replayed {} times. Putting it to dead letter topic.", notification, replayCount);
                context.forward(record, SINK_DLT);
            }else {
                if (!droitDeconnexionService.peutRecevoirNotif(userId, nowReplay, region)) {
                    log.debug("Notification {} has been replayed {} times. Putting it in Store to be replayed ", notification, replayCount);

                    Duration delai = droitDeconnexionService.calculDelai(now, region);
                    long deliveryTime = now + delai.toMillis();

                    String clePrefix = String.format("%d_%s", deliveryTime, notification.getNotification().getHeader().getNotificationId());
                    notification.setDeliveryTime(deliveryTime);
                    stateStore.put(clePrefix, notification);
                }else {
                    log.debug("Une notification {} à rejouer a été envoyée dans le store {}. Elle a été rejouée {} fois",notification, stateStore, notification.getRetryNumber());

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
        this.stateStore = context.getStateStore(STORE);

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
        String sink = switch (notification.getRoutedTopic()) {
            case ROUTED_TOPIC_WEB -> SINK_WEB;
            case ROUTED_TOPIC_MAIL -> SINK_MAIL;
            case ROUTED_TOPIC_PUSH -> SINK_PUSH;
            default -> {
                yield null;
            }
        };
        return sink;
    }
}
