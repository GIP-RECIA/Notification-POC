package fr.recia.notification_e2e_tests.clients;

import fr.recia.event_rest_client_kafka_poc.HttpNotificationClient;

public class InputServiceEventApiClient {

    private final HttpNotificationClient httpNotificationClient;

    public InputServiceEventApiClient(){
        this.httpNotificationClient = new HttpNotificationClient("http://localhost:8179/event/emit", "DEMO-SERVICE", "wtxcI80Xn2qS7yajJh6R74rkoCSbSstV");
    }

    public void sendEvent(String userId, String title, String message) {
        httpNotificationClient.sendNormalPriorityToUser(title, message, userId);
    }
}

