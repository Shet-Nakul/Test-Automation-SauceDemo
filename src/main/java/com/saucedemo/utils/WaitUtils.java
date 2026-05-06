package com.saucedemo.utils;

import com.saucedemo.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * WaitUtils - Centralized explicit wait strategies.
 * All waits are backed by WebDriverWait using config-driven timeouts.
 */
public class WaitUtils {

    private static final Logger log = LogManager.getLogger(WaitUtils.class);
    private static final int DEFAULT_TIMEOUT = ConfigReader.getInstance().getExplicitWait();

    private WaitUtils() {}

    private static WebDriverWait getWait(int timeoutSeconds) {
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutSeconds));
    }

    public static WebElement waitForVisibility(By locator) {
        log.debug("Waiting for visibility: {}", locator);
        return getWait(DEFAULT_TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForVisibility(WebElement element) {
        return getWait(DEFAULT_TIMEOUT).until(ExpectedConditions.visibilityOf(element));
    }

    public static WebElement waitForClickability(By locator) {
        log.debug("Waiting for clickability: {}", locator);
        return getWait(DEFAULT_TIMEOUT).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForClickability(WebElement element) {
        return getWait(DEFAULT_TIMEOUT).until(ExpectedConditions.elementToBeClickable(element));
    }

    public static boolean waitForInvisibility(By locator) {
        log.debug("Waiting for invisibility: {}", locator);
        return getWait(DEFAULT_TIMEOUT).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static List<WebElement> waitForPresenceOfAll(By locator) {
        log.debug("Waiting for all elements: {}", locator);
        return getWait(DEFAULT_TIMEOUT).until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public static List<WebElement> waitForVisibilityOfAll(By locator) {
        return getWait(DEFAULT_TIMEOUT).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public static boolean waitForUrlContains(String urlFragment) {
        log.debug("Waiting for URL to contain: {}", urlFragment);
        return getWait(DEFAULT_TIMEOUT).until(ExpectedConditions.urlContains(urlFragment));
    }

    public static boolean waitForTitleContains(String title) {
        return getWait(DEFAULT_TIMEOUT).until(ExpectedConditions.titleContains(title));
    }

    public static String waitForTextPresent(By locator) {
        WebElement el = waitForVisibility(locator);
        getWait(DEFAULT_TIMEOUT).until(
                driver -> !el.getText().trim().isEmpty()
        );
        return el.getText().trim();
    }

    public static <T> T waitForCondition(ExpectedCondition<T> condition, int timeoutSeconds) {
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutSeconds))
                .until(condition);
    }

    public static void hardWait(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
