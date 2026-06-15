package fr.recia.notifications.e2e_tests.clients;

import fr.recia.notifications.model_kafka.model.StoredNotification;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;

import java.util.List;

public class OutputNotificationApiClient {

    private final String getNotifUrl = "http://localhost:8176/notif/all";

    public List<StoredNotification> getNotifications(String bearer) {
        return RestAssured
                .given()
                .header("Authorization", "Bearer "+bearer)
                .when()
                .get(getNotifUrl)
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });
    }

}
