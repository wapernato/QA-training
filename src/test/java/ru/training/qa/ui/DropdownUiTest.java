package ru.training.qa.ui;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DropdownUiTest extends BaseUiTest {

    @Test
    void shouldSelectOptionOne() {
        webDriver.get("https://the-internet.herokuapp.com/dropdown");

        WebElement dropdownElement = webDriver.findElement(By.id("dropdown"));
        WebElement disabledOption = webDriver.findElement(
                By.cssSelector("#dropdown option[disabled]")
        );

        Select dropdown = new Select(dropdownElement);

        dropdown.selectByVisibleText("Option 1");

        String selectedOptionText = dropdown.getFirstSelectedOption().getText();

        assertEquals("Please select an option", disabledOption.getText());
        assertEquals("Option 1", selectedOptionText);
    }
}
