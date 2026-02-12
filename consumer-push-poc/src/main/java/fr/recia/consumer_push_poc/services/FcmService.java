package fr.recia.consumer_push_poc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import fr.recia.consumer_push_poc.configuration.FCMProperties;
import fr.recia.consumer_push_poc.model.ErrorResponse;
import fr.recia.model_kafka_poc.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class FcmService {

    private final FCMProperties fcmProperties;
    private final HttpClient client;
    private final ObjectMapper mapper;
    private final TokenService tokenService;
    private String accessToken;
    private long tokenExpiration;

    public FcmService(FCMProperties fcmProperties, ObjectMapper objectMapper, TokenService tokenService){
        this.fcmProperties = fcmProperties;
        this.client = HttpClient.newHttpClient();
        this.mapper = objectMapper;
        this.tokenService = tokenService;
        this.tokenExpiration = 0;
    }

    public void sendNotification(Notification notification, String deviceToken) throws Exception {

        String token = getAccessToken();
        Map<String, Object> payload = buildMessage(notification, deviceToken);
        log.trace("Message built for token {} is {}", deviceToken, payload);
        String json = mapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.fcmProperties.getFcmUrl()))
                .timeout(Duration.ofSeconds(15))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            try {
                ErrorResponse error = mapper.readValue(response.body(), ErrorResponse.class);
                // Supprimer la device si elle n'est plus enregistrée
                if(error.getError().getDetails().getFirst().getErrorCode().equals("UNREGISTERED")){
                    tokenService.removeToken(notification.getHeader().getUserId(), deviceToken);
                    log.info("Device token {} removed from token list because device is unregistered", deviceToken);
                }
            } catch (Exception e) {
                log.error("Parsing error. Couldn't read error response", e);
                throw new RuntimeException(response.body());
            }
        } else {
            log.debug("Notification {} was sent successfully. Response is {}", notification, response.body());
        }
    }

    private String getAccessToken() throws Exception {
        // Si on possède déjà un AT et s'il n'est pas expiré, alors on retourne l'AT actuel
        if (accessToken != null && System.currentTimeMillis() < tokenExpiration) {
            log.trace("Access token already valid, returning it : {}", accessToken);
            return accessToken;
        }
        // Sinon on en demande un
        log.debug("Access token is no longer valid, getting a new one");
        GoogleCredentials credentials = GoogleCredentials.fromStream(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(this.fcmProperties.getServiceAccountPath())))
                        .createScoped(List.of(this.fcmProperties.getFirebaseAuthUrl()));
        credentials.refreshIfExpired();
        this.accessToken = credentials.getAccessToken().getTokenValue();
        this.tokenExpiration = System.currentTimeMillis() + 55 * 60 * 1000;
        log.debug("New access token is {}", accessToken);
        return accessToken;
    }

    private Map<String, Object> buildMessage(Notification notification, String token) {
        return Map.of(
            // Objet racine contenant tout le payload
            "message", Map.of(
                // Token identifiant l'utilisateur
                "token", token,
                // Infos de la notification
                "notification", Map.of(
                        "title", notification.getContent().getTitle(),
                        "body", notification.getContent().getMessage()
                ),
                // Data supplémentaire custom et libre
                "data", Map.of(
                        "key1", "value1",
                        "key2", "value2"
                )
            )
        );
    }
}

