package com.saucedemo.pages;

import com.saucedemo.base.BasePage;
import com.saucedemo.utils.ElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * CartPage - Represents the shopping cart page (/cart.html).
 */
public class CartPage extends BasePage {

    // ──────────────────── Locators ────────────────────
    private static final By PAGE_TITLE         = By.cssSelector(".title");
    private static final By CART_ITEMS         = By.cssSelector(".cart_item");
    private static final By CART_ITEM_NAMES    = By.cssSelector(".inventory_item_name");
    private static final By CART_ITEM_PRICES   = By.cssSelector(".inventory");
    private static final By CART_ITEM_QTY      = By.cssSelector(".cart_quantity");
    private static final By REMOVE_BUTTONS     = By.cssSelector("[data-test^='remove']");
    private static final By CONTINUE_SHOPPING  = By.id("continue-shopping");
    private static final By CHECKOUT_BTN       = By.id("checkout");
    private static final By CART_BADGE         = By.cssSelector(".shopping_cart_badge");

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    @FindBy(id = "continue-")
    private WebElement continueShoppingButton;

    // ──────────────────── Actions ────────────────────

    public CheckoutStepOnePage proceedToCheckout() {
        log.info("Clicking Checkout button");
        click(checkoutButton);
        waitForUrl("checkout-step-one");
        return new CheckoutStepOnePage();
    }

    public InventoryPage continueShopping() {
        log.info("Clicking Continue Shopping");
        click(continueShoppingButton);
        waitForUrl("inventory");
        return new InventoryPage();
    }

    public CartPage removeItemByName(String itemName) {
        log.info("Removing item from cart: {}", itemName);
        By removeBtn = By.xpath(
                "//div[text()='" + itemName + "']/ancestor::div[@class='cart_item']" +
                "//button[contains(@data-test,'remove')]");
        click(removeBtn);
        return this;
    }

    public CartPage removeFirstItem() {
        List<WebElement> buttons = ElementUtils.findElements(REMOVE_BUTTONS);
        ElementUtils.click(buttons.get(0));
        return this;
    }

    // ──────────────────── Assertions ────────────────────

    public String getPageTitle() {
        return getText(PAGE_TITLE);
    }

    public int getCartItemCount() {
        return ElementUtils.getElementCount(CART_ITEMS);
    }

    public List<String> getCartItemNames() {
        return ElementUtils.getAllTexts(CART_ITEM_NAMES);
    }

    public List<String> getCartItemPrices() {
        return ElementUtils.getAllTexts(CART_ITEM_PRICES);
    }

    public boolean isCartEmpty() {
        return getCartItemCount() == 0;
    }

    public boolean isItemInCart(String itemName) {
        return getCartItemNames().contains(itemName);
    }

    public boolean isOnCartPage() {
        return getCurrentUrl().contains("cart");
    }

    public int getCartBadgeCount() {
        if (!isDisplayed(CART_BADGE)) return 0;
        return Integer.parseInt(getText(CART_BADGE));
    }
}
