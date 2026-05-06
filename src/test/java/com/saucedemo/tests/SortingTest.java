package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutStepOnePage;
import com.saucedemo.pages.CheckoutStepTwoPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SortingTest - Validates all four product sort options on the inventory page.
 *
 * TC01 - Sort by Name (A to Z)
 * TC02 - Sort by Name (Z to A)
 * TC03 - Sort by Price (low to high) and Price (high to low)
 */
public class SortingTest extends BaseTest {

    private InventoryPage loginAndGetInventory() {
        return new LoginPage().loginAs(config.getValidUsername(), config.getValidPassword());
    }

    // ──────────────────────────────────────────────────────
    // TC01 - Sort A to Z
    // ──────────────────────────────────────────────────────
    @Test(description = "Products should be sorted alphabetically A to Z")
    public void testSortByNameAtoZ() {
        log.info("=== TC01: Sort by Name A to Z ===");

        InventoryPage inventoryPage = loginAndGetInventory();
        inventoryPage.sortBy("Name (A to Z)");

        List<String> displayedNames = inventoryPage.getAllProductNames();
        List<String> sortedNames    = displayedNames.stream().sorted().collect(Collectors.toList());

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(displayedNames, sortedNames,
                "Products should be sorted A to Z");
        softAssert.assertEquals(displayedNames.size(), 6,
                "All 6 products should still be shown after sort");
        softAssert.assertAll();

        log.info("TC01 PASSED: Products sorted A to Z correctly. Order: {}", displayedNames);
    }

    // ──────────────────────────────────────────────────────
    // TC02 - Sort Z to A
    // ──────────────────────────────────────────────────────
    @Test(description = "Products should be sorted reverse alphabetically Z to A")
    public void testSortByNameZtoA() {
        log.info("=== TC02: Sort by Name Z to A ===");

        InventoryPage inventoryPage = loginAndGetInventory();
        inventoryPage.sortBy("Name (Z to A)");

        List<String> displayedNames = inventoryPage.getAllProductNames();
        List<String> expectedNames  = displayedNames.stream()
                .sorted((a, b) -> b.compareTo(a))
                .collect(Collectors.toList());

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(displayedNames, expectedNames,
                "Products should be sorted Z to A");
        softAssert.assertAll();

        log.info("TC02 PASSED: Products sorted Z to A correctly. Order: {}", displayedNames);
    }

    // ──────────────────────────────────────────────────────
    // TC03 - Sort by Price Low to High and High to Low
    // ──────────────────────────────────────────────────────
    @Test(description = "Products should sort correctly by price in both directions")
    public void testSortByPrice() {
        log.info("=== TC03: Sort by Price (Low to High and High to Low) ===");

        InventoryPage inventoryPage = loginAndGetInventory();

        // ---- Low to High ----
        inventoryPage.sortBy("Price (low to high)");
        List<Double> pricesLowToHigh = parsePrices(inventoryPage.getAllProductPrices());

        SoftAssert softAssert = new SoftAssert();
        for (int i = 0; i < pricesLowToHigh.size() - 1; i++) {
            softAssert.assertTrue(
                    pricesLowToHigh.get(i) <= pricesLowToHigh.get(i + 1),
                    String.format("Price at index %d (%.2f) should be <= price at index %d (%.2f)",
                            i, pricesLowToHigh.get(i), i + 1, pricesLowToHigh.get(i + 1))
            );
        }
        log.info("Low to High prices: {}", pricesLowToHigh);

        // ---- High to Low ----
        inventoryPage.sortBy("Price (high to low)");
        List<Double> pricesHighToLow = parsePrices(inventoryPage.getAllProductPrices());

        for (int i = 0; i < pricesHighToLow.size() - 1; i++) {
            softAssert.assertTrue(
                    pricesHighToLow.get(i) >= pricesHighToLow.get(i + 1),
                    String.format("Price at index %d (%.2f) should be >= price at index %d (%.2f)",
                            i, pricesHighToLow.get(i), i + 1, pricesHighToLow.get(i + 1))
            );
        }
        log.info("High to Low prices: {}", pricesHighToLow);

        softAssert.assertAll();
        log.info("TC03 PASSED: Price sorting works correctly in both directions.");
    }

    // ──────────────────────────────────────────────────────
    // Helper: parse "$X.XX" → Double
    // ──────────────────────────────────────────────────────
    private List<Double> parsePrices(List<String> priceStrings) {
        return priceStrings.stream()
                .map(p -> Double.parseDouble(p.replace("$", "").trim()))
                .collect(Collectors.toList());
    }

    // ──────────────────────────────────────────────────────
    // TC04 (COMPLEX) - Sort cart persistence and multi-sort validation
    // ──────────────────────────────────────────────────────
    @Test(description = "Complex sorting: Multiple sort changes with cart persistence and totals verification")
    public void testComplexSortingWithCartPersistenceAndTotalsVerification() {
        log.info("=== TC04 (COMPLEX): Complex Sorting with Cart Persistence ===");

        InventoryPage inventoryPage = loginAndGetInventory();
        SoftAssert softAssert = new SoftAssert();

        // Phase 1: Sort by Name A-Z and add specific products
        log.info("Phase 1: Sorting by Name (A to Z) and adding products");
        inventoryPage.sortBy("Name (A to Z)");

        List<String> namesAtoZ = inventoryPage.getAllProductNames();
        List<String> sortedExpected = namesAtoZ.stream().sorted().collect(Collectors.toList());
        softAssert.assertEquals(namesAtoZ, sortedExpected,
                "Products should be sorted A to Z");
        log.info("Products in A-Z order: {}", namesAtoZ);

        // Add first and third products from A-Z sorted list
        String product1 = namesAtoZ.get(0);
        String product3 = namesAtoZ.get(2);
        inventoryPage
                .addProductToCartByName(product1)
                .addProductToCartByName(product3);

        softAssert.assertEquals(inventoryPage.getCartBadgeCount(), 2,
                "Cart should have 2 items after adding from A-Z sorted view");
        log.info("Phase 1 PASSED: Added {} and {}", product1, product3);

        // Phase 2: Change sort to Z-A and verify cart persists
        log.info("Phase 2: Sorting by Name (Z to A) and verifying cart persistence");
        inventoryPage.sortBy("Name (Z to A)");

        List<String> namesZtoA = inventoryPage.getAllProductNames();
        List<String> sortedZtoA = namesZtoA.stream()
                .sorted((a, b) -> b.compareTo(a))
                .collect(Collectors.toList());
        softAssert.assertEquals(namesZtoA, sortedZtoA,
                "Products should be sorted Z to A");

        softAssert.assertEquals(inventoryPage.getCartBadgeCount(), 2,
                "Cart should still have 2 items after Z-A sort");
        log.info("Phase 2 PASSED: Cart persisted after Z-A sort");

        // Add another product from Z-A sorted view
        String productFromZtoA = namesZtoA.get(1);
        inventoryPage.addProductToCartByName(productFromZtoA);
        softAssert.assertEquals(inventoryPage.getCartBadgeCount(), 3,
                "Cart should have 3 items after adding from Z-A sorted view");
        log.info("Added {} from Z-A sorted view", productFromZtoA);

        // Phase 3: Sort by Price Low to High and verify cart persists with all items
        log.info("Phase 3: Sorting by Price (low to high) and verifying cart");
        inventoryPage.sortBy("Price (low to high)");

        List<String> pricesLowToHigh = inventoryPage.getAllProductPrices();
        List<Double> parsedPrices = parsePrices(pricesLowToHigh);
        for (int i = 0; i < parsedPrices.size() - 1; i++) {
            softAssert.assertTrue(
                    parsedPrices.get(i) <= parsedPrices.get(i + 1),
                    String.format("Prices should be sorted low to high: %.2f <= %.2f",
                            parsedPrices.get(i), parsedPrices.get(i + 1))
            );
        }

        softAssert.assertEquals(inventoryPage.getCartBadgeCount(), 3,
                "Cart should still have 3 items after price low-high sort");
        log.info("Phase 3 PASSED: Cart persisted after price sort, prices verified");

        // Phase 4: Sort by Price High to Low and verify cart
        log.info("Phase 4: Sorting by Price (high to low)");
        inventoryPage.sortBy("Price (high to low)");

        List<String> pricesHighToLow = inventoryPage.getAllProductPrices();
        List<Double> parsedPricesHighToLow = parsePrices(pricesHighToLow);
        for (int i = 0; i < parsedPricesHighToLow.size() - 1; i++) {
            softAssert.assertTrue(
                    parsedPricesHighToLow.get(i) >= parsedPricesHighToLow.get(i + 1),
                    String.format("Prices should be sorted high to low: %.2f >= %.2f",
                            parsedPricesHighToLow.get(i), parsedPricesHighToLow.get(i + 1))
            );
        }

        softAssert.assertEquals(inventoryPage.getCartBadgeCount(), 3,
                "Cart should still have 3 items after price high-low sort");
        log.info("Phase 4 PASSED: Cart persisted after high-low price sort");

        // Phase 5: Add two more products from high-to-low sorted view
        log.info("Phase 5: Adding 2 more products from high-to-low sorted view");
        List<String> namesHighToLowView = inventoryPage.getAllProductNames();
        inventoryPage
                .addProductToCartByName(namesHighToLowView.get(0))
                .addProductToCartByName(namesHighToLowView.get(1));

        softAssert.assertEquals(inventoryPage.getCartBadgeCount(), 5,
                "Cart should have 5 items after adding 2 more");
        log.info("Phase 5 PASSED: Added 2 more products");

        // Phase 6: Navigate to cart and verify all 5 products are present
        log.info("Phase 6: Verifying all 5 products in cart regardless of sort order");
        CartPage cartPage = inventoryPage.goToCart();
        softAssert.assertEquals(cartPage.getCartItemCount(), 5,
                "Cart should contain exactly 5 items");

        List<String> cartItemNames = cartPage.getCartItemNames();
        List<String> cartItemPrices = cartPage.getCartItemPrices();
        softAssert.assertEquals(cartItemNames.size(), 5, "Should have 5 item names in cart");
        softAssert.assertEquals(cartItemPrices.size(), 5, "Should have 5 item prices in cart");

        // Verify prices are valid format
        cartItemPrices.forEach(price ->
                softAssert.assertTrue(price.startsWith("$"),
                        "Cart price should start with '$': " + price));

        log.info("Cart Items: {}", cartItemNames);
        log.info("Cart Prices: {}", cartItemPrices);
        log.info("Phase 6 PASSED: All 5 products verified in cart");

        // Phase 7: Proceed to checkout and verify totals are independent of sort order used
        log.info("Phase 7: Proceeding to checkout and verifying totals");
        CheckoutStepOnePage checkoutPage = cartPage.proceedToCheckout();
        CheckoutStepTwoPage overviewPage = checkoutPage
                .fillCustomerInfo("John", "Doe", "12345");

        double itemTotal = overviewPage.getItemTotalValue();
        double tax = overviewPage.getTaxValue();
        double orderTotal = overviewPage.getOrderTotalValue();
        double expectedTotal = Math.round((itemTotal + tax) * 100.0) / 100.0;

        softAssert.assertEquals(orderTotal, expectedTotal,
                "Order total should equal item total + tax");
        softAssert.assertTrue(itemTotal > 0, "Item total should be greater than zero");
        softAssert.assertTrue(tax > 0, "Tax should be greater than zero");

        log.info("Item Total: ${} | Tax: ${} | Order Total: ${}",
                itemTotal, tax, orderTotal);
        log.info("Phase 7 PASSED: Checkout totals verified independent of sort order");

        softAssert.assertAll();
        log.info("TC04 PASSED: Complex sorting scenario with cart persistence and totals verification completed successfully.");
    }
}
