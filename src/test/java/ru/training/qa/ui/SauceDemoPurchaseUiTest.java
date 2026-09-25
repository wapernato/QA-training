package ru.training.qa.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class SauceDemoPurchaseUiTest {

    private static final String BASE_URL = "https://www.saucedemo.com/";
    private static final String USERNAME = "standard_user";
    private static final String PASSWORD = "secret_sauce";
    private static final String PRODUCT_NAME = "Sauce Labs Backpack";
    private static final String FIRST_NAME = "Test";
    private static final String LAST_NAME = "User";
    private static final String POSTAL_CODE = "123456";

    private WebDriver webDriver;
    private WebDriverWait webDriverWait;

    @BeforeEach
    void setUp() {
        webDriver = new ChromeDriver();
        webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
    }

    @Test
    void shouldCompletePurchase() {
        webDriver.get(BASE_URL);

        WebElement loginInput = webDriver.findElement(By.id("user-name"));
        WebElement passwordInput = webDriver.findElement(By.id("password"));

        loginInput.sendKeys(USERNAME);
        passwordInput.sendKeys(PASSWORD);

        WebElement loginButton = webDriver.findElement(By.id("login-button"));
        loginButton.click();

        webDriverWait.until(
                ExpectedConditions.urlToBe(
                        "https://www.saucedemo.com/inventory.html"
                )
        );

        WebElement productCard = findProductCard(PRODUCT_NAME);

        WebElement addToCartButton = productCard.findElement(By.cssSelector("button.btn_inventory"));
        addToCartButton.click();

        WebElement cartBadge = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.shopping_cart_badge"))
        );
        assertEquals("1", cartBadge.getText());

        WebElement shoppingCartLink = webDriver.findElement(By.cssSelector("a.shopping_cart_link"));
        shoppingCartLink.click();

        // TODO 4: проверить текст кнопки Remove.
        // TODO 5: проверить переход в корзину, название и цену товара.
        // TODO 6: перейти к оформлению и заполнить данные покупателя.
        // TODO 7: завершить заказ.
        // TODO 8: проверить сообщение Thank you for your order!

        fail("Реализуй сценарий покупки и удали этот fail");
    }

    private WebElement findProductCard(String productName) {
        String xpath = (
                "//*[@data-test='inventory-item-name' " +
                        "and normalize-space()='%s']" +
                        "/ancestor::div[@data-test='inventory-item'][1]"
        ).formatted(productName);

        return webDriver.findElement(By.xpath(xpath));
    }
}
