package fr.recia.routing_kafka_poc.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.recia.model_kafka_poc.model.ChannelPreferences;
import fr.recia.model_kafka_poc.model.Notification;
import fr.recia.model_kafka_poc.model.NotificationSerde;
import fr.recia.model_kafka_poc.model.PreferencesNotification;
import fr.recia.model_kafka_poc.model.Priority;
import fr.recia.model_kafka_poc.model.RoutedNotification;
import fr.recia.model_kafka_poc.model.RoutedNotificationSerde;
import fr.recia.model_kafka_poc.model.ServicePreferences;
import fr.recia.model_kafka_poc.model.UserPreferences;
import fr.recia.model_kafka_poc.model.UserPreferencesSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class BasicRouter {

    private static final String WS_TOPIC = "notifications.web";
    private static final String MAIL_TOPIC = "notifications.mail";
    private static final String PUSH_TOPIC = "notifications.push";

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Serde<Notification> notificationSerde(ObjectMapper objectMapper){
        return new NotificationSerde(objectMapper);
    }

    @Bean
    public Serde<UserPreferences> prefsSerde(ObjectMapper objectMapper){
        return new UserPreferencesSerde(objectMapper);
    }

    @Bean
    public Serde<RoutedNotification> routedNotificationSerde(ObjectMapper objectMapper){
        return new RoutedNotificationSerde(objectMapper);
    }

    private Set<String> channelsFromPrefs(ChannelPreferences prefs) {
        Set<String> channels = new HashSet<>();
        if (prefs.isWs()) {
            channels.add(WS_TOPIC);
        }
        if (prefs.isMail()) {
            channels.add(MAIL_TOPIC);
        }
        if (prefs.isPush()) {
            channels.add(PUSH_TOPIC);
        }
        return channels;
    }


    private Set<String> resolveChannels(Notification notif, UserPreferences prefs) {
        log.trace("Resolving output channels for notification {} and preferences {}", notif, prefs);
        // 1. Aucune préférence -> comportement par défaut
        if (prefs == null) {
            log.trace("No preferences are set -> putting notification {} in default topic", notif);
            return Set.of(WS_TOPIC);
        }

        String service = notif.getHeader().getEventHeader().getService();
        Priority priority = notif.getHeader().getEventHeader().getPriority();

        // 2. Règle par service
        if(prefs.getServices() != null){
            ServicePreferences sp = prefs.getServices().get(service);
            if (sp != null) {
                if (!sp.isEnabled()) {
                    // Service désactivé --> Pas de notif à envoyer
                    log.trace("Service is disabled -> notification {} will not be sent", notif);
                    return Set.of();
                }
                // Si le service est activé, on regarde s'il surcharge la configuration globale
                // Si non, on fait en fonction de la configuration globale
                if(!sp.isOverride()){
                    Set<String> channelsToSend = channelsFromPrefs(prefs.getGlobal());
                    log.trace("Service is enabled but does not override global configuration -> notification {} will be sent to topics {}", notif, channelsToSend);
                    return channelsToSend;
                }
                // Si oui, on regarde ses règles en fonction de la priorité de la notification
                ChannelPreferences cp = sp.getPriorities().get(priority);
                if (cp != null) {
                    Set<String> channelsToSend = channelsFromPrefs(cp);
                    log.trace("Service is enabled and overrides global configuration -> notification {} will be sent to topics {}", notif, channelsToSend);
                    return channelsToSend;
                }
            }
        }

        // 3. Si on a pas de préférences par service alors on regarde les préférences globales
        Set<String> channelsToSend = channelsFromPrefs(prefs.getGlobal());
        log.trace("Service is unknown to user preferences -> using global configuration notification {} will be sent to topics {}", notif, channelsToSend);
        return channelsToSend;
    }


    @Bean
    public KStream<String, Notification> basicRouting(StreamsBuilder builder, Serde<Notification> notificationSerde, Serde<UserPreferences> prefsSerde, Serde<RoutedNotification> routedNotificationSerde) {

        // Stream pour récupérer les notifications
        KStream<String, Notification> input = builder.stream("events.expanded", Consumed.with(Serdes.String(), notificationSerde));
        input.peek((key, value) -> {
            log.trace("Nouvel event : key={}, value={}", key, value);
        });

        // KTable pour récupérer les préferences utilisateur
        KTable<String, UserPreferences> preferences = builder.table("user.preferences-v2", Consumed.with(Serdes.String(), prefsSerde));
        preferences.toStream().peek((key, prefs) -> {
            log.trace("Nouvelle préférence : key={}, value={}", key, prefs);
        });

        // Jointure entre les notifications et les prefs utilisateur
        KStream<String, PreferencesNotification> enriched = input.leftJoin(preferences, (event, prefs) -> new PreferencesNotification(event, prefs));
        enriched.peek((key, notif) -> {
            log.trace("Création d'une notification préférenciée : key={}, value={}", key, notif);
        });

        // Logique de routage
        enriched.flatMap((userId, enrichedNotif) -> {
            // Récupération des noms des channels dans lesquels il faut déposer la notif
            Set<String> channels = resolveChannels(enrichedNotif.getNotification(), enrichedNotif.getPreferences());
            // On retourne une liste de KeyValue, avec comme valeur une RoutedNotification qui à l'information du topic dans laquelle elle doit être déposée
            return channels.stream()
                    .map(channel -> KeyValue.pair(enrichedNotif.getNotification().getHeader().getUserId(), new RoutedNotification(enrichedNotif.getNotification(), channel)))
                    .toList();
        //.to attend en retour un TopicNameExtractor qui va retourner le nom du topic dans lequel on va mettre la notif et un Produced pour la sérialisation
        }).to(
                (key, value, ctx) -> value.getRoutedTopic(),
                Produced.with(Serdes.String(), routedNotificationSerde)
        );

        return input;
    }
}
