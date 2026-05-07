package com.saucedemo.utils;

import com.saucedemo.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * DriverManager - Thread-safe WebDriver factory using ThreadLocal.
 * Supports Chrome, Firefox, and Edge browsers.
 */
public class DriverManager {

    private static final Logger log = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ConfigReader config = ConfigReader.getInstance();

    private DriverManager() {}

    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            initDriver(config.getBrowser());
        }
        return driverThreadLocal.get();
    }

    public static void initDriver(String browser) {
        WebDriver driver;
        boolean headless = config.isHeadless();

        log.info("Initializing '{}' browser | headless={}", browser, headless);

        switch (browser.toLowerCase().trim()) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions ffOpts = new FirefoxOptions();
                if (headless) ffOpts.addArguments("--headless");
                driver = new FirefoxDriver(ffOpts);
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOpts = new EdgeOptions();
                if (headless) edgeOpts.addArguments("--headless");
                driver = new EdgeDriver(edgeOpts);
            }
            default -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOpts = new ChromeOptions();
                chromeOpts.addArguments("--start-maximized");
                chromeOpts.addArguments("--disable-notifications");
                chromeOpts.addArguments("--disable-popup-blocking");
                chromeOpts.addArguments("--no-sandbox");
                chromeOpts.addArguments("--disable-dev-shm-usage");
                if (headless) {
                    chromeOpts.addArguments("--headless=new");
                    chromeOpts.addArguments("--window-size=1920,1080");
                }
                driver = new ChromeDriver(chromeOpts);
            }
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
        driver.manage().window().setSize(new Dimension(1920, 1080));

        driverThreadLocal.set(driver);
        log.info("WebDriver initialized: {}", driver.getClass().getSimpleName());
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
            log.info("WebDriver quit and removed from ThreadLocal.");
        }
    }
}
