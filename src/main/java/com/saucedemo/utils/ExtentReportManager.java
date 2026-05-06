package com.saucedemo.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.saucedemo.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ExtentReportManager - Manages ExtentReports lifecycle.
 * Thread-safe using ThreadLocal for ExtentTest.
 */
public class ExtentReportManager {

    private static final Logger log = LogManager.getLogger(ExtentReportManager.class);
    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();
    private static final ConfigReader config = ConfigReader.getInstance();

    private ExtentReportManager() {}

    public static ExtentReports getExtentReports() {
        if (extentReports == null) {
            String reportPath = config.getReportPath();
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setDocumentTitle(config.getReportName());
            sparkReporter.config().setReportName(config.getReportName());
            sparkReporter.config().setEncoding("UTF-8");

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("Application", "SauceDemo");
            extentReports.setSystemInfo("Environment", "QA");
            extentReports.setSystemInfo("Browser", config.getBrowser());
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));

            log.info("ExtentReports initialized at: {}", reportPath);
        }
        return extentReports;
    }

    public static ExtentTest getTest() {
        return testThreadLocal.get();
    }

    public static void setTest(ExtentTest test) {
        testThreadLocal.set(test);
    }

    public static void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
            log.info("ExtentReports flushed.");
        }
    }

    public static void removeTest() {
        testThreadLocal.remove();
    }
}
