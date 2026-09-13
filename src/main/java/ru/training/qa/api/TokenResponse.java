package ru.training.qa.api;

public record TokenResponse(String accessToken, String tokenType, long expiresInSeconds) { }
