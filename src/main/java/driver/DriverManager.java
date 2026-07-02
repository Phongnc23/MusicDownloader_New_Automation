package driver;

import io.appium.java_client.android.AndroidDriver;

/**
 * Giu AndroidDriver theo tung thread (ho tro chay song song an toan).
 */
public class DriverManager {

    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static AndroidDriver getDriver() {
        return DRIVER.get();
    }

    public static void setDriver(AndroidDriver driver) {
        DRIVER.set(driver);
    }

    public static void removeDriver() {
        if (DRIVER.get() != null) {
            DRIVER.remove();
        }
    }
}
