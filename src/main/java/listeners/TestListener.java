package listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import report.ExtentReportManager;
import utils.ScreenshotUtils;

/**
 * Listener noi TestNG voi ExtentReports.
 * Tu tao ExtentTest moi moi test, log PASS/FAIL/SKIP, dinh kem screenshot khi fail.
 */
public class TestListener implements ITestListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        log.info("==== BAT DAU SUITE: {} ====", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String desc = result.getMethod().getDescription();
        ExtentReportManager.createTest(testName, desc != null ? desc : testName);
        ExtentReportManager.getTest().log(Status.INFO, "Bat dau: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = ExtentReportManager.getTest();
        // Anh MINH CHUNG PASS: chup trang thai cuoi cua test (driver con song - listener chay TRUOC
        // @AfterMethod quit driver). Nhung base64 da nen -> HTML tu chua.
        String b64 = ScreenshotUtils.captureBase64Compressed();
        if (b64 != null) {
            try {
                test.pass("Anh minh chung (PASS): " + result.getMethod().getMethodName(),
                        MediaEntityBuilder.createScreenCaptureFromBase64String(b64).build());
            } catch (Exception e) {
                log.warn("Khong dinh kem duoc anh minh chung PASS: {}", e.getMessage());
            }
        }
        test.log(Status.PASS, "PASS: " + result.getMethod().getMethodName());
        ExtentReportManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = ExtentReportManager.getTest();
        test.log(Status.FAIL, "FAIL: " + result.getMethod().getMethodName());
        test.log(Status.FAIL, result.getThrowable());

        // Dinh kem screenshot
        String path = ScreenshotUtils.capture(result.getMethod().getMethodName());
        if (path != null) {
            try {
                test.fail("Screenshot luc fail:",
                        MediaEntityBuilder.createScreenCaptureFromPath(path).build());
            } catch (Exception e) {
                log.warn("Khong dinh kem duoc screenshot vao report: {}", e.getMessage());
            }
        }
        ExtentReportManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.SKIP, "SKIP: " + result.getMethod().getMethodName());
        }
        ExtentReportManager.removeTest();
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("==== KET THUC SUITE: {} ====", context.getName());
        ExtentReportManager.flush();
    }
}
