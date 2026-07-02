package driver;

import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Factory dieu phoi vong doi driver, ket hop voi DriverManager (ThreadLocal).
 */
public class DriverFactory {

    private static final Logger log = LogManager.getLogger(DriverFactory.class);

    private DriverFactory() {
    }

    /** Khoi tao driver moi va luu vao ThreadLocal. */
    public static AndroidDriver initDriver() {
        if (DriverManager.getDriver() == null) {
            AndroidDriver driver = AndroidDriverManager.createDriver();
            DriverManager.setDriver(driver);
        }
        return DriverManager.getDriver();
    }

    /** Quit driver va xoa khoi ThreadLocal. */
    public static void quitDriver() {
        AndroidDriver driver = DriverManager.getDriver();
        if (driver != null) {
            try {
                driver.quit();
                log.info("Driver da quit thanh cong.");
            } catch (Exception e) {
                log.warn("Loi khi quit driver: {}", e.getMessage());
            } finally {
                DriverManager.removeDriver();
            }
        }
    }
}
