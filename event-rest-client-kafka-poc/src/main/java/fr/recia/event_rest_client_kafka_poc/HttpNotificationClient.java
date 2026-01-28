package fr.recia.event_rest_client_kafka_poc;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    public void sendLowPriorityToUser(String title, String message, String userId) {
        EventHeader eventHeader = new EventHeader(UUID.randomUUID().toString(), Priority.LOW, this.service, LocalDateTime.now().toString());
        Content eventContent = new Content(title, message);
        Target eventTarget = new Target(TargetType.USER, Collections.singletonList(userId));
        ServiceEvent serviceEvent = new ServiceEvent(eventHeader, eventContent, eventTarget);
        send(serviceEvent);
    }

    public void sendNormalPriorityToUser(String title, String message, String userId) {
        EventHeader eventHeader = new EventHeader(UUID.randomUUID().toString(), Priority.NORMAL, this.service, LocalDateTime.now().toString());
        Content eventContent = new Content(title, message);
        Target eventTarget = new Target(TargetType.USER, Collections.singletonList(userId));
        ServiceEvent serviceEvent = new ServiceEvent(eventHeader, eventContent, eventTarget);
        send(serviceEvent);
    }

    public void sendHighPriorityToUser(String title, String message, String userId) {
        EventHeader eventHeader = new EventHeader(UUID.randomUUID().toString(), Priority.HIGH, this.service, LocalDateTime.now().toString());
        Content eventContent = new Content(title, message);
        Target eventTarget = new Target(TargetType.USER, Collections.singletonList(userId));
        ServiceEvent serviceEvent = new ServiceEvent(eventHeader, eventContent, eventTarget);
        send(serviceEvent);
    }

    public void sendLowPriorityToGroup(String title, String message, String userId) {
        EventHeader eventHeader = new EventHeader(UUID.randomUUID().toString(), Priority.LOW, this.service, LocalDateTime.now().toString());
        Content eventContent = new Content(title, message);
        Target eventTarget = new Target(TargetType.GROUP, Collections.singletonList(userId));
        ServiceEvent serviceEvent = new ServiceEvent(eventHeader, eventContent, eventTarget);
        send(serviceEvent);
    }

    public void sendNormalPriorityToGroup(String title, String message, String userId) {
        EventHeader eventHeader = new EventHeader(UUID.randomUUID().toString(), Priority.NORMAL, this.service, LocalDateTime.now().toString());
        Content eventContent = new Content(title, message);
        Target eventTarget = new Target(TargetType.GROUP, Collections.singletonList(userId));
        ServiceEvent serviceEvent = new ServiceEvent(eventHeader, eventContent, eventTarget);
        send(serviceEvent);
    }

    public void sendHighPriorityToGroup(String title, String message, String userId) {
        EventHeader eventHeader = new EventHeader(UUID.randomUUID().toString(), Priority.HIGH, this.service, LocalDateTime.now().toString());
        Content eventContent = new Content(title, message);
        Target eventTarget = new Target(TargetType.GROUP, Collections.singletonList(userId));
        ServiceEvent serviceEvent = new ServiceEvent(eventHeader, eventContent, eventTarget);
        send(serviceEvent);
    }

    private void send(ServiceEvent payload) {
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