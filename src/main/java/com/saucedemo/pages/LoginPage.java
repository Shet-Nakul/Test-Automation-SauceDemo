package com.saucedemo.pages;

import com.saucedemo.base.BasePage;
import com.saucedemo.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * LoginPage - Represents https://www.saucedemo.com (login screen).
 */
public class LoginPage extends BasePage {

    // ──────────────────── Locators ────────────────────
    private static final By USERNAME_INPUT    = By.id("user-name");
    private static final By PASSWORD_INPUT    = By.id("pass");
    private static final By LOGIN_BUTTON      = By.id("login-button");
    private static final By ERROR_MESSAGE     = By.cssSelector("[data-test='error']");
    private static final By ERROR_CLOSE_BTN   = By.cssSelector(".error-button");

    @FindBy(id = "user-name")       private WebElement usernameField;
    @FindBy(id = "password")        private WebElement passwordField;
    @FindBy(id = "login-button")    private WebElement loginButton;

    // ──────────────────── Actions ────────────────────

    public LoginPage enterUsername(String username) {
        log.info("Entering username: {}", username);
        type(usernameField, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        log.info("Entering password");
        type(passwordField, password);
        return this;
    }

    public InventoryPage clickLoginButton() {
        log.info("Clicking Login button");
        click(loginButton);
        waitForUrl("inventory");
        return new InventoryPage();
    }

    public LoginPage clickLoginExpectingFailure() {
        log.info("Clicking Login button (expecting failure)");
        click(loginButton);
        return this;
    }

    public InventoryPage loginAs(String username, String password) {
        return enterUsername(username)
                .enterPassword(password)
                .clickLoginButton();
    }

    public LoginPage loginExpectingError(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLoginExpectingFailure();
    }

    // ──────────────────── Assertions ────────────────────

    public boolean isErrorMessageDisplayed() {
        return isDisplayed(ERROR_MESSAGE);
    }

    public String getErrorMessage() {
        WaitUtils.waitForVisibility(ERROR_MESSAGE);
        return getText(ERROR_MESSAGE);
    }

    public boolean isLoginButtonDisplayed() {
        return isDisplayed(LOGIN_BUTTON);
    }

    public boolean isUsernameFieldDisplayed() {
        return isDisplayed(USERNAME_INPUT);
    }

    public void closeErrorMessage() {
        click(ERROR_CLOSE_BTN);
    }
}
