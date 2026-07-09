package report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import constants.AppConstants;
import utils.ScreenshotUtils;

import java.io.File;

/**
 * Quan ly ExtentReports (singleton) + ExtentTest theo tung thread.
 */
public class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();
    // Danh dau test HIEN TAI da tu dinh anh minh chung (attachProof) chua -> TestListener
    // dua vao day de KHONG chup lai anh trang thai cuoi (thuong da ve Home -> vo nghia).
    private static final ThreadLocal<Boolean> PROOF = ThreadLocal.withInitial(() -> Boolean.FALSE);

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
        PROOF.set(Boolean.FALSE); // moi test bat dau: chua co proof
        return test;
    }

    public static ExtentTest getTest() {
        return TEST.get();
    }

    public static void removeTest() {
        TEST.remove();
        PROOF.remove();
    }

    /**
     * Chup NGAY trang thai hien tai (ket qua ky vong cua case) -> nhung base64 vao report lam
     * anh minh chung. GOI TRONG test, tai dung thoi diem man hinh dang o trang thai mong muon
     * (vd: da mo Search Online + ban phim hien) TRUOC khi dieu huong ve Home. Sau khi goi, TestListener
     * se KHONG chup lai anh trang thai cuoi (tranh anh Home vo nghia).
     *
     * @param desc mo ta trang thai minh chung (vd "Da mo man Search Online, ban phim hien")
     */
    public static void attachProof(String desc) {
        ExtentTest t = getTest();
        if (t == null) return;
        String b64 = ScreenshotUtils.captureBase64Compressed();
        if (b64 != null) {
            try {
                t.log(Status.PASS, desc,
                        MediaEntityBuilder.createScreenCaptureFromBase64String(b64).build());
                PROOF.set(Boolean.TRUE);
                return;
            } catch (Exception ignore) {
                // fallback: chi ghi text neu nhung anh loi
            }
        }
        t.log(Status.PASS, desc);
    }

    /** TestListener dung: test hien tai da co anh minh chung tu attachProof chua? */
    public static boolean hasProof() {
        return Boolean.TRUE.equals(PROOF.get());
    }

    /** Ghi report ra file (goi o @AfterSuite). */
    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
