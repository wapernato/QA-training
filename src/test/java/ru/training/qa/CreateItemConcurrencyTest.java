package ru.training.qa;

import java.util.UUID;
import java.util.concurrent.*;
import org.junit.jupiter.api.Test;
import ru.training.qa.api.ItemResponse;
import ru.training.qa.api.RegisterRequest;
import ru.training.qa.client.AuthApi;
import ru.training.qa.client.ItemsApi;
import ru.training.qa.data.ItemDataFactory;
import ru.training.qa.data.UserDataFactory;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateItemConcurrencyTest {

    private final AuthApi authApi = new AuthApi();
    private final UserDataFactory userDataFactory = new UserDataFactory();
    private final ItemsApi itemsApi = new ItemsApi();
    private final ItemDataFactory itemDataFactory = new ItemDataFactory();

    private String registerAndLogin() {
        RegisterRequest user = userDataFactory.validUser();
        authApi.register(user.username(), user.password())
                .then()
                .statusCode(201);

        return authApi.loginResponse(user.username(), user.password())
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
        return itemsApi.createItem(token, itemDataFactory.validItem())
                .then()
                .statusCode(201)
                .extract()
                .as(ItemResponse.class)
                .id();
    }

    private void deleteItem(String token, UUID itemId) {
        itemsApi.deleteItem(token, itemId)
                .then()
                .statusCode(204);
    }

    @Test
    void shouldCreateDistinctItemsConcurrently() throws Exception {

        String tokenA = registerAndLogin();

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

            UUID finalFirstItemId = firstItemId;
            UUID finalSecondItemId = secondItemId;
            assertAll(
                    () -> assertNotNull(finalFirstItemId),
                    () -> assertNotNull(finalSecondItemId),
                    () -> assertNotEquals(finalFirstItemId, finalSecondItemId)
            );
        } finally {

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
