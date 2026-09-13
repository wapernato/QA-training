package ru.training.qa;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasKey;

public class ItemApiTest {

    private final List<UUID> createdItemIds = new ArrayList<>();
    private String accessToken;

    @AfterEach
    void cleanUp() {
        for (UUID itemId : createdItemIds) {
            given()
                    .auth().oauth2(accessToken)
                    .when()
                    .delete("/api/v1/items/{id}", itemId)
                    .then()
                    .statusCode(204);
        }
    }
    @Test
    void shouldCreateItemWhenUserIsAuthenticated() {
        String username = UUID.randomUUID().toString();
        String password = "r18Vb7!a;0o";

        given().contentType(ContentType.JSON).body(Map.of(
                        "username", username,
                        "password", password
                )).when().post("/api/v1/auth/register")
                .then()
                .statusCode(201)
                .body("username", equalTo(username) )
                .body("id", greaterThan(0));

        accessToken = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "username", username,
                        "password", password
                )).when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .body("accessToken", not(blankOrNullString()))
                .body("tokenType", equalTo("Bearer"))
                .body("expiresInSeconds", greaterThan(0))
                .body("$", not(hasKey("password")))
                .body("$", not(hasKey("passwordHash")))
                .extract()
                .jsonPath()
                .getString("accessToken");

        UUID itemId = given()
                .auth().oauth2(accessToken).contentType(ContentType.JSON)
                .body(Map.of(
                        "name", "QA keyboard",
                        "price", 5000,
                        "statistics", Map.of(
                                "contacts", 0,
                                "likes", 0,
                                "viewCount", 0
                        )
                )).when().post("/api/v1/items")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getUUID("id");

        createdItemIds.add(itemId);
    }
}