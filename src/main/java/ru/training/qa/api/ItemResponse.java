package ru.training.qa.api;

import java.util.UUID;

public record ItemResponse(UUID id, long sellerId, String name, long price, StatisticsRequest statistics) { }
