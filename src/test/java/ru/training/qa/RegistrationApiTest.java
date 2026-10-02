package ru.training.qa;

import org.junit.jupiter.api.Test;
import ru.training.qa.api.RegisterRequest;
import ru.training.qa.client.AuthApi;
import ru.training.qa.data.UserDataFactory;

import static org.hamcrest.Matchers.*;

class RegistrationApiTest {

    private final AuthApi authApi = new AuthApi();
    private final UserDataFactory userDataFactory = new UserDataFactory();

    @Test
    void shouldRegister_whenDataIsValid() {
        RegisterRequest user = userDataFactory.validUser();

        authApi.register(user.username(), user.password())
                .then()
                .statusCode(201)
                .body("username", equalTo(user.username()));
    }

    @Test
    void shouldReturn409WhenUsernameAlreadyExists() {
        RegisterRequest user = userDataFactory.validUser();

        authApi.register(user.username(), user.password())
                .then()
                .statusCode(201)
                .body("username", equalTo(user.username()));

        authApi.register(user.username(), user.password())
                .then()
                .statusCode(409)
                .body("status", equalTo(409))
                .body("code", equalTo("CONFLICT"))
                .body("path", equalTo("/api/v1/auth/register"));
    }
}
