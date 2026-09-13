package ru.training.qa;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasKey;

class ItemSecurityApiTest {

    private String registerAndLogin(String username, String password) {
        given().contentType(ContentType.JSON).body(Map.of(
                "username", username,
                "password", password
        )).when()
                .post("/api/v1/auth/register")
                .then()
                .statusCode(201);

        return given().contentType(ContentType.JSON).body(Map.of(
                "username", username,
                "password", password
        )).when()
                .post("/api/v1/auth/login")
                .then()
                .body("accessToken", not(blankOrNullString()))
                .body("tokenType", equalTo("Bearer"))
                .body("expiresInSeconds", greaterThan(0))
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("accessToken");
    }

    private UUID createItem(String token) {
        return given().contentType(ContentType.JSON).auth().oauth2(token)
                .body(Map.of(
                "name", "QA keyboard",
                "price", 5000,
                "statistics", Map.of(
                        "contacts", 0,
                        "likes", 0,
                        "viewCount", 0
                )
        )).when()
                .post("/api/v1/items")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getUUID("id");
    }

    private void deleteItem(String token, UUID itemId) {
        given().contentType(ContentType.JSON)
                .auth().oauth2(token)
                .when()
                .delete("/api/v1/items/{id}", itemId)
                .then()
                .statusCode(204);
    }

    @Test
    void shouldReturn404WhenAnotherUserDeletesItem() {
        String tokenA = registerAndLogin("artem_" + UUID.randomUUID(), "robzaidaHQD345");
        String tokenB = registerAndLogin("antony_" + UUID.randomUUID(), "ro1b#zikHQD");

        UUID itemId = createItem(tokenA);

        try {
            given()
                    .auth()
                    .oauth2(tokenB)
                    .when()
                    .delete("/api/v1/items/{id}", itemId)
                    .then()
                    .statusCode(404);

            given().auth().oauth2(tokenA)
                    .when()
                    .get("/api/v1/items/{id}", itemId)
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
        finally {
            deleteItem(tokenA, itemId);
        }
    }
}