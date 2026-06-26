package fr.recia.notifications.expander.kafka;

import fr.recia.notifications.expander.service.LdapGroupService;
import fr.recia.notifications.expander.service.LdapMailService;
import fr.recia.notifications.model_kafka.model.Notification;
import fr.recia.notifications.model_kafka.model.NotificationHeader;
import fr.recia.notifications.model_kafka.model.ServiceEvent;
import fr.recia.notifications.model_kafka.model.TargetType;
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

    private final static String TOPIC_OUT = "notifications.events.expanded";
    private final static String TOPIC_IN = "notifications.events.requested";

    private final KafkaTemplate<String, Notification> kafkaTemplate;
    private final LdapGroupService ldapGroupService;
    private final LdapMailService ldapMailService;

    public ServiceEventConsumer(KafkaTemplate<String, Notification> kafkaTemplate, LdapGroupService ldapGroupService, LdapMailService ldapMailService) {
        this.kafkaTemplate = kafkaTemplate;
        this.ldapGroupService = ldapGroupService;
        this.ldapMailService = ldapMailService;
    }

    private void sendToKafkaForUserList(List<String> ids, ServiceEvent serviceEvent){
        for (String userId : ids) {
            Notification notification = new Notification(new NotificationHeader(UUID.randomUUID().toString(), userId, serviceEvent.getHeader()), serviceEvent.getContent());
            kafkaTemplate.send(TOPIC_OUT, userId, notification);
            log.trace("Nouvelle notification {} déposée dans le topic {}", notification, TOPIC_OUT);
        }
    }


    @KafkaListener(topics = TOPIC_IN)
    public void consume(ServiceEvent serviceEvent) {
        TargetType target = serviceEvent.getTarget().getType();
        log.trace("ServiceEvent {} reçu en entrée depuis le topic {}", serviceEvent, TOPIC_IN);

        // Si c'est une liste de user le traitement est facile, on créé une notif par user
        if(target.equals(TargetType.UID)){
            log.trace("Expansion de l'event par utilisateurs");
            sendToKafkaForUserList(serviceEvent.getTarget().getIds(), serviceEvent);
        }
        // Si c'est une liste de groupes, alors il faut d'abord trouver tous les users de tous les groupes, puis on créé une notif par user
        else if(target.equals(TargetType.GROUP)){
            log.trace("Expansion de l'event par groupes");
            Set<String> uniqueUidsOfUsersInGroup = new HashSet<>();
            for(String group : serviceEvent.getTarget().getIds()){
                List<String> listOfUidsOfUsersInGroup = ldapGroupService.getGroupMembers(group);
                log.trace("Ajout de tous les utilisateurs {} pour le groupe {}", listOfUidsOfUsersInGroup, group);
                uniqueUidsOfUsersInGroup.addAll(listOfUidsOfUsersInGroup);
            }
            sendToKafkaForUserList(new ArrayList<>(uniqueUidsOfUsersInGroup), serviceEvent);
        // Si c'est un email, alors on récupère d'abord l'uid, puis on envoie la notification
        } else if (target.equals(TargetType.EMAIL)) {
            List<String> emailIDs = serviceEvent.getTarget().getIds();
            String email = emailIDs.get(0);
            log.trace("L'utilisateur a un email en identifiant, son mail est {}, recherche de son UID", email);
            List<String> userId = ldapMailService.getUidByMail(email);
            
            if(userId != null && !userId.isEmpty()) {
                sendToKafkaForUserList(userId, serviceEvent);
            }else {
                log.warn("Aucun UID trouvé dans le LDAP pour l'email {}. La notification est ignorée.", email);
            }
        }
        else {
            log.error("Type de target pour l'event {} inconnu !", serviceEvent);
            throw new RuntimeException("Unknown target type !");
        }
    }
}

