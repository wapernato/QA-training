package ru.training.qa;

import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.*;

import java.util.Map;
import java.util.UUID;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

class RegistrationApiTest {

    @Test
    void shouldRegister_whenDataIsValid() {
        String username = UUID.randomUUID().toString();
        String password = "14f6H2sdf!$";

        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                "username", username,
                "password", password
        )).when()
                .post("/api/v1/auth/register")
                .then()
                .statusCode(201)
                .body("username", equalTo(username));

    }
}
