package com.saucedemo.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

/**
 * JavaScriptUtils - JavaScript execution helpers for edge cases.
 */
public class JavaScriptUtils {

    private static final Logger log = LogManager.getLogger(JavaScriptUtils.class);

    private JavaScriptUtils() {}

    private static JavascriptExecutor js() {
        return (JavascriptExecutor) DriverManager.getDriver();
    }

    public static Object executeScript(String script, Object... args) {
        log.debug("Executing JS: {}", script);
        return js().executeScript(script, args);
    }

    public static void highlightElement(WebElement element) {
        String originalStyle = element.getAttribute("style");
        js().executeScript(
                "arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');",
                element);
        WaitUtils.hardWait(300);
        js().executeScript("arguments[0].setAttribute('style', arguments[1]);", element, originalStyle);
    }

    public static void scrollToElement(WebElement element) {
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    public static String getPageTitle() {
        return (String) js().executeScript("return document.title;");
    }

    public static String getCurrentUrl() {
        return (String) js().executeScript("return window.location.href;");
    }

    public static Long getScrollPosition() {
        return (Long) js().executeScript("return window.scrollY;");
    }
}
