package ru.training.qa;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.*;

class CreateItemConcurrencyTest {

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
    void ItemConcurrencyTest() throws Exception{

        String tokenA = registerAndLogin("artem_" + UUID.randomUUID(), "c8bxxfhL^G");

        UUID firstItemId = null;
        UUID secondItemId = null;

        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch readyGate = new CountDownLatch(2);

        Callable<UUID> task = () -> {
            readyGate.countDown();
            startGate.await();

            return createItem(tokenA);
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            Future<UUID> firstTask = executorService.submit(task);
            Future<UUID> secondTask = executorService.submit(task);

            readyGate.await();
            startGate.countDown();

            firstItemId = firstTask.get(10, TimeUnit.SECONDS);
            secondItemId = secondTask.get(10, TimeUnit.SECONDS);

            List<UUID> list = List.of(firstItemId, secondItemId);

            UUID finalFirstItemId = firstItemId;
            UUID finalSecondItemId = secondItemId;
            assertAll(
                    () -> assertNotNull(finalFirstItemId),
                    () -> assertNotNull(finalSecondItemId),
                    () -> assertNotEquals(finalFirstItemId, finalSecondItemId)
            );
        }
        finally {

            if (firstItemId != null) {
                deleteItem(tokenA, firstItemId);
            }

            if (secondItemId != null) {
                deleteItem(tokenA, secondItemId);
            }

            executorService.shutdownNow();
        }
    }
}
