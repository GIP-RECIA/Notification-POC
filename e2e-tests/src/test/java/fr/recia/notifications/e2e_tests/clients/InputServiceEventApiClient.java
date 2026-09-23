package fr.recia.notifications.e2e_tests.clients;

import fr.recia.notifications.event_rest_client_kafka.HttpNotificationClient;
import fr.recia.notifications.model_kafka.model.Channel;

import java.util.List;

public class InputServiceEventApiClient {

    private final HttpNotificationClient httpNotificationClient;

    public InputServiceEventApiClient(){
        this.httpNotificationClient = new HttpNotificationClient("http://localhost:8179/event/emit", "DEMO-SERVICE", "wtxcI80Xn2qS7yajJh6R74rkoCSbSstV");
    }

    public void sendEvent(String userId, String title, String message) {
        httpNotificationClient.sendNormalPriorityToUser(title, message, "link", userId, List.of(Channel.WEB, Channel.MAIL, Channel.PUSH));
    }
}

