package ru.training.qa;

import static org.junit.jupiter.api.Assertions.*;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ItemValidatorPracticeTest {
    static Stream<Arguments> invalidPrices() { return Stream.of(Arguments.of(-1L), Arguments.of(0L)); }
    @ParameterizedTest
    @MethodSource("invalidPrices")
    void priceMustBePositive(long price) {
        assertFalse(isValidPrice(price));
    }
    private boolean isValidPrice(long price) { return price > 0; }
}
