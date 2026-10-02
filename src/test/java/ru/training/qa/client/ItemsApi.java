package ru.training.qa.client;

import io.restassured.response.Response;
import ru.training.qa.api.CreateItemRequest;
import ru.training.qa.config.RequestSpecFactory;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class ItemsApi {

    public Response createItem(String token, CreateItemRequest itemRequest) {
        return given()
            .auth()
            .oauth2(token)
            .spec(RequestSpecFactory.baseSpec())
            .body(itemRequest)
            .when()
            .post("/api/v1/items");
    }

    public Response deleteItem(String token, UUID itemId) {
        return given()
            .spec(RequestSpecFactory.baseSpec())
            .auth()
            .oauth2(token)
            .when()
            .delete("/api/v1/items/{id}", itemId);
    }

    public Response getItem(String token, UUID itemId) {
        return given()
            .auth()
            .oauth2(token)
            .spec(RequestSpecFactory.baseSpec())
            .get("/api/v1/items/{id}", itemId);
    }

    public Response updateItem(
        String token,
        UUID itemId,
        CreateItemRequest itemRequest
    ) {
        return given()
            .auth()
            .oauth2(token)
            .spec(RequestSpecFactory.baseSpec())
            .body(itemRequest)
            .put("/api/v1/items/{id}", itemId);
    }
}
