package fr.recia.notification_e2e_tests.clients;

import fr.recia.model_kafka_poc.model.UserPreferences;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class InputPreferencesApiClient {

    private final String preferencesPostUrl = "http://localhost:8078/prefs/save";

    public void postPreference(UserPreferences userPreferences, String bearer) {
        RestAssured
                .given()
                .header("Authorization", "Bearer "+bearer)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(userPreferences)
                .when()
                .post(preferencesPostUrl)
                .then()
                .statusCode(202);
    }
}

