package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * NavigationTest - Validates page-to-page navigation across the application.
 *
 * TC01 - Navigating to cart and back to inventory preserves page state
 * TC02 - Navigating from product detail back to products list works
 * TC03 - Cancel on checkout overview returns to inventory with cart intact
 */
public class NavigationTest extends BaseTest {

    private static final String PRODUCT_1 = "Sauce Labs Backpack";
    private static final String PRODUCT_2 = "Sauce Labs Bike Light";

    private InventoryPage loginAndGetInventory() {
        return new LoginPage().loginAs(config.getValidUsername(), config.getValidPassword());
    }

    // ──────────────────────────────────────────────────────
    // TC01 - Cart ↔ Inventory navigation preserves cart state
    // ──────────────────────────────────────────────────────
    @Test(description = "Navigating to cart and back to inventory should preserve cart items")
    public void testCartAndBackToInventoryNavigation() {
        log.info("=== TC01: Cart Navigation Preservation ===");

        InventoryPage inventoryPage = loginAndGetInventory();
        inventoryPage.addProductToCartByName(PRODUCT_1);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(inventoryPage.getCartBadgeCount(), 1,
                "Cart badge should be 1 before navigating to cart");

        CartPage cartPage = inventoryPage.goToCart();
        softAssert.assertTrue(cartPage.isOnCartPage(), "Should be on cart page");
        softAssert.assertTrue(cartPage.isItemInCart(PRODUCT_1),
                PRODUCT_1 + " should be in cart");

        InventoryPage backToInventory = cartPage.continueShopping();
        softAssert.assertTrue(backToInventory.isOnInventoryPage(),
                "Should return to inventory page");
        softAssert.assertEquals(backToInventory.getCartBadgeCount(), 1,
                "Cart badge should still be 1 after returning");
        softAssert.assertAll();

        log.info("TC01 PASSED: Cart navigation preserves state correctly.");
    }

    // ──────────────────────────────────────────────────────
    // TC02 - Product detail → Back to products
    // ──────────────────────────────────────────────────────
    @Test(description = "Back button on product detail should return to product list")
    public void testProductDetailBackNavigation() {
        log.info("=== TC02: Product Detail Back Navigation ===");

        InventoryPage inventoryPage = loginAndGetInventory();
        int initialProductCount = inventoryPage.getProductCount();

        ProductDetailPage detailPage = inventoryPage.clickOnProduct(PRODUCT_1);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(detailPage.isOnDetailPage(),
                "Should be on product detail page");
        softAssert.assertEquals(detailPage.getProductName(), PRODUCT_1,
                "Detail page product name should match");

        InventoryPage backToProducts = detailPage.goBackToProducts();
        softAssert.assertTrue(backToProducts.isOnInventoryPage(),
                "Should return to inventory page via Back button");
        softAssert.assertEquals(backToProducts.getProductCount(), initialProductCount,
                "All products should still be listed after navigating back");
        softAssert.assertAll();

        log.info("TC02 PASSED: Product detail back navigation works correctly.");
    }

    // ──────────────────────────────────────────────────────
    // TC03 - Cancel on checkout overview returns to inventory
    // ──────────────────────────────────────────────────────
    @Test(description = "Cancelling on checkout overview should return to inventory with cart intact")
    public void testCancelOnCheckoutOverviewNavigation() {
        log.info("=== TC03: Cancel on Checkout Overview ===");

        InventoryPage inventoryPage = loginAndGetInventory();
        inventoryPage
                .addProductToCartByName(PRODUCT_1)
                .addProductToCartByName(PRODUCT_2);

        CheckoutStepTwoPage overviewPage = inventoryPage
                .goToCart()
                .proceedToCheckout()
                .fillCustomerInfo("Jane", "Smith", "54321");

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(overviewPage.isOnOverviewPage(),
                "Should be on checkout overview page");

        InventoryPage cancelledBack = overviewPage.clickCancel();
        softAssert.assertTrue(cancelledBack.isOnInventoryPage(),
                "Cancelling checkout should return to inventory");
        softAssert.assertEquals(cancelledBack.getCartBadgeCount(), 2,
                "Cart should still have 2 items after cancelling checkout");
        softAssert.assertAll();

        log.info("TC03 PASSED: Cancel on checkout overview returns to inventory correctly.");
    }

    // ──────────────────────────────────────────────────────
    // TC04 (COMPLEX) - Complex multi-step navigation with state preservation
    // ──────────────────────────────────────────────────────
    @Test(description = "Complex navigation: Multiple back-and-forth operations preserving state throughout")
    public void testComplexMultiStepNavigationWithStatePreservation() {
        log.info("=== TC04 (COMPLEX): Complex Multi-Step Navigation ===");

        InventoryPage inventoryPage = loginAndGetInventory();
        SoftAssert softAssert = new SoftAssert();

        // Phase 1: Add products from inventory
        log.info("Phase 1: Adding multiple products from inventory");
        inventoryPage
                .addProductToCartByName(PRODUCT_1)
                .addProductToCartByName(PRODUCT_2)
                .addProductToCartByName("Sauce Labs Fleece Jacket");

        softAssert.assertEquals(inventoryPage.getCartBadgeCount(), 3,
                "Cart badge should show 3 products");
        log.info("Phase 1 PASSED: 3 products added");

        // Phase 2: Navigate to product detail and back multiple times
        log.info("Phase 2: Navigating to product details and back");
        ProductDetailPage detailPage1 = inventoryPage.clickOnProduct(PRODUCT_1);
        softAssert.assertTrue(detailPage1.isOnDetailPage(), "Should be on product detail page");
        softAssert.assertEquals(detailPage1.getCartBadgeCount(), 3,
                "Cart badge should persist on detail page");

        InventoryPage backFromDetail1 = detailPage1.goBackToProducts();
        softAssert.assertTrue(backFromDetail1.isOnInventoryPage(), "Should return to inventory");
        softAssert.assertEquals(backFromDetail1.getCartBadgeCount(), 3,
                "Cart count should remain 3 after returning from detail");

        // Navigate to a different product's detail page
        ProductDetailPage detailPage2 = backFromDetail1.clickOnProduct(PRODUCT_2);
        softAssert.assertEquals(detailPage2.getCartBadgeCount(), 3,
                "Cart badge should still show 3 on different product detail");
        InventoryPage backFromDetail2 = detailPage2.goBackToProducts();
        softAssert.assertEquals(backFromDetail2.getCartBadgeCount(), 3,
                "Cart count should remain 3 after returning again");
        log.info("Phase 2 PASSED: Detail page navigation cycles completed");

        // Phase 3: Navigate to cart
        log.info("Phase 3: Navigating to cart and verifying content");
        CartPage cartPage = backFromDetail2.goToCart();
        softAssert.assertTrue(cartPage.isOnCartPage(), "Should be on cart page");
        softAssert.assertEquals(cartPage.getCartItemCount(), 3,
                "Cart should have all 3 items");

        // Verify items in cart
        softAssert.assertTrue(cartPage.isItemInCart(PRODUCT_1), PRODUCT_1 + " should be in cart");
        softAssert.assertTrue(cartPage.isItemInCart(PRODUCT_2), PRODUCT_2 + " should be in cart");
        log.info("Phase 3 PASSED: Cart verified with all items");

        // Phase 4: Navigate back to inventory from cart
        log.info("Phase 4: Returning to inventory from cart");
        InventoryPage backFromCart1 = cartPage.continueShopping();
        softAssert.assertTrue(backFromCart1.isOnInventoryPage(), "Should return to inventory from cart");
        softAssert.assertEquals(backFromCart1.getCartBadgeCount(), 3,
                "Cart count should remain 3 after returning from cart");
        log.info("Phase 4 PASSED: Returned to inventory from cart");

        // Phase 5: Add another product and navigate to cart again
        log.info("Phase 5: Adding another product and returning to cart");
        backFromCart1.addProductToCartByName("Sauce Labs Bolt T-Shirt");
        softAssert.assertEquals(backFromCart1.getCartBadgeCount(), 4,
                "Cart badge should show 4 after adding product");

        CartPage cartPage2 = backFromCart1.goToCart();
        softAssert.assertEquals(cartPage2.getCartItemCount(), 4,
                "Cart should now have 4 items");
        log.info("Phase 5 PASSED: Added product and verified in cart");

        // Phase 6: Remove an item from cart and return to inventory
        log.info("Phase 6: Removing item from cart and returning to inventory");
        cartPage2.removeItemByName(PRODUCT_1);
        softAssert.assertEquals(cartPage2.getCartItemCount(), 3,
                "Cart should have 3 items after removal");

        InventoryPage backFromCart2 = cartPage2.continueShopping();
        softAssert.assertTrue(backFromCart2.isOnInventoryPage(), "Should return to inventory");
        softAssert.assertEquals(backFromCart2.getCartBadgeCount(), 3,
                "Cart badge should show 3 after returning from removing item");
        log.info("Phase 6 PASSED: Item removed and cart updated correctly");

        // Phase 7: Navigate to checkout flow and cancel
        log.info("Phase 7: Starting checkout flow and cancelling");
        CheckoutStepTwoPage overviewPage = backFromCart2
                .goToCart()
                .proceedToCheckout()
                .fillCustomerInfo("Jane", "Smith", "54321");

        softAssert.assertTrue(overviewPage.isOnOverviewPage(),
                "Should be on checkout overview page");

        InventoryPage cancelledBack = overviewPage.clickCancel();
        softAssert.assertTrue(cancelledBack.isOnInventoryPage(),
                "Should return to inventory after cancel");
        softAssert.assertEquals(cancelledBack.getCartBadgeCount(), 3,
                "Cart should still have 3 items after cancelling checkout");
        log.info("Phase 7 PASSED: Cancelled checkout and returned with cart intact");

        // Phase 8: Final verification - navigate back to cart
        log.info("Phase 8: Final verification - returning to cart");
        CartPage finalCart = cancelledBack.goToCart();
        softAssert.assertEquals(finalCart.getCartItemCount(), 3,
                "Final cart should have 3 items");
        log.info("Phase 8 PASSED: All items still present after complex navigation");

        softAssert.assertAll();
        log.info("TC04 PASSED: Complex multi-step navigation completed with all state preserved correctly.");
    }
}
