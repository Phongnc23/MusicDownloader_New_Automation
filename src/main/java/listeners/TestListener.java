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
        // Neu test da tu dinh anh minh chung dung trang thai ky vong (ExtentReportManager.attachProof)
        // -> KHONG chup lai (tranh anh Home vo nghia sau khi case da back ve Home).
        // Nguoc lai (test chua dinh proof) -> chup trang thai cuoi lam fallback. base64 da nen -> HTML tu chua.
        if (!ExtentReportManager.hasProof()) {
            String b64 = ScreenshotUtils.captureBase64Compressed();
            if (b64 != null) {
                try {
                    test.pass("Anh minh chung (PASS): " + result.getMethod().getMethodName(),
                            MediaEntityBuilder.createScreenCaptureFromBase64String(b64).build());
                } catch (Exception e) {
                    log.warn("Khong dinh kem duoc anh minh chung PASS: {}", e.getMessage());
                }
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

        // Dinh kem screenshot luc FAIL bang base64 (da nen) -> NHUNG thang vao HTML
        // giong anh PASS. Nho vay report la 1 file HTML TU CHUA hoan toan, gui may khac
        // khong can kem thu muc screenshots/.
        String b64 = ScreenshotUtils.captureBase64Compressed();
        if (b64 != null) {
            try {
                test.fail("Screenshot luc fail:",
                        MediaEntityBuilder.createScreenCaptureFromBase64String(b64).build());
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
