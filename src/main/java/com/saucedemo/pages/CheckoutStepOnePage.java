package com.saucedemo.pages;

import com.saucedemo.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * CheckoutStepOnePage - Represents checkout step one: customer information.
 */
public class CheckoutStepOnePage extends BasePage {

    // ──────────────────── Locators ────────────────────
    private static final By PAGE_TITLE      = By.cssSelector(".title");
    private static final By FIRST_NAME      = By.id("first-name");
    private static final By LAST_NAME       = By.id("last-name");
    private static final By POSTAL_CODE     = By.id("postal-code");
    private static final By CONTINUE_BTN    = By.id("continue");
    private static final By CANCEL_BTN      = By.id("cancel");
    private static final By ERROR_MESSAGE   = By.cssSelector("[data-test='error']");

    @FindBy(id = "first-name")   private WebElement firstNameField;
    @FindBy(id = "last-name")    private WebElement lastNameField;
    @FindBy(id = "postal-code")  private WebElement postalCodeField;
    @FindBy(id = "continue")     private WebElement continueButton;

    // ──────────────────── Actions ────────────────────

    public CheckoutStepOnePage enterFirstName(String firstName) {
        log.info("Entering first name: {}", firstName);
        type(firstNameField, firstName);
        return this;
    }

    public CheckoutStepOnePage enterLastName(String lastName) {
        log.info("Entering last name: {}", lastName);
        type(lastNameField, lastName);
        return this;
    }

    public CheckoutStepOnePage enterPostalCode(String postalCode) {
        log.info("Entering postal code: {}", postalCode);
        type(postalCodeField, postalCode);
        return this;
    }

    public CheckoutStepTwoPage clickContinue() {
        log.info("Clicking Continue on checkout step one");
        click(continueButton);
        waitForUrl("checkout-step-two");
        return new CheckoutStepTwoPage();
    }

    public CheckoutStepOnePage clickContinueExpectingError() {
        click(continueButton);
        return this;
    }

    public CartPage clickCancel() {
        click(CANCEL_BTN);
        waitForUrl("cart");
        return new CartPage();
    }

    public CheckoutStepTwoPage fillCustomerInfo(String firstName, String lastName, String postalCode) {
        return enterFirstName(firstName)
                .enterLastName(lastName)
                .enterPostalCode(postalCode)
                .clickContinue();
    }

    // ──────────────────── Assertions ────────────────────

    public String getPageTitle() {
        return getText(PAGE_TITLE);
    }

    public boolean isErrorMessageDisplayed() {
        return isDisplayed(ERROR_MESSAGE);
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public boolean isOnStepOnePage() {
        return getCurrentUrl().contains("checkout-step-one");
    }
}
