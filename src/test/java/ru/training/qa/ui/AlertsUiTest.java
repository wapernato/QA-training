package ru.training.qa.ui;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlertsUiTest extends BaseUiTest {

    @Test
    void shouldAcceptJsAlert() {
        webDriver.get("https://the-internet.herokuapp.com/javascript_alerts");

        WebElement alertButton = webDriver.findElement(By.cssSelector("button[onclick='jsAlert()']"));

        alertButton.click();

        Alert alert = webDriverWait.until(
                ExpectedConditions.alertIsPresent()
        );

        assertEquals("I am a JS Alert", alert.getText());

        alert.accept();

        WebElement resultMessage = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("result"))
        );

        assertEquals("You successfully clicked an alert", resultMessage.getText());
    }
}
