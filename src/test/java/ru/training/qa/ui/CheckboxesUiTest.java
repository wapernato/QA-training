package ru.training.qa.ui;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckboxesUiTest extends BaseUiTest {

    @Test
    void shouldToggleCheckboxesWhenUserClicksThem() {
        webDriver.get("https://the-internet.herokuapp.com/checkboxes");

        List<WebElement> checkboxes = webDriver.findElements(By.cssSelector("input[type='checkbox']"));
        WebElement firstCheckbox = checkboxes.get(0);
        WebElement secondCheckbox = checkboxes.get(1);

        assertFalse(firstCheckbox.isSelected());
        firstCheckbox.click();
        assertTrue(firstCheckbox.isSelected());

        assertTrue(secondCheckbox.isSelected());
        secondCheckbox.click();
        assertFalse(secondCheckbox.isSelected());
    }
}
