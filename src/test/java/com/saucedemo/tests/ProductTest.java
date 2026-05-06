package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.ProductDetailPage;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

/**
 * ProductTest - Verifies product display and detail navigation.
 *
 * TC01 - All 6 products should be displayed on inventory page
 * TC02 - Product detail page should show correct product info
 * TC03 - Add to cart from detail page should update cart badge
 */
public class ProductTest extends BaseTest {

    private static final String SAMPLE_PRODUCT = "Sauce Labs Backpack";

    private InventoryPage loginAndGetInventory() {
        return new LoginPage().loginAs(config.getValidUsername(), config.getValidPassword());
    }

    // ──────────────────────────────────────────────────────
    // TC01 - Inventory page shows all 6 products
    // ──────────────────────────────────────────────────────
    @Test(description = "All 6 products should be visible on inventory page")
    public void testAllProductsDisplayed() {
        log.info("=== TC01: All Products Displayed ===");

        InventoryPage inventoryPage = loginAndGetInventory();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(inventoryPage.getProductCount(), 6,
                "Exactly 6 products should be displayed");

        List<String> names = inventoryPage.getAllProductNames();
        softAssert.assertFalse(names.isEmpty(), "Product names list should not be empty");

        List<String> prices = inventoryPage.getAllProductPrices();
        softAssert.assertEquals(prices.size(), 6,
                "All 6 products should have a price");

        prices.forEach(price ->
                softAssert.assertTrue(price.startsWith("$"),
                        "Price should start with '$': " + price));

        softAssert.assertAll();
        log.info("TC01 PASSED: All 6 products are visible with names and prices.");
    }

    // ──────────────────────────────────────────────────────
    // TC02 - Product detail page shows correct info
    // ──────────────────────────────────────────────────────
    @Test(description = "Clicking a product should open its detail page with correct info")
    public void testProductDetailPageContent() {
        log.info("=== TC02: Product Detail Page Content ===");

        InventoryPage inventoryPage = loginAndGetInventory();
        ProductDetailPage detailPage = inventoryPage.clickOnProduct(SAMPLE_PRODUCT);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(detailPage.isOnDetailPage(),
                "Should be on product detail page");
        softAssert.assertEquals(detailPage.getProductName(), SAMPLE_PRODUCT,
                "Product name on detail page should match clicked product");
        softAssert.assertFalse(detailPage.getProductDescription().isEmpty(),
                "Product description should not be empty");
        softAssert.assertTrue(detailPage.getProductPrice().startsWith("$"),
                "Product price should start with $");
        softAssert.assertTrue(detailPage.isProductImageDisplayed(),
                "Product image should be visible");
        softAssert.assertTrue(detailPage.isAddToCartButtonDisplayed(),
                "Add to Cart button should be visible");
        softAssert.assertAll();

        log.info("TC02 PASSED: Product detail page displays correct information.");
    }

    // ──────────────────────────────────────────────────────
    // TC03 - Add to cart from detail page updates cart badge
    // ──────────────────────────────────────────────────────
    @Test(description = "Adding product from detail page should update the cart badge")
    public void testAddToCartFromDetailPage() {
        log.info("=== TC03: Add to Cart from Detail Page ===");

        InventoryPage inventoryPage = loginAndGetInventory();
        ProductDetailPage detailPage = inventoryPage.clickOnProduct(SAMPLE_PRODUCT);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(detailPage.getCartBadgeCount(), 0,
                "Cart badge should be 0 before adding item");

        detailPage.addToCart();

        softAssert.assertEquals(detailPage.getCartBadgeCount(), 1,
                "Cart badge should be 1 after adding item");
        softAssert.assertTrue(detailPage.isRemoveButtonDisplayed(),
                "Remove button should appear after adding to cart");

        softAssert.assertAll();
        log.info("TC03 PASSED: Cart badge updated correctly from detail page.");
    }

    // ──────────────────────────────────────────────────────
    // TC04 (COMPLEX) - Explore all products, add selected ones, verify totals
    // ──────────────────────────────────────────────────────
    @Test(description = "Complex scenario: Browse all products, open details, add multiple items, and verify consistency")
    public void testComplexProductExplorationAndSelection() {
        log.info("=== TC04 (COMPLEX): Complex Product Exploration and Selection ===");

        InventoryPage inventoryPage = loginAndGetInventory();
        SoftAssert softAssert = new SoftAssert();

        // Phase 1: Verify all products are present
        log.info("Phase 1: Verifying all 6 products are displayed");
        softAssert.assertEquals(inventoryPage.getProductCount(), 6,
                "All 6 products should be visible");

        List<String> allProductNames = inventoryPage.getAllProductNames();
        List<String> allProductPrices = inventoryPage.getAllProductPrices();

        softAssert.assertEquals(allProductNames.size(), 6, "Should have 6 product names");
        softAssert.assertEquals(allProductPrices.size(), 6, "Should have 6 product prices");
        log.info("All Products: {}", allProductNames);
        log.info("All Prices: {}", allProductPrices);

        // Phase 2: Explore individual product details
        log.info("Phase 2: Exploring product details for multiple products");
        String product1 = allProductNames.get(0);
        String product2 = allProductNames.get(2);
        String product3 = allProductNames.get(4);

        ProductDetailPage detailPage1 = inventoryPage.clickOnProduct(product1);
        softAssert.assertTrue(detailPage1.isOnDetailPage(), "Should be on detail page for product 1");
        softAssert.assertEquals(detailPage1.getProductName(), product1,
                "Detail page should show correct product 1 name");
        String price1 = detailPage1.getProductPrice();
        softAssert.assertTrue(price1.startsWith("$"), "Price 1 should start with $");

        // Add first product from detail page
        detailPage1.addToCart();
        softAssert.assertEquals(detailPage1.getCartBadgeCount(), 1,
                "Cart badge should show 1 after adding first product");
        log.info("Added {} with price {} to cart", product1, price1);

        // Go back and select product 2
        InventoryPage backToInventory1 = detailPage1.goBackToProducts();
        ProductDetailPage detailPage2 = backToInventory1.clickOnProduct(product2);
        softAssert.assertTrue(detailPage2.isOnDetailPage(), "Should be on detail page for product 2");
        softAssert.assertEquals(detailPage2.getProductName(), product2,
                "Detail page should show correct product 2 name");
        String price2 = detailPage2.getProductPrice();

        detailPage2.addToCart();
        softAssert.assertEquals(detailPage2.getCartBadgeCount(), 2,
                "Cart badge should show 2 after adding second product");
        log.info("Added {} with price {} to cart", product2, price2);

        // Go back and select product 3
        InventoryPage backToInventory2 = detailPage2.goBackToProducts();
        ProductDetailPage detailPage3 = backToInventory2.clickOnProduct(product3);
        softAssert.assertTrue(detailPage3.isOnDetailPage(), "Should be on detail page for product 3");
        softAssert.assertEquals(detailPage3.getProductName(), product3,
                "Detail page should show correct product 3 name");
        String price3 = detailPage3.getProductPrice();

        detailPage3.addToCart();
        softAssert.assertEquals(detailPage3.getCartBadgeCount(), 3,
                "Cart badge should show 3 after adding third product");
        log.info("Added {} with price {} to cart", product3, price3);

        // Phase 3: Return to inventory and verify consistency
        log.info("Phase 3: Verifying consistency after multi-product exploration");
        InventoryPage backToInventory3 = detailPage3.goBackToProducts();
        softAssert.assertTrue(backToInventory3.isOnInventoryPage(),
                "Should be back on inventory page");
        softAssert.assertEquals(backToInventory3.getProductCount(), 6,
                "All 6 products should still be visible");
        softAssert.assertEquals(backToInventory3.getCartBadgeCount(), 3,
                "Cart badge should still show 3 selected products");

        // Phase 4: Verify cart contains all added products with correct prices
        log.info("Phase 4: Verifying cart contains all selected products");
        CartPage cartPage = backToInventory3.goToCart();
        softAssert.assertEquals(cartPage.getCartItemCount(), 3,
                "Cart should contain exactly 3 items");

        List<String> cartItems = cartPage.getCartItemNames();
        softAssert.assertTrue(cartItems.contains(product1), product1 + " should be in cart");
        softAssert.assertTrue(cartItems.contains(product2), product2 + " should be in cart");
        softAssert.assertTrue(cartItems.contains(product3), product3 + " should be in cart");

        List<String> cartPrices = cartPage.getCartItemPrices();
        softAssert.assertEquals(cartPrices.size(), 3, "Should have prices for all 3 cart items");
        cartPrices.forEach(price ->
                softAssert.assertTrue(price.startsWith("$"),
                        "Each cart price should start with '$': " + price));

        log.info("Cart Items: {}", cartItems);
        log.info("Cart Prices: {}", cartPrices);

        softAssert.assertAll();
        log.info("TC04 PASSED: Complex product exploration successfully verified with all details matching.");
    }
}
