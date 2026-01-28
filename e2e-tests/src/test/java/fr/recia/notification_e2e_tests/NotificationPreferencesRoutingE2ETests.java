package fr.recia.notification_e2e_tests;

import fr.recia.model_kafka_poc.model.ChannelPreferences;
import fr.recia.model_kafka_poc.model.Priority;
import fr.recia.model_kafka_poc.model.ServicePreferences;
import fr.recia.model_kafka_poc.model.StoredNotification;
import fr.recia.model_kafka_poc.model.UserPreferences;
import fr.recia.notification_e2e_tests.clients.InputPreferencesApiClient;
import fr.recia.notification_e2e_tests.clients.InputServiceEventApiClient;
import fr.recia.notification_e2e_tests.clients.OutputNotificationApiClient;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class NotificationPreferencesRoutingE2ETests {

    private final InputPreferencesApiClient inputPreferencesApiClient = new InputPreferencesApiClient();
    private final InputServiceEventApiClient inputServiceEventApiClient = new InputServiceEventApiClient();
    private final OutputNotificationApiClient outputApi = new OutputNotificationApiClient();
    private final String SERVICE_NAME = "DEMO-SERVICE";
    private final String USER_ID = "AAA1";
    private final String BEARER_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2VudC5yZWNpYS5mci9wb3J0YWlsIiwic3ViIjoiQUFBMSIsImF1ZCI6Imh0dHBzOi8vZW50LnJlY2lhLmZyL3BvcnRhaWwiLCJleHAiOjk3NjkxNzYwOTYsImlhdCI6MTc2OTE3NTc5Nn0.VVGM4llYMG7Qa4-Jv5Y0uRhMfRhzGYKWwGzC0JSnuko";

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

