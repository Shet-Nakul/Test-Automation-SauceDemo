package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * CheckoutTest - End-to-end checkout flow verification.
 *
 * <p>TC01 - Complete checkout flow should show success confirmation</p>
 * <p>TC02 - Missing first name in checkout form should display error</p>
 * <p>TC03 - Order totals on overview page should be mathematically correct</p>
 */
public class CheckoutTest extends BaseTest {

    private static final String PRODUCT    = "Sauce Labs Backpack";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME  = "Doe";
    private static final String ZIP        = "12345";

    private InventoryPage loginAndGetInventory() {
        return new LoginPage().loginAs(config.getValidUsername(), config.getValidPassword());
    }

    // ──────────────────────────────────────────────────────
    // TC01 - Full checkout flow - success confirmation
    // ──────────────────────────────────────────────────────
    @Test(description = "Completing the full checkout flow should display order confirmation")
    public void testCompleteCheckoutFlow() {
        log.info("=== TC01: Complete Checkout Flow ===");

        CheckoutCompletePage completePage = loginAndGetInventory()
                .addProductToCartByName(PRODUCT)
                .goToCart()
                .proceedToCheckout()
                .fillCustomerInfo(FIRST_NAME, LAST_NAME, ZIP)
                .clickFinish();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(completePage.isOnCompletePage(),
                "Should land on checkout complete page");
        softAssert.assertTrue(completePage.isOrderSuccessful(),
                "Success header should contain 'Thank You'");
        softAssert.assertFalse(completePage.getCompleteText().isEmpty(),
                "Confirmation text should not be empty");
        softAssert.assertTrue(completePage.isPonyExpressImageDisplayed(),
                "Confirmation image should be visible");
        softAssert.assertTrue(completePage.isBackHomeButtonDisplayed(),
                "Back Home button should be displayed");
        softAssert.assertAll();

        log.info("TC01 PASSED: Full checkout completed successfully.");
    }

    // ──────────────────────────────────────────────────────
    // TC02 - Missing first name validation
    // ──────────────────────────────────────────────────────
    @Test(description = "Omitting first name in checkout form should display validation error")
    public void testCheckoutWithMissingFirstName() {
        log.info("=== TC02: Checkout Missing First Name Validation ===");

        CheckoutStepOnePage stepOnePage = loginAndGetInventory()
                .addProductToCartByName(PRODUCT)
                .goToCart()
                .proceedToCheckout()
                .enterLastName(LAST_NAME)
                .enterPostalCode(ZIP)
                .clickContinueExpectingError();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(stepOnePage.isErrorMessageDisplayed(),
                "Error message should appear when first name is missing");
        softAssert.assertTrue(
                stepOnePage.getErrorMessage().contains("First Name is required"),
                "Error should say 'First Name is required'");
        softAssert.assertTrue(stepOnePage.isOnStepOnePage(),
                "Should remain on checkout step one page");
        softAssert.assertAll();

        log.info("TC02 PASSED: Missing first name validation works correctly.");
    }

    // ──────────────────────────────────────────────────────
    // TC03 - Order total = item total + tax
    // ──────────────────────────────────────────────────────
    @Test(description = "Order total on overview page should equal item total plus tax")
    public void testOrderTotalCalculation() {
        log.info("=== TC03: Order Total Calculation ===");

        CheckoutStepTwoPage overviewPage = loginAndGetInventory()
                .addProductToCartByName(PRODUCT)
                .goToCart()
                .proceedToCheckout()
                .fillCustomerInfo(FIRST_NAME, LAST_NAME, ZIP);

        double itemTotal   = overviewPage.getItemTotalValue();
        double tax         = overviewPage.getTaxValue();
        double orderTotal  = overviewPage.getOrderTotalValue();
        double expectedTotal = Math.round((itemTotal + tax) * 100.0) / 100.0;

        log.info("Item Total: {} | Tax: {} | Order Total: {} | Expected: {}",
                itemTotal, tax, orderTotal, expectedTotal);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(orderTotal, expectedTotal,
                "Order total should equal item total + tax");
        softAssert.assertTrue(itemTotal > 0, "Item total should be greater than zero");
        softAssert.assertTrue(tax > 0,       "Tax should be greater than zero");
        softAssert.assertAll();

        log.info("TC03 PASSED: Order total calculation is mathematically correct.");
    }

    // ──────────────────────────────────────────────────────
    // TC04 (COMPLEX) - Multi-product checkout with error handling
    // ──────────────────────────────────────────────────────
    @Test(description = "Complex checkout: Multiple products, form validation errors, recovery, and success")
    public void testComplexCheckoutWithMultiProductsAndErrorRecovery() {
        log.info("=== TC04 (COMPLEX): Multi-Product Checkout with Error Recovery ===");

        InventoryPage inventoryPage = loginAndGetInventory();
        SoftAssert softAssert = new SoftAssert();

        // Phase 1: Add multiple products to cart
        log.info("Phase 1: Adding 4 products to cart");
        inventoryPage
                .addProductToCartByName(PRODUCT)
                .addProductToCartByName("Sauce Labs Bike Light")
                .addProductToCartByName("Sauce Labs Fleece Jacket")
                .addProductToCartByName("Sauce Labs Onesie");

        softAssert.assertEquals(inventoryPage.getCartBadgeCount(), 4,
                "Cart badge should show 4 products before checkout");
        log.info("Phase 1 PASSED: 4 products added to cart");

        // Phase 2: Navigate to checkout and attempt with missing information
        log.info("Phase 2: Attempting checkout with missing first name");
        CheckoutStepOnePage stepOnePage = inventoryPage
                .goToCart()
                .proceedToCheckout();

        // Try with only last name and zip (missing first name)
        stepOnePage.enterLastName(LAST_NAME).enterPostalCode(ZIP);
        CheckoutStepOnePage errorPage = stepOnePage.clickContinueExpectingError();

        softAssert.assertTrue(errorPage.isErrorMessageDisplayed(),
                "Error should display for missing first name");
        softAssert.assertTrue(errorPage.getErrorMessage().contains("First Name"),
                "Error should mention First Name");
        softAssert.assertTrue(errorPage.isOnStepOnePage(),
                "Should remain on checkout step one");
        log.info("Phase 2 PASSED: Validation error correctly displayed and handled");

        // Phase 3: Submit form with all required fields
        log.info("Phase 3: Completing checkout form with all fields");
        CheckoutStepTwoPage stepTwoPage = errorPage
                .enterFirstName(FIRST_NAME)
                .clickContinue();

        softAssert.assertTrue(stepTwoPage.isOnOverviewPage(),
                "Should progress to checkout overview after valid form submission");
        log.info("Phase 3 PASSED: Successfully progressed to overview page");

        // Phase 4: Verify overview page shows all products and correct totals
        log.info("Phase 4: Verifying checkout overview with multiple products");
        // TODO: Verify that all 4 products are displayed on overview page
        // softAssert.assertEquals(stepTwoPage.getProductCount(), 4,
        //         "Overview page should show all 4 added products");

        double itemTotal = stepTwoPage.getItemTotalValue();
        double tax = stepTwoPage.getTaxValue();
        double orderTotal = stepTwoPage.getOrderTotalValue();

        softAssert.assertTrue(itemTotal > 0, "Item total should be greater than zero");
        softAssert.assertTrue(tax > 0, "Tax should be greater than zero");

        double expectedTotal = Math.round((itemTotal + tax) * 100.0) / 100.0;
        softAssert.assertEquals(orderTotal, expectedTotal,
                "Order total should equal item total + tax");

        log.info("Item Total: ${} | Tax: ${} | Order Total: ${}",
                itemTotal, tax, orderTotal);
        log.info("Phase 4 PASSED: Overview totals verified correctly");

        // Phase 5: Complete the purchase
        log.info("Phase 5: Completing purchase");
        CheckoutCompletePage completePage = stepTwoPage.clickFinish();

        softAssert.assertTrue(completePage.isOnCompletePage(),
                "Should land on checkout complete page after finishing");
        softAssert.assertTrue(completePage.isOrderSuccessful(),
                "Order success message should be displayed");
        softAssert.assertTrue(completePage.isPonyExpressImageDisplayed(),
                "Confirmation image should be visible");
        log.info("Phase 5 PASSED: Purchase completed successfully");

        // Phase 6: Verify we can return to home
        log.info("Phase 6: Returning to home and verifying fresh session");
        InventoryPage backHome = completePage.clickBackHome();
        softAssert.assertTrue(backHome.isOnInventoryPage(),
                "Should return to inventory after clicking Back Home");
        softAssert.assertEquals(backHome.getProductCount(), 6,
                "Fresh inventory should show all 6 products");
        softAssert.assertFalse(backHome.isCartBadgeDisplayed(),
                "Cart should be empty after successful checkout");
        log.info("Phase 6 PASSED: Returned to inventory with clean session");

        softAssert.assertAll();
        log.info("TC04 PASSED: Complex multi-product checkout with error recovery completed successfully.");
    }
}
