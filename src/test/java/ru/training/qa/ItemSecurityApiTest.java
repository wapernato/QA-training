package ru.training.qa;

import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.training.qa.api.ItemResponse;
import ru.training.qa.api.RegisterRequest;
import ru.training.qa.client.AuthApi;
import ru.training.qa.client.ItemsApi;
import ru.training.qa.data.ItemDataFactory;
import ru.training.qa.data.UserDataFactory;

import static org.hamcrest.Matchers.*;

class ItemSecurityApiTest {

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
    void shouldReturn404WhenAnotherUserDeletesItem() {
        String tokenA = registerAndLogin();
        String tokenB = registerAndLogin();

        UUID itemId = createItem(tokenA);

        try {
            itemsApi.deleteItem(tokenB, itemId)
                    .then()
                    .statusCode(404);

            itemsApi.getItem(tokenA, itemId)
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        } finally {
            deleteItem(tokenA, itemId);
        }
    }
}
