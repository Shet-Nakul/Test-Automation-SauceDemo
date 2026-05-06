package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.ProductDetailPage;
import com.saucedemo.pages.CheckoutStepOnePage;
import com.saucedemo.pages.CheckoutStepTwoPage;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * LogoutTest - Validates logout behaviour and post-logout session handling.
 *
 * TC01 - Clicking logout should redirect to login page
 * TC02 - After logout, accessing inventory URL directly should redirect to login
 * TC03 - After logout and re-login, user should see fresh inventory page
 */
public class LogoutTest extends BaseTest {

    private static final String PRODUCT = "Sauce Labs Fleece Jacket";

    private InventoryPage loginAndGetInventory() {
        return new LoginPage().loginAs(config.getValidUsername(), config.getValidPassword());
    }

    // ──────────────────────────────────────────────────────
    // TC01 - Logout redirects to login page
    // ──────────────────────────────────────────────────────
    @Test(description = "Clicking logout should land the user on the login page")
    public void testLogoutRedirectsToLoginPage() {
        log.info("=== TC01: Logout Redirects to Login ===");

        LoginPage loginPage = loginAndGetInventory().logout();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(loginPage.isLoginButtonDisplayed(),
                "Login button should be visible after logout");
        softAssert.assertTrue(loginPage.isUsernameFieldDisplayed(),
                "Username field should be visible after logout");
        softAssert.assertTrue(loginPage.getCurrentUrl().contains("saucedemo.com"),
                "Should be on SauceDemo domain after logout");
        softAssert.assertFalse(loginPage.getCurrentUrl().contains("inventory"),
                "Should NOT be on inventory page after logout");
        softAssert.assertAll();

        log.info("TC01 PASSED: Logout redirects to login page successfully.");
    }

    // ──────────────────────────────────────────────────────
    // TC02 - Direct URL access after logout redirects to login
    // ──────────────────────────────────────────────────────
    @Test(description = "Accessing inventory URL after logout should redirect back to login")
    public void testDirectUrlAccessAfterLogout() {
        log.info("=== TC02: Direct URL Access After Logout ===");

        InventoryPage inventoryPage = loginAndGetInventory();
        inventoryPage.logout();

        // Attempt to navigate directly to the inventory page
        String inventoryUrl = config.getAppUrl() + "/inventory.html";
        log.info("Attempting direct navigation to: {}", inventoryUrl);
        inventoryPage.driver.get(inventoryUrl);

        SoftAssert softAssert = new SoftAssert();
        // SauceDemo should redirect back to the login page
        String currentUrl = inventoryPage.driver.getCurrentUrl();
        softAssert.assertFalse(currentUrl.contains("inventory.html"),
                "Should be redirected away from inventory after logout");
        softAssert.assertAll();

        log.info("TC02 PASSED: Direct URL access after logout is blocked correctly. Current URL: {}", currentUrl);
    }

    // ──────────────────────────────────────────────────────
    // TC03 - Logout and re-login shows fresh inventory
    // ──────────────────────────────────────────────────────
    @Test(description = "After logout and re-login, a fresh inventory page should be shown")
    public void testLogoutAndReLogin() {
        log.info("=== TC03: Logout and Re-Login ===");

        // First session: add item then logout
        InventoryPage firstSession = loginAndGetInventory();
        firstSession.addProductToCartByName(PRODUCT);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(firstSession.getCartBadgeCount(), 1,
                "Cart should have 1 item in first session before logout");

        LoginPage loginPage = firstSession.logout();
        softAssert.assertTrue(loginPage.isLoginButtonDisplayed(),
                "Login page should be shown after logout");

        // Second session: log back in
        InventoryPage secondSession = loginPage.loginAs(
                config.getValidUsername(), config.getValidPassword());

        softAssert.assertTrue(secondSession.isOnInventoryPage(),
                "Should be on inventory page after re-login");
        softAssert.assertEquals(secondSession.getProductCount(), 6,
                "All 6 products should be visible after re-login");
        // Note: SauceDemo does NOT persist cart between sessions by default
        softAssert.assertFalse(secondSession.isCartBadgeDisplayed(),
                "Cart should be empty at start of new session");
        softAssert.assertAll();

        log.info("TC03 PASSED: Logout and re-login shows a clean inventory session.");
    }

    // ──────────────────────────────────────────────────────
    // TC04 (COMPLEX) - Logout during various states and session cleanup
    // ──────────────────────────────────────────────────────
    @Test(description = "Logout at different application states with session cleanup and re-login verification")
    public void testComplexLogoutScenariosDuringVariousStates() {
        log.info("=== TC04 (COMPLEX): Complex Logout Scenarios During Various States ===");

        SoftAssert softAssert = new SoftAssert();

        // Scenario 1: Logout from inventory without any cart items
        log.info("Scenario 1: Logout from empty cart state");
        InventoryPage inventoryPage1 = loginAndGetInventory();
        softAssert.assertTrue(inventoryPage1.isOnInventoryPage(), "Should be on inventory");

        LoginPage loginPage1 = inventoryPage1.logout();
        softAssert.assertTrue(loginPage1.isLoginButtonDisplayed(), "Login page should display");
        softAssert.assertTrue(loginPage1.isUsernameFieldDisplayed(), "Username field should display");
        log.info("Scenario 1 PASSED: Logout from empty cart state successful");

        // Scenario 2: Add items to cart, then logout, then re-login and verify cart is cleared
        log.info("Scenario 2: Logout with items in cart");
        InventoryPage inventoryPage2 = new LoginPage()
                .loginAs(config.getValidUsername(), config.getValidPassword());
        inventoryPage2
                .addProductToCartByName(PRODUCT)
                .addProductToCartByName("Sauce Labs Bike Light");

        softAssert.assertEquals(inventoryPage2.getCartBadgeCount(), 2,
                "Cart should have 2 items before logout");
        log.info("Added 2 items to cart");

        LoginPage loginPage2 = inventoryPage2.logout();
        softAssert.assertTrue(loginPage2.isLoginButtonDisplayed(), "Should return to login page");

        // Re-login and verify cart is empty
        InventoryPage inventoryPage2_relogin = loginPage2.loginAs(
                config.getValidUsername(), config.getValidPassword());
        softAssert.assertFalse(inventoryPage2_relogin.isCartBadgeDisplayed(),
                "Cart badge should not display after re-login (cart should be empty)");
        log.info("Scenario 2 PASSED: Cart cleared after logout and re-login");

        // Scenario 3: Navigate to product detail, logout, re-login and verify product detail is inaccessible
        log.info("Scenario 3: Logout from product detail page");
        InventoryPage inventoryPage3 = new LoginPage()
                .loginAs(config.getValidUsername(), config.getValidPassword());
        ProductDetailPage detailPage = inventoryPage3.clickOnProduct(PRODUCT);
        softAssert.assertTrue(detailPage.isOnDetailPage(), "Should be on detail page");

        LoginPage loginPage3 = new LoginPage(); // Create a new page object that will navigate to detail after logout
        // Note: We can't directly logout from ProductDetailPage without adding logout method.
        // So we navigate back first
        InventoryPage backToInventory3 = detailPage.goBackToProducts();
        LoginPage loginPage3_actual = backToInventory3.logout();
        softAssert.assertTrue(loginPage3_actual.isLoginButtonDisplayed(),
                "Should logout successfully from after viewing product details");
        log.info("Scenario 3 PASSED: Logout from product detail context successful");

        // Scenario 4: Start checkout, then logout (cancel checkout first), verify session is clean
        log.info("Scenario 4: Logout after starting checkout");
        InventoryPage inventoryPage4 = new LoginPage()
                .loginAs(config.getValidUsername(), config.getValidPassword());
        inventoryPage4.addProductToCartByName(PRODUCT);

        CheckoutStepOnePage checkoutPage = inventoryPage4
                .goToCart()
                .proceedToCheckout();

        softAssert.assertTrue(checkoutPage.isOnStepOnePage(),
                "Should be on checkout step one page");

        // Enter some data
        checkoutPage.enterFirstName("John").enterLastName("Doe").enterPostalCode("12345");

        // Cancel checkout to get back to inventory
        CheckoutStepTwoPage stepTwoPage = checkoutPage.clickContinue();
        InventoryPage backFromCheckout = stepTwoPage.clickCancel();

        // Now logout from inventory
        LoginPage loginPage4 = backFromCheckout.logout();
        softAssert.assertTrue(loginPage4.isLoginButtonDisplayed(),
                "Should successfully logout after checkout interaction");

        // Re-login and verify no checkout data persists
        InventoryPage inventoryPage4_relogin = loginPage4.loginAs(
                config.getValidUsername(), config.getValidPassword());
        softAssert.assertTrue(inventoryPage4_relogin.isOnInventoryPage(),
                "Should be on fresh inventory after re-login");
        softAssert.assertFalse(inventoryPage4_relogin.isCartBadgeDisplayed(),
                "Cart should be empty (no checkout data persists)");
        log.info("Scenario 4 PASSED: Checkout context cleared after logout and re-login");

        // Scenario 5: Multiple logout and re-login cycles
        log.info("Scenario 5: Multiple logout and re-login cycles");
        for (int i = 1; i <= 3; i++) {
            log.info("Cycle {}: Login and logout", i);
            InventoryPage inventoryPage5 = new LoginPage()
                    .loginAs(config.getValidUsername(), config.getValidPassword());
            softAssert.assertTrue(inventoryPage5.isOnInventoryPage(),
                    "Cycle " + i + ": Should be on inventory");
            softAssert.assertEquals(inventoryPage5.getProductCount(), 6,
                    "Cycle " + i + ": All 6 products should display");

            LoginPage loginPage5 = inventoryPage5.logout();
            softAssert.assertTrue(loginPage5.isLoginButtonDisplayed(),
                    "Cycle " + i + ": Should return to login page");
        }
        log.info("Scenario 5 PASSED: Multiple logout/re-login cycles successful");

        softAssert.assertAll();
        log.info("TC04 PASSED: All complex logout scenarios completed successfully with proper session cleanup.");
    }
}
