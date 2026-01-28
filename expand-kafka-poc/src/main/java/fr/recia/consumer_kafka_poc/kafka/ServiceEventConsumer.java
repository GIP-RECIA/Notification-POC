package fr.recia.consumer_kafka_poc.kafka;

import fr.recia.consumer_kafka_poc.service.LdapGroupService;
import fr.recia.model_kafka_poc.model.Notification;
import fr.recia.model_kafka_poc.model.NotificationHeader;
import fr.recia.model_kafka_poc.model.ServiceEvent;
import fr.recia.model_kafka_poc.model.TargetType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
public class ServiceEventConsumer {

    private final static String TOPIC_OUT = "events.expanded";
    private final static String TOPIC_IN = "events.requested";
    private final static String GROUP_ID = "expanded-consumer";

    private final KafkaTemplate<String, Notification> kafkaTemplate;
    private final LdapGroupService ldapGroupService;

    public ServiceEventConsumer(KafkaTemplate<String, Notification> kafkaTemplate, LdapGroupService ldapGroupService) {
        this.kafkaTemplate = kafkaTemplate;
        this.ldapGroupService = ldapGroupService;
    }

    private void sendToKafkaForUserList(List<String> ids, ServiceEvent serviceEvent){
        for (String userId : ids) {
            Notification notification = new Notification(new NotificationHeader(UUID.randomUUID().toString(), userId, serviceEvent.getHeader()), serviceEvent.getContent());
            kafkaTemplate.send(TOPIC_OUT, userId, notification);
            log.trace("Nouvelle notification {} déposée dans le topic {}", notification, TOPIC_OUT);
        }
    }

    @KafkaListener(topics = TOPIC_IN, groupId = GROUP_ID)
    public void consume(ServiceEvent serviceEvent) {
        log.trace("ServiceEvent {} reçu en entrée depuis le topic {}", serviceEvent, TOPIC_IN);
        // Si c'est une liste de user le traitement est facile, on créé une notif par user
        if(serviceEvent.getTarget().getType().equals(TargetType.USER)){
            log.trace("Expansion de l'event par utilisateurs");
            sendToKafkaForUserList(serviceEvent.getTarget().getIds(), serviceEvent);
        }
        // Si c'est une liste de groupes, alors il faut d'abord trouver tous les users de tous les groupes, puis on créé une notif par user
        else if(serviceEvent.getTarget().getType().equals(TargetType.GROUP)){
            log.trace("Expansion de l'event par groupes");
            Set<String> uniqueUidsOfUsersInGroup = new HashSet<>();
            for(String group : serviceEvent.getTarget().getIds()){
                List<String> listOfUidsOfUsersInGroup = ldapGroupService.getGroupMembers(group);
                log.trace("Ajout de tous les utilisateurs {} pour le groupe {}", listOfUidsOfUsersInGroup, group);
                uniqueUidsOfUsersInGroup.addAll(listOfUidsOfUsersInGroup);
            }
            sendToKafkaForUserList(new ArrayList<>(uniqueUidsOfUsersInGroup), serviceEvent);
        } else {
            log.error("Type de target pour l'event {} inconnu !", serviceEvent);
            throw new RuntimeException("Unknown target type !");
        }
    }

}

