package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * LoginTest - Verifies login functionality for SauceDemo.
 *
 * TC01 - Valid user login should land on inventory page
 * TC02 - Locked-out user should see error message
 * TC03 - Empty credentials should display validation error
 */
public class LoginTest extends BaseTest {

    // ──────────────────────────────────────────────────────
    // TC01 - Successful login with standard_user
    // ──────────────────────────────────────────────────────
    @Test(description = "Valid user login should navigate to inventory page")
    public void testSuccessfulLogin() {
        log.info("=== TC01: Successful Login ===");

        InventoryPage inventoryPage = new LoginPage()
                .loginAs(config.getValidUsername(), config.getValidPassword());

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(inventoryPage.isOnInventoryPage(),
                "URL should contain 'inventory'");
        softAssert.assertEquals(inventoryPage.getPageTitle(), "Products",
                "Page title should be 'Products'");
        softAssert.assertTrue(inventoryPage.getProductCount() > 0,
                "At least one product should be displayed");
        softAssert.assertAll();

        log.info("TC01 PASSED: User logged in successfully and inventory is visible.");
    }

    // ──────────────────────────────────────────────────────
    // TC02 - Locked-out user should see error
    // ──────────────────────────────────────────────────────
    @Test(description = "Locked-out user should see a descriptive error message")
    public void testLockedOutUserLogin() {
        log.info("=== TC02: Locked Out User Login ===");

        LoginPage loginPage = new LoginPage()
                .loginExpectingError(config.getLockedUsername(), config.getValidPassword());

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error message should be displayed for locked user");
        softAssert.assertTrue(
                loginPage.getErrorMessage().contains("locked out"),
                "Error message should mention 'locked out'");
        softAssert.assertAll();

        log.info("TC02 PASSED: Locked-out user error displayed correctly.");
    }

    // ──────────────────────────────────────────────────────
    // TC03 - Empty credentials validation
    // ──────────────────────────────────────────────────────
    @Test(description = "Empty credentials should trigger a validation error")
    public void testEmptyCredentialsLogin() {
        log.info("=== TC03: Empty Credentials Login ===");

        LoginPage loginPage = new LoginPage()
                .clickLoginExpectingFailure();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error message should be shown when no credentials are entered");
        softAssert.assertTrue(
                loginPage.getErrorMessage().contains("Username is required"),
                "Error should say 'Username is required'");
        softAssert.assertTrue(loginPage.isLoginButtonDisplayed(),
                "Login button should still be visible after error");
        softAssert.assertAll();

        log.info("TC03 PASSED: Empty credentials validation works correctly.");
    }

    // ──────────────────────────────────────────────────────
    // TC04 (COMPLEX) - Sequential failed logins followed by successful login
    // ──────────────────────────────────────────────────────
    @Test(description = "Multiple failed login attempts followed by successful login should work correctly")
    public void testMultipleFailedLoginAttemptsFollowedBySuccessfulLogin() {
        log.info("=== TC04 (COMPLEX): Multiple Failed Login Attempts Then Success ===");

        LoginPage loginPage = new LoginPage();

        // Attempt 1: Wrong password for valid user
        log.info("Attempt 1: Trying with valid username but wrong password");
        loginPage.loginExpectingError(config.getValidUsername(), "wrong_password");
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error should be displayed for wrong password");
        softAssert.assertTrue(loginPage.getErrorMessage().contains("Username and password do not match"),
                "Error message should mention password mismatch");

        // Attempt 2: Non-existent username
        log.info("Attempt 2: Trying with non-existent username");
        loginPage = loginPage.getErrorMessage().contains("do not match") ? new LoginPage() : loginPage;
        loginPage.loginExpectingError("nonexistent_user", config.getValidPassword());
        softAssert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error should be displayed for non-existent user");

        // Attempt 3: Try locked user
        log.info("Attempt 3: Trying with locked user");
        loginPage = new LoginPage();
        loginPage.loginExpectingError(config.getLockedUsername(), config.getValidPassword());
        softAssert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error should be displayed for locked user");
        softAssert.assertTrue(loginPage.getErrorMessage().contains("locked out"),
                "Error message should mention locked out status");

        // Attempt 4: Finally successful login
        log.info("Attempt 4: Successful login after multiple failures");
        loginPage = new LoginPage();
        InventoryPage inventoryPage = loginPage.loginAs(config.getValidUsername(), config.getValidPassword());
        softAssert.assertTrue(inventoryPage.isOnInventoryPage(),
                "Should successfully navigate to inventory after multiple failed attempts");
        softAssert.assertEquals(inventoryPage.getPageTitle(), "Products",
                "Products page should display correctly after recovery from failed attempts");

        softAssert.assertAll();
        log.info("TC04 PASSED: Successfully handled multiple failed login attempts followed by successful login.");
    }
}
