package ru.training.qa.data;

import java.util.UUID;
import ru.training.qa.api.RegisterRequest;

public class UserDataFactory {

    public RegisterRequest validUser() {
        return new RegisterRequest("qa_" + UUID.randomUUID(), "QaPassword123!");
    }
}
