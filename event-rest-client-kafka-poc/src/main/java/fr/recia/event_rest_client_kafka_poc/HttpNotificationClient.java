package fr.recia.event_rest_client_kafka_poc;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.recia.model_kafka_poc.model.Channel;
import fr.recia.model_kafka_poc.model.Content;
import fr.recia.model_kafka_poc.model.EventHeader;
import fr.recia.model_kafka_poc.model.Priority;
import fr.recia.model_kafka_poc.model.ServiceEvent;
import fr.recia.model_kafka_poc.model.Target;
import fr.recia.model_kafka_poc.model.TargetType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class HttpNotificationClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final URI endpoint;
    private final String service;
    private final String apiKey;
    private final int maxRetries;
    private final static int DEFAULT_MAX_RETRIES = 3;

    public HttpNotificationClient(String apiUrl, String service, String apiKey) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.endpoint = URI.create(apiUrl);
        this.service = service;
        this.apiKey = apiKey;
        this.maxRetries = DEFAULT_MAX_RETRIES;
    }

    public HttpNotificationClient(String apiUrl, String service, String apiKey, int maxRetries) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.endpoint = URI.create(apiUrl);
        this.service = service;
        this.apiKey = apiKey;
        this.maxRetries = maxRetries;
    }

    public void sendLowPriorityToUser(String title, String message, String link, String userId, List<Channel> channels) {
        sendNotification(title, message, link, userId, channels, Priority.LOW, TargetType.USER);
    }

    public void sendNormalPriorityToUser(String title, String message, String link, String userId, List<Channel> channels) {
        sendNotification(title, message, link, userId, channels, Priority.NORMAL, TargetType.USER);
    }

    public void sendHighPriorityToUser(String title, String message, String link, String userId, List<Channel> channels) {
        sendNotification(title, message, link, userId, channels, Priority.HIGH, TargetType.USER);
    }

    public void sendLowPriorityToGroup(String title, String message, String link, String groupId, List<Channel> channels) {
        sendNotification(title, message, link, groupId, channels, Priority.LOW, TargetType.GROUP);
    }

    public void sendNormalPriorityToGroup(String title, String message, String link, String groupId, List<Channel> channels) {
        sendNotification(title, message, link, groupId, channels, Priority.NORMAL, TargetType.GROUP);
    }

    public void sendHighPriorityToGroup(String title, String message, String link, String groupId, List<Channel> channels) {
        sendNotification(title, message, link, groupId, channels, Priority.HIGH, TargetType.GROUP);
    }

    public void sendNotification(String title, String message, String link, String userId, List<Channel> channels, Priority priority, TargetType targetType){
        EventHeader eventHeader = new EventHeader(UUID.randomUUID().toString(), priority, this.service, channels, LocalDateTime.now().toString());
        Content eventContent = new Content(title, message, link);
        Target eventTarget = new Target(targetType, Collections.singletonList(userId));
        ServiceEvent serviceEvent = new ServiceEvent(eventHeader, eventContent, eventTarget);
        sendRequest(serviceEvent);
    }

    private void sendRequest(ServiceEvent payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            int current_try = 0;
            while(current_try < this.maxRetries){
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(endpoint)
                        .header("Content-Type", "application/json")
                        .header("X-API-KEY", this.apiKey)
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                HttpResponse<Void> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.discarding());
                if (response.statusCode() == 503) {
                    System.out.println("Failed to sent notification. Retrying...");
                } else if (response.statusCode() >= 400) {
                    throw new RuntimeException("Notification API error: HTTP " + response.statusCode());
                } else {
                    return;
                }
                current_try+=1;
            }
            throw new RuntimeException("Failed to send notification after "+this.maxRetries+" tries");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to send notification", e);
        }
    }
}