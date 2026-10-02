package ru.training.qa;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.training.qa.api.CreateItemRequest;
import ru.training.qa.api.ItemResponse;
import ru.training.qa.api.RegisterRequest;
import ru.training.qa.client.AuthApi;
import ru.training.qa.client.ItemsApi;
import ru.training.qa.data.ItemDataFactory;
import ru.training.qa.data.UserDataFactory;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ItemApiTest {

    private final AuthApi authApi = new AuthApi();
    private final UserDataFactory userDataFactory = new UserDataFactory();
    private final ItemsApi itemsApi = new ItemsApi();
    private final ItemDataFactory itemDataFactory = new ItemDataFactory();

    private final List<UUID> createdItemIds = new ArrayList<>();
    private String accessToken;

    @AfterEach
    void cleanUp() {
        for (UUID itemId : createdItemIds) {
            itemsApi.deleteItem(accessToken, itemId)
                    .then()
                    .statusCode(204);
        }
    }

    @Test
    void shouldCreateItemWhenUserIsAuthenticated() {
        RegisterRequest user = userDataFactory.validUser();

        authApi.register(user.username(), user.password())
                .then()
                .statusCode(201)
                .body("username", equalTo(user.username()))
                .body("id", greaterThan(0));

        accessToken = authApi.loginResponse(user.username(), user.password())
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

        CreateItemRequest request = itemDataFactory.validItem();

        ItemResponse createdItem = itemsApi.createItem(accessToken, request)
                .then()
                .statusCode(201)
                .extract()
                .as(ItemResponse.class);

        createdItemIds.add(createdItem.id());

        assertNotNull(createdItem.id());
        assertEquals(request.name(), createdItem.name());
        assertEquals(request.price(), createdItem.price());
        assertEquals(request.statistics(), createdItem.statistics());
    }
}
