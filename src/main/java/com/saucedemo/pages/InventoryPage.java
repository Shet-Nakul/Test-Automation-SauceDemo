package com.saucedemo.pages;

import com.saucedemo.base.BasePage;
import com.saucedemo.utils.ElementUtils;
import com.saucedemo.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * InventoryPage - Represents the products listing page (/inventory.html).
 */
public class InventoryPage extends BasePage {

    // ──────────────────── Locators ────────────────────
    private static final By PAGE_TITLE           = By.cssSelector(".title");
    private static final By INVENTORY_ITEMS      = By.cssSelector(".inventory_item");
    private static final By ITEM_NAMES           = By.cssSelector(".inventory_item_name");
    private static final By ITEM_PRICES          = By.cssSelector(".inventory_item_price");
    private static final By ADD_TO_CART_BUTTONS  = By.cssSelector("[data-test^='add-to-cart']");
    private static final By REMOVE_BUTTONS       = By.cssSelector("[data-test^='remove']");
    private static final By SORT_DROPDOWN        = By.cssSelector("[data-test='product-sort-container']");
    private static final By CART_BADGE           = By.cssSelector(".shopping_cart_badge");
    private static final By CART_LINK            = By.cssSelector(".shopping_cart_link");
    private static final By BURGER_MENU          = By.id("react-burger-menu-btn");
    private static final By BURGER_MENU_CLOSE    = By.id("react-burger-cross-btn");
    private static final By LOGOUT_LINK          = By.id("logout_sidebar_link");
    private static final By ABOUT_LINK           = By.id("about_sidebar_link");
    private static final By RESET_LINK           = By.id("reset_sidebar_link");

    @FindBy(css = ".title")
    private WebElement pageTitle;

    @FindBy(css = ".shopping_cart_link")
    private WebElement cartLink;

    // ──────────────────── Actions ────────────────────

    public String getPageTitle() {
        return getText(PAGE_TITLE);
    }

    public List<String> getAllProductNames() {
        return ElementUtils.getAllTexts(ITEM_NAMES);
    }

    public List<String> getAllProductPrices() {
        return ElementUtils.getAllTexts(ITEM_PRICES);
    }

    public int getProductCount() {
        return ElementUtils.getElementCount(INVENTORY_ITEMS);
    }

    public InventoryPage addProductToCartByName(String productName) {
        log.info("Adding product to cart: {}", productName);
        By addBtn = By.xpath(
                "//div[text()='" + productName + "']/ancestor::div[@class='inventory_item']" +
                "//button[contains(@data-test,'add-to-cart')]");
        click(addBtn);
        return this;
    }

    public InventoryPage addFirstProductToCart() {
        List<WebElement> buttons = ElementUtils.findElements(ADD_TO_CART_BUTTONS);
        if (!buttons.isEmpty()) {
            ElementUtils.click(buttons.get(0));
        }
        return this;
    }

    public InventoryPage addAllProductsToCart() {
        log.info("Adding all products to cart");
        List<WebElement> buttons = ElementUtils.findElements(ADD_TO_CART_BUTTONS);
        buttons.forEach(ElementUtils::click);
        return this;
    }

    public InventoryPage removeProductByName(String productName) {
        By removeBtn = By.xpath(
                "//div[text()='" + productName + "']/ancestor::div[@class='inventory_item']" +
                "//button[contains(@data-test,'remove')]");
        click(removeBtn);
        return this;
    }

    public InventoryPage sortBy(String sortOption) {
        log.info("Sorting by: {}", sortOption);
        ElementUtils.selectByVisibleText(SORT_DROPDOWN, sortOption);
        return this;
    }

    public CartPage goToCart() {
        log.info("Navigating to cart");
        click(CART_LINK);
        return new CartPage();
    }

    public ProductDetailPage clickOnProduct(String productName) {
        By productLink = By.xpath("//div[text()='" + productName + "']");
        click(productLink);
        return new ProductDetailPage();
    }

    public InventoryPage openBurgerMenu() {
        click(BURGER_MENU);
        WaitUtils.waitForVisibility(LOGOUT_LINK);
        return this;
    }

    public LoginPage logout() {
        openBurgerMenu();
        click(LOGOUT_LINK);
        return new LoginPage();
    }

    public InventoryPage resetAppState() {
        openBurgerMenu();
        click(RESET_LINK);
        click(BURGER_MENU_CLOSE);
        return this;
    }

    // ──────────────────── Assertions ────────────────────

    public int getCartBadgeCount() {
        if (!isDisplayed(CART_BADGE)) return 0;
        return Integer.parseInt(getText(CART_BADGE));
    }

    public boolean isCartBadgeDisplayed() {
        return isDisplayed(CART_BADGE);
    }

    public boolean isOnInventoryPage() {
        return getCurrentUrl().contains("inventory");
    }
}
