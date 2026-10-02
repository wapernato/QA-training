package ru.training.qa.data;

import ru.training.qa.api.CreateItemRequest;
import ru.training.qa.api.StatisticsRequest;

public class ItemDataFactory {

    public CreateItemRequest validItem() {
        return new ItemDataBuilder().build();
    }

    public CreateItemRequest itemWithPrice(long price) {
        return new ItemDataBuilder()
                .withPrice(price)
                .build();
    }

    public CreateItemRequest itemWithName(String name) {
        return new ItemDataBuilder()
                .withName(name)
                .build();
    }

    public CreateItemRequest itemWithStatistics(StatisticsRequest statistics) {
        return new ItemDataBuilder()
                .withStatistics(statistics)
                .build();
    }
}
