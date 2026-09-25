package ru.training.qa.ui;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DynamicLoadingUiTest extends BaseUiTest {

    @Test
    void shouldShowHelloWorldWhenDynamicLoadingFinishes() {
        webDriver.get("https://the-internet.herokuapp.com/dynamic_loading/1");

        WebElement startButton = webDriverWait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("#start button"))
        );
        startButton.click();

        String message = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#finish h4"))
        ).getText();

        assertEquals("Hello World!", message);
    }
}
