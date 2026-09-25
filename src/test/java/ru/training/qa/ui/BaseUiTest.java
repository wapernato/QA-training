package ru.training.qa.ui;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

abstract class BaseUiTest {

    protected WebDriver webDriver;
    protected WebDriverWait webDriverWait;

    @BeforeEach
    void setUpWebDriver() {
        webDriver = new ChromeDriver();
        webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDownWebDriver() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }
}
