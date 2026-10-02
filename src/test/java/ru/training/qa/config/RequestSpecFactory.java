package ru.training.qa.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RequestSpecFactory {

    public static RequestSpecification baseSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost:8080")
                .setContentType(ContentType.JSON)
                .build();
    }
}
