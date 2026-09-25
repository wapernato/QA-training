package ru.training.qa.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.training.qa.ui.pages.LoginPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginUiTest extends BaseUiTest {

    private LoginPage loginPage;

    @BeforeEach
    void setUpLoginPage() {
        loginPage = new LoginPage(webDriver, webDriverWait);
    }

    @Test
    void shouldLoginWhenCredentialsAreValid() {
        webDriver.get("https://the-internet.herokuapp.com/login");

        WebElement loginInput = webDriver.findElement(By.id("username"));
        WebElement passwordInput = webDriver.findElement(By.id("password"));

        loginInput.sendKeys("tomsmith");
        passwordInput.sendKeys("SuperSecretPassword!");

        webDriver.findElement(By.cssSelector("button[type='submit']")).click();

        String message = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("flash"))
        ).getText();

        assertTrue(message.contains("You logged into a secure area!"));
    }

    @Test
    void shouldRejectLoginWhenPasswordIsInvalid() {
        loginPage.open();
        loginPage.login("tomsmith", "IncorrectPassword");
        assertTrue(loginPage.getErrorMessage().contains("Your password is invalid!"));
    }

    @Test
    void shouldLoginAndLogoutWhenDataIsValid() {
        webDriver.get("https://the-internet.herokuapp.com/login");

        WebElement loginInput = webDriver.findElement(By.id("username"));
        WebElement passwordInput = webDriver.findElement(By.id("password"));

        loginInput.sendKeys("tomsmith");
        passwordInput.sendKeys("SuperSecretPassword!");

        webDriver.findElement(By.cssSelector("button[type='submit']")).click();

        String messageLogin = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("flash"))
        ).getText();

        assertTrue(messageLogin.contains("You logged into a secure area!"));

        webDriver.findElement(By.linkText("Logout")).click();

        String messageLogout = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("flash"))
        ).getText();

        assertTrue(messageLogout.contains("You logged out of the secure area!"));
    }
}
