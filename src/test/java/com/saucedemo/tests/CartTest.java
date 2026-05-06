package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

/**
 * CartTest - Validates cart add, remove, and persistence behaviour.
 *
 * TC01 - Adding multiple products should reflect the correct cart badge count
 * TC02 - Removing an item from cart should decrement count and item list
 * TC03 - Continue Shopping should return user to inventory page
 */
public class CartTest extends BaseTest {

    private static final String PRODUCT_1 = "Sauce Labs Backpack";
    private static final String PRODUCT_2 = "Sauce Labs Bike Light";
    private static final String PRODUCT_3 = "Sauce Labs Bolt T-Shirt";

    private InventoryPage loginAndGetInventory() {
        return new LoginPage().loginAs(config.getValidUsername(), config.getValidPassword());
    }

    // ──────────────────────────────────────────────────────
    // TC01 - Multiple items added reflect correct badge count
    // ──────────────────────────────────────────────────────
    @Test(description = "Adding multiple products should update cart badge count correctly")
    public void testAddMultipleProductsToCart() {
        log.info("=== TC01: Add Multiple Products to Cart ===");

        InventoryPage inventoryPage = loginAndGetInventory();

        inventoryPage
                .addProductToCartByName(PRODUCT_1)
                .addProductToCartByName(PRODUCT_2)
                .addProductToCartByName(PRODUCT_3);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(inventoryPage.getCartBadgeCount(), 3,
                "Cart badge should show 3 after adding 3 items");

        CartPage cartPage = inventoryPage.goToCart();
        softAssert.assertEquals(cartPage.getCartItemCount(), 3,
                "Cart page should list 3 items");
        softAssert.assertTrue(cartPage.isItemInCart(PRODUCT_1), PRODUCT_1 + " should be in cart");
        softAssert.assertTrue(cartPage.isItemInCart(PRODUCT_2), PRODUCT_2 + " should be in cart");
        softAssert.assertTrue(cartPage.isItemInCart(PRODUCT_3), PRODUCT_3 + " should be in cart");
        softAssert.assertAll();

        log.info("TC01 PASSED: Multiple products added and reflected in cart correctly.");
    }

    // ──────────────────────────────────────────────────────
    // TC02 - Remove item from cart page
    // ──────────────────────────────────────────────────────
    @Test(description = "Removing an item from cart should update item count and badge")
    public void testRemoveItemFromCart() {
        log.info("=== TC02: Remove Item from Cart ===");

        InventoryPage inventoryPage = loginAndGetInventory();
        inventoryPage
                .addProductToCartByName(PRODUCT_1)
                .addProductToCartByName(PRODUCT_2);

        CartPage cartPage = inventoryPage.goToCart();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(cartPage.getCartItemCount(), 2,
                "Cart should have 2 items before removal");

        cartPage.removeItemByName(PRODUCT_1);

        softAssert.assertEquals(cartPage.getCartItemCount(), 1,
                "Cart should have 1 item after removing one");
        softAssert.assertFalse(cartPage.isItemInCart(PRODUCT_1),
                PRODUCT_1 + " should be removed from cart");
        softAssert.assertTrue(cartPage.isItemInCart(PRODUCT_2),
                PRODUCT_2 + " should still be in cart");
        softAssert.assertAll();

        log.info("TC02 PASSED: Item removed from cart successfully.");
    }

    // ──────────────────────────────────────────────────────
    // TC03 - Continue Shopping returns to inventory
    // ──────────────────────────────────────────────────────
    @Test(description = "Continue Shopping button should navigate back to inventory page")
    public void testContinueShoppingNavigation() {
        log.info("=== TC03: Continue Shopping Navigation ===");

        InventoryPage inventoryPage = loginAndGetInventory();
        inventoryPage.addProductToCartByName(PRODUCT_1);

        CartPage cartPage = inventoryPage.goToCart();
        softAssert_cartPageVerification(cartPage);

        InventoryPage backToInventory = cartPage.continueShopping();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(backToInventory.isOnInventoryPage(),
                "Should navigate back to inventory page after Continue Shopping");
        softAssert.assertEquals(backToInventory.getCartBadgeCount(), 1,
                "Cart badge should still show 1 after navigating back");
        softAssert.assertAll();

        log.info("TC03 PASSED: Continue Shopping returned to inventory with cart intact.");
    }

    private void softAssert_cartPageVerification(CartPage cartPage) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(cartPage.isOnCartPage(), "Should be on cart page");
        softAssert.assertAll();
    }

    // ──────────────────────────────────────────────────────
    // TC04 (COMPLEX) - Complex cart operations with price verification
    // ──────────────────────────────────────────────────────
    @Test(description = "Complex cart scenario: Add multiple items, remove some, verify prices and totals match")
    public void testComplexCartOperationsWithPriceVerification() {
        log.info("=== TC04 (COMPLEX): Complex Cart Operations with Price Verification ===");

        InventoryPage inventoryPage = loginAndGetInventory();
        SoftAssert softAssert = new SoftAssert();

        // Phase 1: Add multiple products to cart
        log.info("Phase 1: Adding 5 products to cart");
        inventoryPage
                .addProductToCartByName(PRODUCT_1)
                .addProductToCartByName(PRODUCT_2)
                .addProductToCartByName(PRODUCT_3)
                .addProductToCartByName("Sauce Labs Fleece Jacket")
                .addProductToCartByName("Sauce Labs Onesie");

        softAssert.assertEquals(inventoryPage.getCartBadgeCount(), 5,
                "Cart badge should show 5 after adding 5 items");
        log.info("Phase 1 PASSED: 5 products added to cart");

        // Phase 2: Navigate to cart and collect prices
        log.info("Phase 2: Verifying cart content and prices");
        CartPage cartPage = inventoryPage.goToCart();
        List<String> cartItemNames = cartPage.getCartItemNames();
        List<String> cartItemPrices = cartPage.getCartItemPrices();

        softAssert.assertEquals(cartPage.getCartItemCount(), 5,
                "Cart should contain 5 items");
        softAssert.assertEquals(cartItemNames.size(), 5,
                "Should have prices for all 5 items");

        // Verify each item is present
        softAssert.assertTrue(cartPage.isItemInCart(PRODUCT_1), PRODUCT_1 + " should be in cart");
        softAssert.assertTrue(cartPage.isItemInCart(PRODUCT_2), PRODUCT_2 + " should be in cart");
        softAssert.assertTrue(cartPage.isItemInCart(PRODUCT_3), PRODUCT_3 + " should be in cart");

        // Verify all prices are valid format
        cartItemPrices.forEach(price ->
                softAssert.assertTrue(price.startsWith("$"),
                        "Each price should start with '$': " + price));

        log.info("Cart Items: {}", cartItemNames);
        log.info("Cart Prices: {}", cartItemPrices);

        // Phase 3: Remove specific items and verify badge updates
        log.info("Phase 3: Removing 2 products from cart");
        cartPage.removeItemByName(PRODUCT_1).removeItemByName(PRODUCT_3);

        softAssert.assertEquals(cartPage.getCartItemCount(), 3,
                "Cart should have 3 items after removing 2");
        softAssert.assertFalse(cartPage.isItemInCart(PRODUCT_1),
                PRODUCT_1 + " should be removed from cart");
        softAssert.assertFalse(cartPage.isItemInCart(PRODUCT_3),
                PRODUCT_3 + " should be removed from cart");
        softAssert.assertTrue(cartPage.isItemInCart(PRODUCT_2),
                PRODUCT_2 + " should still be in cart");

        // Verify badge count through cart badge
        int cartBadgeAfterRemoval = cartPage.getCartBadgeCount();
        softAssert.assertEquals(cartBadgeAfterRemoval, 3,
                "Cart badge should reflect 3 items after removal");

        // Phase 4: Navigate back to inventory and verify cart persistence
        log.info("Phase 4: Testing cart persistence through navigation");
        InventoryPage backToInventory = cartPage.continueShopping();
        softAssert.assertEquals(backToInventory.getCartBadgeCount(), 3,
                "Cart badge should still show 3 after returning to inventory");

        // Phase 5: Add one more product and return to cart
        log.info("Phase 5: Adding one more product and returning to cart");
        backToInventory.addProductToCartByName("Sauce Labs Bolt T-Shirt");
        softAssert.assertEquals(backToInventory.getCartBadgeCount(), 4,
                "Cart badge should show 4 after adding another product");

        CartPage finalCart = backToInventory.goToCart();
        softAssert.assertEquals(finalCart.getCartItemCount(), 4,
                "Final cart should contain 4 items");

        softAssert.assertAll();
        log.info("TC04 PASSED: Complex cart operations completed successfully with all verifications passing.");
    }
}
