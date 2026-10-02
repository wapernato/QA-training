package ru.training.qa.data;

import ru.training.qa.api.CreateItemRequest;
import ru.training.qa.api.StatisticsRequest;

public class ItemDataBuilder {

    private long price = 5000;
    private String name = "QA keyboard";
    private StatisticsRequest statisticsRequest =
        new StatisticsRequest(0, 0, 0);

    public ItemDataBuilder withPrice(long price) {
        this.price = price;
        return this;
    }

    public ItemDataBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ItemDataBuilder withStatistics(StatisticsRequest statisticsRequest) {
        this.statisticsRequest = statisticsRequest;
        return this;
    }

    public CreateItemRequest build() {
        return new CreateItemRequest(name, price, statisticsRequest);
    }
}
