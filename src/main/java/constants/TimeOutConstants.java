package constants;

import java.time.Duration;

/**
 * Cac muc timeout dung trong toan project.
 */
public class TimeOutConstants {

    private TimeOutConstants() {
    }

    // ---- Wait chung (giay) ----
    public static final int SHORT_WAIT  = 5;
    public static final int MEDIUM_WAIT = 10;
    public static final int LONG_WAIT   = 20;

    // ---- Duration tien dung cho WebDriverWait ----
    public static final Duration SHORT_DURATION  = Duration.ofSeconds(SHORT_WAIT);
    public static final Duration MEDIUM_DURATION = Duration.ofSeconds(MEDIUM_WAIT);
    public static final Duration LONG_DURATION   = Duration.ofSeconds(LONG_WAIT);

    // ---- Implicit wait ----
    // PHAI = 0. existsImmediately()/isPresentQuick() = driver.findElements(); voi implicit wait > 0,
    // moi lan check element KHONG ton tai se DUNG CHO DU implicit wait (vd assertFalse(isHomeDisplayed())
    // khi da roi Home) -> chay rat cham. Cac cho can cho element XUAT HIEN da dung WebDriverWait
    // (explicit) trong BasePage, hoac sleep() trong test -> khong phu thuoc implicit wait.
    public static final Duration IMPLICIT_WAIT = Duration.ZERO;

    // ---- Xu ly quang cao Interstitial (millisecond) ----
    // Cho toi da bao lau de quang cao xuat hien nut Close/Skip
    public static final long AD_MAX_WAIT      = 25000;  // 25s
    public static final long AD_POLL_INTERVAL = 500;    // poll moi 0.5s
}
