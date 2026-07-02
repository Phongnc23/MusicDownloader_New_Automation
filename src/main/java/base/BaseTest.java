package base;

import constants.AppConstants;
import driver.DriverFactory;
import driver.DriverManager;
import helpers.DialogHelper;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import report.ExtentReportManager;

import java.lang.reflect.Method;

/**
 * Lop cha cho tat ca test class.
 *
 * Vong doi:
 *  - @BeforeMethod: khoi tao driver moi (app tu launch) -> san sang
 *  - @AfterMethod : quit driver (moi test isolate hoan toan)
 *  - @AfterSuite  : flush ExtentReports
 *
 * Dung @BeforeMethod (KHONG phai @BeforeClass) de moi test case chay tren
 * 1 session sach, tranh state leak giua cac test.
 *
 * Ghi chu: ban build nay KHONG co quang cao -> KHONG goi ad handler o setup.
 */
public abstract class BaseTest {

    protected final Logger log = LogManager.getLogger(this.getClass());
    protected AndroidDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        log.info("==================================================");
        log.info(">>> BAT DAU TEST: {}", method.getName());
        log.info("==================================================");

        // Khoi tao driver (app tu launch vao Home)
        driver = DriverFactory.initDriver();

        // Cold start qua capability doi luc DUNG O LAUNCHER (khi app bi thoat o test truoc,
        // vd TC_030). activateApp dam bao app len foreground (tuong duong intent LAUNCHER -
        // da kiem chung tin cay) -> tranh test fail vi app khong o Home.
        driver.activateApp(AppConstants.APP_PACKAGE);

        // Cho app SAN SANG DIEU HUONG truoc khi test chay. KHONG ep app ve Home nua (theo yeu cau):
        // khong tap tab Home, khong nham marker Home. Chi dong dialog Update + neu app dang ket o
        // overlay KHONG co bottom nav (Select mode/Playing Queue/Play Now/dialog) thi BACK de THOAT
        // OVERLAY cho lo bottom nav -> test tu chuyen tab. Day la "back thoat overlay", KHONG phai
        // back ve home (khi da co bottom nav thi dung ngay, khong BACK -> khong cham exit dialog).
        waitAppReady();

        // Pause playback neu dang phat (churn tu test/lan chay truoc) -> moi test bat dau voi UI
        // TINH (khong churn) -> doc element on dinh. Test playback se tu tapPlayAll lai.
        try {
            new pages.HomePage().pausePlaybackIfPlaying();
        } catch (Exception ignored) {
        }

        log.info("Setup hoan tat - san sang chay test.");
    }

    // Bottom nav (tab Tracks clickable) co tren MOI man tabbed (Home/Tracks/Artists/Albums/Playlists)
    // -> dau hieu app dang o man dieu huong duoc. Khong co = dang o overlay/sub-screen.
    private static final By NAV_BAR = AppiumBy.androidUIAutomator(
            "new UiSelector().description(\"Tracks\").clickable(true)");

    private void waitAppReady() {
        DialogHelper dialogHelper = new DialogHelper(driver);
        long deadline = System.currentTimeMillis()
                + constants.TimeOutConstants.MEDIUM_WAIT * 1000L;
        int backTries = 0;
        while (System.currentTimeMillis() < deadline) {
            try {
                // Dialog Update co the hien muon (cold start) -> dong ngay neu co.
                dialogHelper.dismissUpdateDialogIfPresent();
                // Exit dialog ("Are you sure you want to exit?") co the bung do BACK qua da khi dang o
                // man list root (vd Playlists). KHONG BACK tiep (se thoat app) -> bam Cancel de o lai.
                try {
                    pages.HomePage h = new pages.HomePage();
                    if (h.isExitDialogDisplayed()) { h.tapCancelOnDialog(); Thread.sleep(300); continue; }
                } catch (Exception ignored) {}
                // Co bottom nav -> man tabbed, test tu dieu huong duoc. SAN SANG (khong ep ve Home).
                if (!driver.findElements(NAV_BAR).isEmpty()) return;
                // Khong co bottom nav -> dang ket o overlay (Select mode/Queue/Play Now/dialog).
                // BACK de thoat overlay cho lo bottom nav (gioi han 5 lan tranh loop vo han).
                if (backTries < 5) {
                    try { driver.navigate().back(); } catch (Exception ignored) {}
                    backTries++;
                }
            } catch (Exception ignored) {
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        log.warn("waitAppReady: het {}s van chua thay bottom nav", constants.TimeOutConstants.MEDIUM_WAIT);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(Method method) {
        log.info("<<< KET THUC TEST: {}", method.getName());
        // Pause playback neu dang phat: app khong tat khi quit driver -> nhac (bai 0:03) phat nen
        // churn lam test SAU flaky. Pause de man hinh dung lai truoc khi sang test ke tiep.
        try {
            new pages.HomePage().pausePlaybackIfPlaying();
        } catch (Exception ignored) {
        }
        DriverFactory.quitDriver();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        ExtentReportManager.flush();
        log.info("Da flush ExtentReports.");
    }

    /** Tien ich lay driver hien tai cho test class con. */
    protected AndroidDriver getDriver() {
        return DriverManager.getDriver();
    }
}