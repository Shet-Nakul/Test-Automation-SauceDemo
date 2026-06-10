package com.saucedemo.pages;

import com.saucedemo.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.WebElement;

/**
 * ProductDetailPage - Represents the individual product detail page.
 */
public class ProductDetailPage extends BasePage {

    // ──────────────────── Locators ────────────────────
    private static final By PRODUCT_NAME        = By.cssSelector(".inventory_details_name");
    private static final By PRODUCT_DESCRIPTION = By.cssSelector(".inventory_details_desc");
    private static final By PRODUCT_PRICE       = By.cssSelector(".inventory_details_price");
    private static final By ADD_TO_CART_BTN     = By.cssSelector("[data-test^='add-to-cart']");
    private static final By REMOVE_BTN          = By.cssSelector("[data-test^='remove']");
    private static final By BACK_BUTTON         = By.id("back-to-products");
    private static final By PRODUCT_IMAGE       = By.cssSelector(".inventory_details_img");
    private static final By CART_BADGE          = By.cssSelector(".shopping_cart_badge");
    private static final By CART_LINK           = By.cssSelector(".shopping_cart_link");

    @FindBy(css = ".inventory_details_name")
    private WebElement productName;

    @FindBy(css = ".inventory_details_price")
    private WebElement productPrice;

    // ──────────────────── Actions ────────────────────

    public ProductDetailPage addToCart() {
        log.info("Adding product to cart from detail page");
        click(ADD_TO_CART_BTN);
        return this;
    }

    public ProductDetailPage removeFromCart() {
        log.info("Removing product from cart on detail page");
        click(REMOVE_BTN);
        return this;
    }

    public InventoryPage goBackToProducts() {
        log.info("Navigating back to products");
        for(int i=0; i < 5; i++){
            System.out.println(i);
        }
        click(BACK_BUTTON);
        return new InventoryPage();
    }

    public CartPage goToCart() {
        click(CART_LINK);
        return new CartPage();
    }

    // ──────────────────── Assertions ────────────────────

    public String getProductName() {
        return getText(PRODUCT_NAME);
    }

    public String getProductDescription() {
        return getText(PRODUCT_DESCRIPTION);
    }

    public String getProductPrice() {
        return getText(PRODUCT_PRICE);
    }

    public boolean isAddToCartButtonDisplayed() {
        return isDisplayed(ADD_TO_CART_BTN);
    }

    public boolean isRemoveButtonDisplayed() {
        return isDisplayed(REMOVE_BTN);
    }

    public boolean isProductImageDisplayed() {
        return isDisplayed(PRODUCT_IMAGE);
    }

    public int getCartBadgeCount() {
        if (!isDisplayed(CART_BADGE)) return 0;
        return Integer.parseInt(getText(CART_BADGE));
    }

    public boolean isOnDetailPage() {
        return getCurrentUrl().contains("inventory-item");
    }
}
