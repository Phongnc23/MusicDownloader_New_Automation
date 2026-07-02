package base;

import constants.TimeOutConstants;
import driver.DriverManager;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Lop cha cho tat ca Page Object.
 * Cung cap cac action chung: click, sendKeys, findElement, isDisplayed, getText...
 * Tat ca deu co WebDriverWait san de tranh flaky.
 */
public abstract class BasePage {

    protected final AndroidDriver driver;
    protected final WebDriverWait wait;
    protected final Logger log = LogManager.getLogger(this.getClass());

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait   = new WebDriverWait(driver, TimeOutConstants.MEDIUM_DURATION);
    }

    // ---- Tim element ----
    protected WebElement findElement(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    protected WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // ---- Action ----
    protected void click(By locator) {
        // App Flutter recreate node lien tuc (vd mini player doi % moi giay) -> element hay bi
        // StaleElementReference giua luc tim va click. Retry tim+click vai lan cho on dinh.
        org.openqa.selenium.WebDriverException last = null;
        for (int i = 0; i < 3; i++) {
            try {
                waitClickable(locator).click();
                log.info("Click element: {}", locator);
                return;
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                last = e;
                log.warn("Stale khi click {} (lan {}), retry", locator, i + 1);
            }
        }
        if (last != null) throw last;
    }

    protected void sendKeys(By locator, String text) {
        WebElement el = waitVisible(locator);
        el.clear();
        el.sendKeys(text);
        log.info("Nhap text '{}' vao {}", text, locator);
    }

    protected String getText(By locator) {
        return waitVisible(locator).getText();
    }

    // ---- Kiem tra trang thai ----
    protected boolean isDisplayed(By locator) {
        try {
            return waitVisible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Kiem tra nhanh khong wait lau - dung cho element co the khong ton tai. */
    protected boolean isPresentQuick(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    /** Kiem tra ton tai ngay lap tuc (khong wait) - alias dung trong Page Object. */
    protected boolean existsImmediately(By locator) {
        return isPresentQuick(locator);
    }

    /**
     * Lay content-desc cua element. Tra ve chuoi rong neu khong co attribute
     * hoac element khong ton tai (tranh NPE trong Page Object).
     */
    protected String getContentDesc(By locator) {
        try {
            WebElement el = findElement(locator);
            // getDomAttribute("content-desc") doi luc tra null tren Appium UiAutomator2
            // (tuy element/thoi diem) -> fallback getAttribute("content-desc")/("name").
            String desc = el.getDomAttribute("content-desc");
            if (desc == null || desc.isEmpty()) desc = el.getAttribute("content-desc");
            if (desc == null || desc.isEmpty()) desc = el.getAttribute("name");
            return desc != null ? desc : "";
        } catch (Exception e) {
            return "";
        }
    }

    // ---- Tien ich ----
    /**
     * Poll dieu kien toi da timeoutMs (200ms/lan), tra ve true NGAY khi dieu kien dung.
     * Dung thay sleep co dinh: nhanh khi san sang som, on dinh khi cham (cold start).
     * (implicit wait = 0 nen moi lan check gan nhu tuc thi.)
     */
    public boolean waitUntil(java.util.function.BooleanSupplier condition, long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            try {
                if (condition.getAsBoolean()) return true;
            } catch (Exception ignored) {
            }
            sleep(200);
        }
        try {
            return condition.getAsBoolean();
        } catch (Exception e) {
            return false;
        }
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
