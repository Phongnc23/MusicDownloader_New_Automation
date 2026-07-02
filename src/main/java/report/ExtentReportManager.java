package report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import constants.AppConstants;

import java.io.File;

/**
 * Quan ly ExtentReports (singleton) + ExtentTest theo tung thread.
 */
public class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();

    private ExtentReportManager() {
    }

    /** Khoi tao ExtentReports 1 lan duy nhat. */
    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            File dir = new File(AppConstants.REPORT_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            ExtentSparkReporter spark =
                    new ExtentSparkReporter(AppConstants.REPORT_DIR + AppConstants.REPORT_NAME);
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle(AppConstants.REPORT_DOC_TITLE);
            spark.config().setReportName(AppConstants.REPORT_TITLE);

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("App", AppConstants.APP_PACKAGE);
            extent.setSystemInfo("Device", AppConstants.DEVICE_NAME);
            extent.setSystemInfo("Platform", AppConstants.PLATFORM_NAME + " " + AppConstants.PLATFORM_VERSION);
            extent.setSystemInfo("Tester", "BlueSoftware");
        }
        return extent;
    }

    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = getInstance().createTest(testName, description);
        TEST.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return TEST.get();
    }

    public static void removeTest() {
        TEST.remove();
    }

    /** Ghi report ra file (goi o @AfterSuite). */
    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
