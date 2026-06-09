package com.saucedemo.pages;

import com.saucedemo.base.BasePage;
import com.saucedemo.utils.ElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * CheckoutStepTwoPage - Represents checkout step two: order summary/overview.
 */
public class CheckoutStepTwoPage extends BasePage {

    // ──────────────────── Locators ────────────────────
    private static final By PAGE_TITLE         = By.cssSelector(".title");
    private static final By CART_ITEM_NAMES    = By.cssSelector(".inventory_item_name");
    private static final By CART_ITEM_PRICES   = By.cssSelector(".inventory_item_price");
    private static final By ITEM_TOTAL_LABEL   = By.cssSelector(".summary_subtotal_label");
    private static final By TAX_LABEL          = By.cssSelector(".summary_tax_label");
    private static final By TOTAL_LABEL        = By.cssSelector(".summary_total_label");
    private static final By FINISH_BTN         = By.id("finish");
    private static final By CANCEL_BTN         = By.id("cancel");
    private static final By PAYMENT_INFO       = By.cssSelector(".summary_value_label");
    private static final By SHIPPING_INFO      = By.xpath("(//div[@class='summary_value_label'])[2]");

    @FindBy(id = "finish")
    private WebElement finishButton;

    @FindBy(id = "cancel")
    private WebElement cancelButton;

    // ──────────────────── Actions ────────────────────

    public CheckoutCompletePage clickFinish() {
        log.info("Clicking Finish on checkout overview");
        click(finishButton);
        waitForUrl("checkout-complete");
        return new CheckoutCompletePage();
    }

    public InventoryPage clickCancel() {
        log.info("Clicking Cancel on checkout overview");
        click(cancelButton);
        waitForUrl("inventory");
        return new InventoryPage();
    }

    // ──────────────────── Assertions ────────────────────

    public String getPageTitle() {
        return getText(PAGE_TITLE);
    }

    public List<String> getOrderItemNames() {
        return ElementUtils.getAllTexts(CART_ITEM_NAMES);
    }

    public List<String> getOrderItemPrices() {
        return ElementUtils.getAllTexts(CART_ITEM_PRICES);
    }

    public String getItemTotal() {
        return getText(ITEM_TOTAL_LABEL);
    }

    public String getTax() {
        return getText(TAX_LABEL);
    }

    public String getOrderTotal() {
        return getText(TOTAL_LABEL);
    }

    public double getOrderTotalValue() {
        String rawTotal = getOrderTotal().replace("Total: $", "").trim();
        System.out.println("getOrderTotalValue");
        return Double.parseDouble(rawTotal);
    }

    public double getItemTotalValue() {
        String raw = getItemTotal().replace("Item total: $", "").trim();
        System.out.println("getItemTotalValue");
        return Double.parseDouble(raw);
    }

    public double getTaxValue() {
        String raw = getTax().replace("Tax: $", "").trim();
        return Double.parseDouble(raw);
    }

    public boolean isOnOverviewPage() {
        return getCurrentUrl().contains("checkout-step-two");
    }
}
