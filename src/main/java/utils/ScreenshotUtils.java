package utils;

import constants.AppConstants;
import driver.DriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Chup screenshot, luu vao thu muc /screenshots, tra ve duong dan file.
 */
public class ScreenshotUtils {

    private ScreenshotUtils() {
    }

    /**
     * Chup man hinh hien tai.
     *
     * @param testName ten test (dung dat ten file)
     * @return duong dan tuyet doi cua file anh, hoac null neu loi
     */
    public static String capture(String testName) {
        try {
            TakesScreenshot ts = DriverManager.getDriver();
            File src = ts.getScreenshotAs(OutputType.FILE);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName  = testName + "_" + timestamp + ".png";
            String destPath  = AppConstants.SCREENSHOT_DIR + fileName;

            File dest = new File(destPath);
            FileUtils.copyFile(src, dest);
            LogUtils.info("Da chup screenshot: {}", destPath);
            return destPath;
        } catch (Exception e) {
            LogUtils.error("Khong chup duoc screenshot: " + e.getMessage());
            return null;
        }
    }
}
