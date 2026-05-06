package com.saucedemo.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigReader - Singleton class to read configuration properties.
 * Loads config.properties once and provides typed getters.
 */
public class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static ConfigReader instance;
    private final Properties properties;
    private static final String CONFIG_PATH = "src/test/resources/config.properties";

    private ConfigReader() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            properties.load(fis);
            log.info("Configuration loaded from: {}", CONFIG_PATH);
        } catch (IOException e) {
            log.error("Failed to load config.properties: {}", e.getMessage());
            throw new RuntimeException("Cannot load configuration file: " + CONFIG_PATH, e);
        }
    }

    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    public String getProperty(String key) {
        String value = System.getProperty(key, properties.getProperty(key));
        if (value == null) {
            log.warn("Property '{}' not found in config.", key);
        }
        return value;
    }

    public String getAppUrl()               { return getProperty("app.url"); }
    public String getAppTitle()             { return getProperty("app.title"); }
    public String getBrowser()              { return getProperty("browser"); }
    public boolean isHeadless()             { return Boolean.parseBoolean(getProperty("headless")); }
    public int getImplicitWait()            { return Integer.parseInt(getProperty("implicit.wait")); }
    public int getExplicitWait()            { return Integer.parseInt(getProperty("explicit.wait")); }
    public int getPageLoadTimeout()         { return Integer.parseInt(getProperty("page.load.timeout")); }
    public String getValidUsername()        { return getProperty("valid.username"); }
    public String getValidPassword()        { return getProperty("valid.password"); }
    public String getLockedUsername()       { return getProperty("locked.username"); }
    public String getPerformanceUsername()  { return getProperty("performance.username"); }
    public String getProblemUsername()      { return getProperty("problem.username"); }
    public String getReportPath()           { return getProperty("report.path"); }
    public String getReportName()           { return getProperty("report.name"); }
    public String getScreenshotPath()       { return getProperty("screenshot.path"); }
    public boolean isScreenshotOnFailure()  { return Boolean.parseBoolean(getProperty("screenshot.on.failure")); }
}
