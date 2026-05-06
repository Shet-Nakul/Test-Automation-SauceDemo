# Complex Test Cases - Quick Reference

## Test Case Index

### 1. LoginTest
**TC04 (COMPLEX):** `testMultipleFailedLoginAttemptsFollowedBySuccessfulLogin()`
- Location: `src/test/java/com/saucedemo/tests/LoginTest.java`
- Lines: Added as TC04 after TC03
- Tests: Sequential failed logins → successful recovery
- Assertions: 9 assertions across 4 login attempts

### 2. CartTest  
**TC04 (COMPLEX):** `testComplexCartOperationsWithPriceVerification()`
- Location: `src/test/java/com/saucedemo/tests/CartTest.java`
- Tests: Add 5 items → remove 2 → persist → add 1 → verify
- Phases: 5 distinct phases
- Assertions: Price format validation + badge tracking

### 3. ProductTest
**TC04 (COMPLEX):** `testComplexProductExplorationAndSelection()`
- Location: `src/test/java/com/saucedemo/tests/ProductTest.java`
- Tests: Browse 3 products → add from details → verify cart consistency
- Phases: 4 distinct phases
- Data: Tests 1st, 3rd, and 5th products dynamically

### 4. CheckoutTest
**TC04 (COMPLEX):** `testComplexCheckoutWithMultiProductsAndErrorRecovery()`
- Location: `src/test/java/com/saucedemo/tests/CheckoutTest.java`
- Tests: 4 products → validation error → recover → complete → verify
- Phases: 6 distinct phases
- Special: Order total calculation verification

### 5. NavigationTest
**TC04 (COMPLEX):** `testComplexMultiStepNavigationWithStatePreservation()`
- Location: `src/test/java/com/saucedemo/tests/NavigationTest.java`
- Tests: Detail pages → cart → inventory cycles
- Phases: 8 distinct phases with multiple back-and-forth operations
- Focus: State preservation across all navigations

### 6. LogoutTest
**TC04 (COMPLEX):** `testComplexLogoutScenariosDuringVariousStates()`
- Location: `src/test/java/com/saucedemo/tests/LogoutTest.java`
- Tests: 5 scenarios + 3 re-login cycles
- Scenarios: Empty cart, with items, from detail, from checkout, multiple cycles
- Focus: Session cleanup and security

### 7. SortingTest
**TC04 (COMPLEX):** `testComplexSortingWithCartPersistenceAndTotalsVerification()`
- Location: `src/test/java/com/saucedemo\tests\SortingTest.java`
- Tests: A-Z sort → Z-A sort → Price LH sort → Price HL → 5 items → checkout
- Phases: 7 distinct phases
- Focus: Cart persistence through all sort operations

---

## Run Individual Complex Tests

```bash
# LoginTest
mvn clean test -Dtest=LoginTest#testMultipleFailedLoginAttemptsFollowedBySuccessfulLogin

# CartTest
mvn clean test -Dtest=CartTest#testComplexCartOperationsWithPriceVerification

# ProductTest
mvn clean test -Dtest=ProductTest#testComplexProductExplorationAndSelection

# CheckoutTest
mvn clean test -Dtest=CheckoutTest#testComplexCheckoutWithMultiProductsAndErrorRecovery

# NavigationTest
mvn clean test -Dtest=NavigationTest#testComplexMultiStepNavigationWithStatePreservation

# LogoutTest
mvn clean test -Dtest=LogoutTest#testComplexLogoutScenariosDuringVariousStates

# SortingTest
mvn clean test -Dtest=SortingTest#testComplexSortingWithCartPersistenceAndTotalsVerification
```

---

## Run All New Complex Tests Only

```bash
mvn clean test -Dtest=*Test#testComplex*
```

---

## Test Statistics

| Test Class | Standard Tests | Complex Tests | Total |
|-----------|----------------|---------------|-------|
| LoginTest | 3 | 1 | 4 |
| CartTest | 3 | 1 | 4 |
| ProductTest | 3 | 1 | 4 |
| CheckoutTest | 3 | 1 | 4 |
| NavigationTest | 3 | 1 | 4 |
| LogoutTest | 3 | 1 | 4 |
| SortingTest | 3 | 1 | 4 |
| **TOTAL** | **21** | **7** | **28** |

---

## Key Features of Complex Tests

✅ Multi-phase scenarios (typically 4-8 phases each)
✅ Dynamic data handling (not hardcoded product names)
✅ Advanced state verification
✅ Error handling and recovery validation
✅ Mathematical calculations verification
✅ Navigation and persistence testing
✅ Comprehensive logging with phase tracking
✅ SoftAssert for multiple validations per test

---

## Compilation & Build Status

✅ All 7 test classes compile successfully
✅ Maven clean compile: SUCCESS
✅ No compilation errors or warnings
✅ Ready for execution with TestNG

---

Generated: May 6, 2026
Framework: SauceDemo UI Automation Framework 1.0.0

