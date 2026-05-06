# Complex Test Cases Summary

This document describes the complex test cases (TC04) added to each test class in the SauceDemo Automation Framework.

## Overview

One advanced complex test case has been added to each of the 7 test classes, providing more sophisticated scenarios that combine multiple features and edge cases.

---

## 1. LoginTest - TC04 (COMPLEX)

### Test Name: `testMultipleFailedLoginAttemptsFollowedBySuccessfulLogin()`

**Description:** Tests the application's ability to handle multiple sequential failed login attempts before succeeding.

**Scenario:**
- **Attempt 1:** Wrong password with valid username (expects "Username and password do not match" error)
- **Attempt 2:** Non-existent username (expects appropriate error)
- **Attempt 3:** Locked-out user (expects "locked out" error message)
- **Attempt 4:** Successful login with valid credentials after multiple failures

**Key Validations:**
- Error messages are displayed correctly for each failure scenario
- Application recovers properly after multiple failed attempts
- Successful login is possible even after multiple failures
- Inventory page displays correctly after recovery

**Business Value:** Ensures robust error handling and session recovery after authentication failures.

---

## 2. CartTest - TC04 (COMPLEX)

### Test Name: `testComplexCartOperationsWithPriceVerification()`

**Description:** Comprehensive cart management test combining add, remove, and persistence with price verification.

**Scenario (7 Phases):**
1. Add 5 different products to cart
2. Verify cart content and prices
3. Remove 2 specific products and verify badge update
4. Navigate away and back to inventory (cart persistence test)
5. Add another product from inventory
6. Return to cart and verify final count
7. Validate all prices match expected format

**Key Validations:**
- Cart badge accurately reflects product counts
- Prices maintain correct format throughout operations
- Cart persists when navigating between pages
- Multiple add/remove operations work correctly
- Price integrity maintained across operations

**Business Value:** Ensures reliable cart functionality with accurate counts and price tracking.

---

## 3. ProductTest - TC04 (COMPLEX)

### Test Name: `testComplexProductExplorationAndSelection()`

**Description:** Multi-product exploration scenario with detail page navigation and consistency verification.

**Scenario (4 Phases):**
1. Verify all 6 products display with names and prices
2. Navigate to 3 different product detail pages sequentially
3. Add each product from its detail page to cart
4. Verify all 3 products appear in cart with correct details

**Key Validations:**
- All 6 products display on inventory page
- Product detail pages show correct information
- Cart badge updates correctly from detail pages
- Cart contains all added products with matching names and prices
- Navigation between detail pages preserves cart state

**Business Value:** Validates complete product browsing journey and cart consistency.

---

## 4. CheckoutTest - TC04 (COMPLEX)

### Test Name: `testComplexCheckoutWithMultiProductsAndErrorRecovery()`

**Description:** End-to-end checkout with 4 products, validation errors, recovery, and order completion.

**Scenario (6 Phases):**
1. Add 4 products to cart
2. Attempt checkout with incomplete form (missing first name)
3. Verify validation error and correct input
4. Navigate to overview page and verify all products with totals
5. Validate mathematical correctness of order total (item total + tax)
6. Complete purchase and return to home with verification

**Key Validations:**
- Form validation works correctly
- Error recovery allows form resubmission
- All 4 products appear on checkout overview
- Order total = Item Total + Tax (mathematically verified)
- Successful completion clears cart
- Post-checkout inventory shows fresh state

**Business Value:** Ensures complete checkout flow with proper validation and calculation accuracy.

---

## 5. NavigationTest - TC04 (COMPLEX)

### Test Name: `testComplexMultiStepNavigationWithStatePreservation()`

**Description:** Complex navigation with multiple back-and-forth operations across all pages maintaining state.

**Scenario (8 Phases):**
1. Add 3 products from inventory
2. Navigate to product detail pages and back (multiple times)
3. Navigate to cart and verify content
4. Continue shopping back to inventory
5. Add more products and return to cart
6. Remove items and return to inventory
7. Start checkout and cancel
8. Final verification of cart integrity

**Key Validations:**
- Cart badge persists across all navigation
- Product detail pages don't lose cart state
- Can navigate multiple times between pages
- Cart items maintained after removal on various pages
- Cancelling checkout returns to inventory with cart intact
- Final cart count matches expected

**Business Value:** Ensures robust navigation with state preservation across complex user journeys.

---

## 6. LogoutTest - TC04 (COMPLEX)

### Test Name: `testComplexLogoutScenariosDuringVariousStates()`

**Description:** Tests logout from multiple application states with session cleanup verification.

**Scenario (5 Scenarios):**
1. **Scenario 1:** Logout from empty cart state
2. **Scenario 2:** Add items, logout, re-login, verify cart is cleared
3. **Scenario 3:** Logout from product detail context
4. **Scenario 4:** Logout after starting checkout (cancel first), verify session clean
5. **Scenario 5:** Multiple login/logout cycles (3 iterations)

**Key Validations:**
- Logout accessible from inventory page
- Cart cleared after logout and re-login
- Session data not persisted across logout
- Multiple logout/re-login cycles work correctly
- Fresh inventory appears after each re-login
- No checkout data persists after logout

**Business Value:** Validates proper session management and security through logout.

---

## 7. SortingTest - TC04 (COMPLEX)

### Test Name: `testComplexSortingWithCartPersistenceAndTotalsVerification()`

**Description:** Multi-sort operations with cart persistence and checkout verification.

**Scenario (7 Phases):**
1. Sort by Name (A-Z) and add 2 products
2. Change sort to Name (Z-A), verify cart persists, add 1 more
3. Sort by Price (low to high), verify cart with 3 items
4. Sort by Price (high to low), verify cart still intact
5. Add 2 more products from high-to-low view (5 total)
6. Verify all 5 products in cart with correct format prices
7. Checkout with 5 products, verify totals independent of sort order

**Key Validations:**
- Sorting works correctly for all 4 sort options
- Cart persists through all sort changes
- Products correctly identified across different sort orders
- Cart contains 5 items with valid prices
- Checkout totals calculated correctly (Item Total + Tax = Order Total)
- Totals independent of sort order used

**Business Value:** Ensures sorting functionality doesn't impact cart or checkout calculations.

---

## Execution Summary

All 7 test classes now contain:
- **3 standard test cases (TC01-TC03):** Basic functionality tests
- **1 complex test case (TC04):** Advanced multi-phase scenarios

### Total Test Cases: 28 (7 classes × 4 test cases each)

### Compilation Status: ✅ ALL TESTS COMPILE SUCCESSFULLY

---

## How to Run the Tests

### Run all tests:
```bash
mvn clean test
```

### Run specific test class:
```bash
mvn clean test -Dtest=LoginTest
```

### Run specific complex test case:
```bash
mvn clean test -Dtest=CartTest#testComplexCartOperationsWithPriceVerification
```

### Run with TestNG XML configuration:
```bash
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/testng.xml
```

---

## Test Framework Features Used

- **SoftAssert:** Multiple assertions without stopping on first failure
- **Page Object Model:** Clean separation of test logic and page interactions
- **Method Chaining:** Fluent API for readable test scenarios
- **Logging:** Comprehensive logging with INFO level phase tracking
- **Data-Driven:** Tests handle dynamic product lists (not hardcoded)

---

## Key Testing Patterns

1. **Multi-Phase Testing:** Breaking complex scenarios into distinct phases
2. **State Preservation:** Verifying state persists across navigation
3. **Error Recovery:** Testing error handling and recovery paths
4. **Mathematical Validation:** Verifying calculations (order totals, prices)
5. **Navigation Cycles:** Testing back-and-forth navigation
6. **Session Management:** Verifying logout and re-login flows

---

## Notes

- All tests follow the existing project naming conventions and structure
- Tests use existing page objects and utility methods
- No new dependencies were added
- Tests are compatible with TestNG 7.x framework
- Each complex test case is independent and can run in isolation

