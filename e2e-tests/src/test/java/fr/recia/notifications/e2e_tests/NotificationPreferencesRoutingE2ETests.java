package fr.recia.notifications.e2e_tests;

import fr.recia.notifications.model_kafka.model.ChannelPreferences;
import fr.recia.notifications.model_kafka.model.Priority;
import fr.recia.notifications.model_kafka.model.ServicePreferences;
import fr.recia.notifications.model_kafka.model.StoredNotification;
import fr.recia.notifications.model_kafka.model.UserPreferences;
import fr.recia.notifications.e2e_tests.clients.InputPreferencesApiClient;
import fr.recia.notifications.e2e_tests.clients.InputServiceEventApiClient;
import fr.recia.notifications.e2e_tests.clients.OutputNotificationApiClient;
import fr.recia.notifications.e2e_tests.clients.InputSmtpApiClient;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;

import io.jsonwebtoken.io.Decoders;

class NotificationPreferencesRoutingE2ETests {

    private final InputPreferencesApiClient inputPreferencesApiClient = new InputPreferencesApiClient();
    private final InputServiceEventApiClient inputServiceEventApiClient = new InputServiceEventApiClient();
    private final InputSmtpApiClient inputSmtpApiClient = new InputSmtpApiClient("localhost", 1026, "user-1", "password1");

    private final OutputNotificationApiClient outputApi = new OutputNotificationApiClient();
    private final String SERVICE_NAME = "DEMO-SERVICE";
    private final String USER_ID = "AAA1";

    private final String USER_EMAIL = "corentin.colin@netocentre.fr";
    private final String USER_ID_MAIL = "F1700ivg";

    private final String BEARER_TOKEN = generateToken(USER_ID);
    private final String BEARER_TOKEN_MAIL = generateToken(USER_ID_MAIL);

    private static String generateToken(String userId) {
        byte[] keyBytes = Decoders.BASE64.decode("7xqI7LLKhRt15a1r05vz4O5lIchhxCx6PXB5dLmyq/U=");
        return Jwts.builder()
                .setIssuer("https://ent.recia.fr/portail")
                .setSubject(userId)
                .setAudience("https://ent.recia.fr/portail")
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .setIssuedAt(new Date())
                .signWith(Keys.hmacShaKeyFor(keyBytes), SignatureAlgorithm.HS256)
                .compact();
    }

    private void assertNotificationReceived(String expectedTitle, String expectedMessage) {
        await().atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    List<StoredNotification> notifications = outputApi.getNotifications(BEARER_TOKEN_MAIL);
                    assertThat(notifications).isNotNull();
                    assertThat(notifications)
                            .filteredOn(n -> expectedTitle.equals(n.getNotification().getContent().getTitle())
                                    && expectedMessage.equals(n.getNotification().getContent().getMessage()))
                            .hasSize(1);
                });
    }

    @Test
    void smtp_proxy_to_web_notification() {
        String testId = UUID.randomUUID().toString();
        String subject = "Partage Nextcloud " + testId;
        String expectedTitle = "Un fichier a été partagé avec vous.";  // = titre configuré côté processor

        String from = "noreply@Nextcloud.recia.fr";

        inputSmtpApiClient.sendEmail(from, USER_EMAIL, subject, "corps du mail peu importe", null);

        assertNotificationReceived(expectedTitle, subject);
    }


    @Test
    void default_preferences_unique_user() {
        String testId = UUID.randomUUID().toString();
        String title = "Test 1 consumer web "+testId;
        String message = "Preferences par defaut target user unique "+testId;
        inputServiceEventApiClient.sendEvent(USER_ID, title, message);
        await().atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    List<StoredNotification> notifications = outputApi.getNotifications(BEARER_TOKEN);
                    assertThat(notifications).isNotNull();
                    assertThat(notifications)
                            .isNotNull()
                            .filteredOn(n -> title.equals(n.getNotification().getContent().getTitle()) && message.equals(n.getNotification().getContent().getMessage()))
                            .hasSize(1);
                });
    }

    @Test
    void global_preferences_unique_user() {
        String testId = UUID.randomUUID().toString();
        String title = "Test 2 consumer web"+testId;
        String message = "Preferences globales target user unique"+testId;
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setUserId(USER_ID);
        userPreferences.setGlobal(new ChannelPreferences(true, false, false));
        userPreferences.setServices(new HashMap<>());
        inputPreferencesApiClient.postPreference(userPreferences, BEARER_TOKEN);
        inputServiceEventApiClient.sendEvent(USER_ID, title, message);
        await().atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    List<StoredNotification> notifications = outputApi.getNotifications(BEARER_TOKEN);
                    assertThat(notifications).isNotNull();
                    assertThat(notifications)
                            .isNotNull()
                            .filteredOn(n -> title.equals(n.getNotification().getContent().getTitle()) && message.equals(n.getNotification().getContent().getMessage()))
                            .hasSize(1);
                });
    }

    @Test
    void service_preferences_no_override_unique_user() {
        String testId = UUID.randomUUID().toString();
        String title = "Test 3 consumer web"+testId;
        String message = "Preferences service no override target user unique"+testId;
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setUserId(USER_ID);
        userPreferences.setGlobal(new ChannelPreferences(true, false, false));
        Map<String, ServicePreferences> servicePreferencesMap = new HashMap<>();
        servicePreferencesMap.put(SERVICE_NAME, new ServicePreferences(true, false, new HashMap<>()));
        userPreferences.setServices(servicePreferencesMap);
        inputPreferencesApiClient.postPreference(userPreferences, BEARER_TOKEN);
        inputServiceEventApiClient.sendEvent(USER_ID, title, message);
        await().atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    List<StoredNotification> notifications = outputApi.getNotifications(BEARER_TOKEN);
                    assertThat(notifications).isNotNull();
                    assertThat(notifications)
                            .isNotNull()
                            .filteredOn(n -> title.equals(n.getNotification().getContent().getTitle()) && message.equals(n.getNotification().getContent().getMessage()))
                            .hasSize(1);
                });
    }

    @Test
    void service_preferences_override_unique_user() {
        String testId = UUID.randomUUID().toString();
        String title = "Test 4 consumer web"+testId;
        String message = "Preferences service override target user unique"+testId;
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setUserId(USER_ID);
        userPreferences.setGlobal(new ChannelPreferences(true, false, false));
        Map<String, ServicePreferences> servicePreferencesMap = new HashMap<>();
        Map<Priority, ChannelPreferences> priorityChannelPreferencesMap = new HashMap<>();
        priorityChannelPreferencesMap.put(Priority.NORMAL, new ChannelPreferences(true, false, false));
        servicePreferencesMap.put(SERVICE_NAME, new ServicePreferences(true, true, priorityChannelPreferencesMap));
        userPreferences.setServices(servicePreferencesMap);
        inputPreferencesApiClient.postPreference(userPreferences, BEARER_TOKEN);
        inputServiceEventApiClient.sendEvent(USER_ID, title, message);
        await().atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    List<StoredNotification> notifications = outputApi.getNotifications(BEARER_TOKEN);
                    assertThat(notifications).isNotNull();
                    assertThat(notifications)
                            .isNotNull()
                            .filteredOn(n -> title.equals(n.getNotification().getContent().getTitle()) && message.equals(n.getNotification().getContent().getMessage()))
                            .hasSize(1);
                });
    }
}

