package com.saucedemo.pages;

import com.saucedemo.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * CheckoutCompletePage - Represents the order confirmation page (/checkout-complete.html).
 */
public class CheckoutCompletePage extends BasePage {

    // ──────────────────── Locators ────────────────────
    private static final By PAGE_TITLE          = By.cssSelector(".title");
    private static final By COMPLETE_HEADER     = By.cssSelector(".complete-header");
    private static final By COMPLETE_TEXT       = By.cssSelector(".complete-text");
    private static final By PONY_EXPRESS_IMAGE  = By.cssSelector(".pony_express");
    private static final By BACK_HOME_BTN       = By.id("back-to-products");

    @FindBy(id = "back-to-products")
    private WebElement backHomeButton;

    @FindBy(css = ".complete-header")
    private WebElement completeHeader;

    // ──────────────────── Actions ────────────────────

    public InventoryPage clickBackHome() {
        log.info("Clicking Back Home button");
        click(backHomeButton);
        waitForUrl("inventory");
        return new InventoryPage();
    }

    // ──────────────────── Assertions ────────────────────

    public String getPageTitle() {
        return getText(PAGE_TITLE);
    }

    public String getCompleteHeader() {
        return getText(COMPLETE_HEADER);
    }

    public String getCompleteText() {
        return getText(COMPLETE_TEXT);
    }

    public boolean isOrderSuccessful() {
        return getCompleteHeader().toLowerCase().contains("thank you");
    }

    public boolean isPonyExpressImageDisplayed() {
        return isDisplayed(PONY_EXPRESS_IMAGE);
    }

    public boolean isOnCompletePage() {
        return getCurrentUrl().contains("checkout-complete");
    }

    public boolean isBackHomeButtonDisplayed() {
        return isDisplayed(BACK_HOME_BTN);
    }
}
