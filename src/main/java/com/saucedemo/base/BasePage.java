package com.saucedemo.base;

import com.saucedemo.utils.DriverManager;
import com.saucedemo.utils.ElementUtils;
import com.saucedemo.utils.JavaScriptUtils;
import com.saucedemo.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

/**
 * BasePage - All Page Objects extend this class.
 * Provides shared driver access and common page utilities.
 */
public abstract class BasePage {

    public final WebDriver driver;
    protected final Logger log = LogManager.getLogger(getClass());

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        PageFactory.initElements(driver, this);
    }

    protected void click(By locator) {
        ElementUtils.click(locator);
    }

    protected void click(WebElement element) {
        ElementUtils.click(element);
    }

    protected void type(By locator, String text) {
        ElementUtils.type(locator, text);
    }

    protected void type(WebElement element, String text) {
        ElementUtils.type(element, text);
    }

    protected String getText(By locator) {
        return ElementUtils.getText(locator);
    }

    protected String getText(WebElement element) {
        return ElementUtils.getText(element);
    }

    protected boolean isDisplayed(By locator) {
        return ElementUtils.isDisplayed(locator);
    }

    protected void waitForUrl(String urlFragment) {
        WaitUtils.waitForUrlContains(urlFragment);
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    protected void scrollToElement(WebElement element) {
        JavaScriptUtils.scrollToElement(element);
    }
}
