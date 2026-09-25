package ru.training.qa.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

    private final WebDriver webDriver;
    private final WebDriverWait webDriverWait;

    public LoginPage(WebDriver webDriver, WebDriverWait webDriverWait) {
        this.webDriver = webDriver;
        this.webDriverWait = webDriverWait;
    }

    public void open() {
        webDriver.get("https://the-internet.herokuapp.com/login");
    }

    public void login(String username, String password) {
        WebElement loginInput = webDriver.findElement(By.id("username"));
        WebElement passwordInput = webDriver.findElement(By.id("password"));

        loginInput.sendKeys(username);
        passwordInput.sendKeys(password);

        webDriver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    public String getErrorMessage() {
        return webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("flash"))
        ).getText();
    }
}
