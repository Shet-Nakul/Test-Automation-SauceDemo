package com.saucedemo.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.saucedemo.utils.ExtentReportManager;
import com.saucedemo.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

/**
 * ExtentReportListener - Hooks into TestNG lifecycle to build rich HTML reports.
 */
public class ExtentReportListener implements ITestListener, ISuiteListener {

    private static final Logger log = LogManager.getLogger(ExtentReportListener.class);

    @Override
    public void onStart(ISuite suite) {
        log.info("========== Suite Started: {} ==========", suite.getName());
        ExtentReportManager.getExtentReports();
    }

    @Override
    public void onFinish(ISuite suite) {
        log.info("========== Suite Finished: {} ==========", suite.getName());
        ExtentReportManager.flushReports();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        log.info(">>> Test Started: {}", testName);

        ExtentTest test = ExtentReportManager.getExtentReports()
                .createTest(testName, description);
        ExtentReportManager.setTest(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("✔ Test PASSED: {}", result.getName());
        ExtentReportManager.getTest().log(Status.PASS, "Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("✘ Test FAILED: {} | Reason: {}", result.getName(), result.getThrowable());

        ExtentTest test = ExtentReportManager.getTest();
        test.log(Status.FAIL, "Test Failed: " + result.getThrowable().getMessage());

        // Attach screenshot
        String base64 = ScreenshotUtils.captureBase64Screenshot();
        if (base64 != null) {
            test.addScreenCaptureFromBase64String(base64, "Failure Screenshot");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("⚠ Test SKIPPED: {}", result.getName());
        ExtentReportManager.getTest().log(Status.SKIP,
                "Test Skipped: " + result.getThrowable());
    }
}
