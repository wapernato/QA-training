package ru.training.qa.client;

import io.restassured.response.Response;
import ru.training.qa.api.LoginRequest;
import ru.training.qa.api.RegisterRequest;
import ru.training.qa.config.RequestSpecFactory;

import static io.restassured.RestAssured.given;

public class AuthApi {
    public Response register(String username, String password) {
        return given().spec(RequestSpecFactory.baseSpec())
                .body(new RegisterRequest(username, password))
                .post("/api/v1/auth/register");
    }

    public Response loginResponse(String username, String password) {
        return given().spec(RequestSpecFactory.baseSpec())
                .body(new LoginRequest(username, password))
                .post("/api/v1/auth/login");
    }

    public String login(String username, String password) {
        return loginResponse(username, password).then()
                .statusCode(200).extract().jsonPath().getString("accessToken");
    }
}
