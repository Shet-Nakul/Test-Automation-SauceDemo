package com.saucedemo.utils;

import com.saucedemo.config.ConfigReader;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ScreenshotUtils - Captures screenshots on test failure or on-demand.
 * Returns the file path so it can be embedded in ExtentReports.
 */
public class ScreenshotUtils {

    private static final Logger log = LogManager.getLogger(ScreenshotUtils.class);
    private static final ConfigReader config = ConfigReader.getInstance();

    private ScreenshotUtils() {}

    /**
     * Captures a screenshot and saves it with a timestamp.
     *
     * @param testName name of the test (used in filename)
     * @return absolute path of the saved screenshot, or null on failure
     */
    public static String captureScreenshot(String testName) {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) {
            log.warn("Driver is null, cannot capture screenshot.");
            return null;
        }
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String screenshotDir = config.getScreenshotPath();
            File dir = new File(screenshotDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = screenshotDir + testName + "_" + timestamp + ".png";
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File dest = new File(fileName);
            FileUtils.copyFile(src, dest);
            log.info("Screenshot saved: {}", dest.getAbsolutePath());
            return dest.getAbsolutePath();
        } catch (IOException e) {
            log.error("Failed to save screenshot: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Returns screenshot as Base64 string for embedding in reports.
     */
    public static String captureBase64Screenshot() {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) return null;
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }
}
