package com.saucedemo.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ElementUtils - Reusable, stable element interaction methods.
 * Wraps Selenium actions with logging and wait integration.
 */
public class ElementUtils {

    private static final Logger log = LogManager.getLogger(ElementUtils.class);

    private ElementUtils() {}

    public static void click(By locator) {
        WebElement el = WaitUtils.waitForClickability(locator);
        log.info("Clicking element: {}", locator);
        el.click();
    }

    public static void click(WebElement element) {
        WaitUtils.waitForClickability(element);
        log.info("Clicking WebElement: {}", element.getTagName());
        element.click();
    }

    public static void jsClick(WebElement element) {
        log.info("JS-clicking element");
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        js.executeScript("arguments[0].click();", element);
    }

    public static void type(By locator, String text) {
        WebElement el = WaitUtils.waitForVisibility(locator);
        el.clear();
        log.info("Typing '{}' into: {}", text, locator);
        el.sendKeys(text);
    }

    public static void type(WebElement element, String text) {
        WaitUtils.waitForVisibility(element);
        element.clear();
        element.sendKeys(text);
    }

    public static String getText(By locator) {
        String text = WaitUtils.waitForVisibility(locator).getText().trim();
        log.debug("Got text '{}' from: {}", text, locator);
        return text;
    }

    public static String getText(WebElement element) {
        return WaitUtils.waitForVisibility(element).getText().trim();
    }

    public static String getAttribute(By locator, String attribute) {
        return WaitUtils.waitForVisibility(locator).getAttribute(attribute);
    }

    public static boolean isDisplayed(By locator) {
        try {
            return DriverManager.getDriver().findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    public static boolean isEnabled(By locator) {
        try {
            return WaitUtils.waitForVisibility(locator).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public static void selectByVisibleText(By locator, String text) {
        Select select = new Select(WaitUtils.waitForVisibility(locator));
        log.info("Selecting '{}' from dropdown: {}", text, locator);
        select.selectByVisibleText(text);
    }

    public static void selectByValue(By locator, String value) {
        Select select = new Select(WaitUtils.waitForVisibility(locator));
        select.selectByValue(value);
    }

    public static List<String> getAllTexts(By locator) {
        List<WebElement> elements = WaitUtils.waitForVisibilityOfAll(locator);
        return elements.stream()
                .map(e -> e.getText().trim())
                .collect(Collectors.toList());
    }

    public static List<WebElement> findElements(By locator) {
        return WaitUtils.waitForPresenceOfAll(locator);
    }

    public static void scrollIntoView(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    public static void scrollToBottom() {
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    public static void hoverOver(WebElement element) {
        Actions actions = new Actions(DriverManager.getDriver());
        actions.moveToElement(element).perform();
    }

    public static void clearAndType(By locator, String text) {
        WebElement el = WaitUtils.waitForVisibility(locator);
        el.sendKeys(Keys.CONTROL + "a");
        el.sendKeys(Keys.DELETE);
        el.sendKeys(text);
    }

    public static int getElementCount(By locator) {
        try {
            return DriverManager.getDriver().findElements(locator).size();
        } catch (Exception e) {
            return 0;
        }
    }
}
