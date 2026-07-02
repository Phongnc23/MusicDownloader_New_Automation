package driver;

import constants.AppConstants;
import constants.TimeOutConstants;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Khoi tao AndroidDriver voi cac capability chuan.
 *
 * Capabilities quan trong:
 *  - noReset = true               -> giu nguyen data app, khong cai lai
 *  - autoGrantPermissions = true  -> tu cap quyen khi cai (neu fresh install)
 *  - ignoreHiddenApiPolicyError   -> can thiet cho Oppo/ColorOS
 */
public class AndroidDriverManager {

    private static final Logger log = LogManager.getLogger(AndroidDriverManager.class);

    private AndroidDriverManager() {
    }

    public static AndroidDriver createDriver() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(AppConstants.PLATFORM_NAME)
                .setPlatformVersion(AppConstants.PLATFORM_VERSION)
                .setDeviceName(AppConstants.DEVICE_NAME)
                .setAutomationName(AppConstants.AUTOMATION_NAME)
                .setAppPackage(AppConstants.APP_PACKAGE)
                .setAppActivity(AppConstants.APP_ACTIVITY)
                .setNoReset(true)
                .setAutoGrantPermissions(true)
                .setNewCommandTimeout(java.time.Duration.ofSeconds(300));

        // Capabilities dac thu cho Oppo / ColorOS
        options.setCapability("appium:ignoreHiddenApiPolicyError", true);
        options.setCapability("appium:disableWindowAnimation", true);
        options.setCapability("appium:autoGrantPermissions", true);
        // Cho phep activity khong duoc export van launch duoc
        options.setCapability("appium:appWaitActivity", "*");

        // TOI UU TOC DO tao session (moi test 1 driver -> tiet kiem ~5s/test):
        //  - skipServerInstallation: bo cai lai uiautomator2 server (da cai san tren may test)
        //  - skipDeviceInitialization: bo buoc device-init lap lai
        //  - skipLogcatCapture: khong bat logcat (khong dung trong test)
        // Cai fresh chua co server -> dat SKIP_SERVER_INSTALL=false trong .env cho lan chay dau.
        options.setCapability("appium:skipServerInstallation", AppConstants.SKIP_SERVER_INSTALL);
        options.setCapability("appium:skipDeviceInitialization", AppConstants.SKIP_DEVICE_INIT);
        options.setCapability("appium:skipLogcatCapture", true);

        try {
            log.info("Khoi tao AndroidDriver -> {}", AppConstants.APPIUM_SERVER_URL);
            AndroidDriver driver = new AndroidDriver(
                    new URL(AppConstants.APPIUM_SERVER_URL), options);
            driver.manage().timeouts().implicitlyWait(TimeOutConstants.IMPLICIT_WAIT);

            // QUAN TRONG (toi uu toc do): app Flutter co mini player phat nhac -> animation
            // LIEN TUC -> KHONG BAO GIO idle. Mac dinh UiAutomator2 cho app idle truoc moi
            // findElements (waitForIdleTimeout ~10s) -> moi lan check element treo den timeout,
            // cong don thanh PHUT (vd Menu02: 20 phut/7 test). Dat 100ms -> tra ve gan nhu tuc thi.
            try {
                driver.setSetting("waitForIdleTimeout", 100);
            } catch (Exception e) {
                log.warn("Khong set duoc waitForIdleTimeout: {}", e.getMessage());
            }

            log.info("AndroidDriver khoi tao thanh cong. Session: {}", driver.getSessionId());
            return driver;
        } catch (MalformedURLException e) {
            log.error("URL Appium server khong hop le: {}", AppConstants.APPIUM_SERVER_URL, e);
            throw new RuntimeException("Khong the khoi tao driver - URL sai", e);
        }
    }
}
