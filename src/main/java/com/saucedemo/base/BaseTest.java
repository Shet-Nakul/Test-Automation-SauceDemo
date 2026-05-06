package com.saucedemo.base;

import com.saucedemo.config.ConfigReader;
import com.saucedemo.utils.DriverManager;
import com.saucedemo.utils.ExtentReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * BaseTest - Parent class for all TestNG test classes.
 * Manages driver lifecycle, navigation to base URL, and logging.
 */
public abstract class BaseTest {

    protected static final Logger log = LogManager.getLogger(BaseTest.class);
    protected static final ConfigReader config = ConfigReader.getInstance();

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        log.info("---------- Setting up test: {} ----------", method.getName());
        DriverManager.initDriver(config.getBrowser());
        DriverManager.getDriver().get(config.getAppUrl());
        log.info("Navigated to: {}", config.getAppUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(Method method) {
        log.info("---------- Tearing down test: {} ----------", method.getName());
        DriverManager.quitDriver();
        ExtentReportManager.removeTest();
    }
}
