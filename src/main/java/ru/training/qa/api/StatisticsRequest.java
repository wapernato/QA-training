package ru.training.qa.api;

import jakarta.validation.constraints.PositiveOrZero;

public record StatisticsRequest(
        @PositiveOrZero long contacts,
        @PositiveOrZero long likes,
        @PositiveOrZero long viewCount) { }
