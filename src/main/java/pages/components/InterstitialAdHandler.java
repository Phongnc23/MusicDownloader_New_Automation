package pages.components;

import base.BasePage;
import constants.TimeOutConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utils.GestureUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Xu ly quang cao Interstitial (AdMob) xuat hien khi mo app.
 *
 * LUU Y: File nay PHAI luu UTF-8 vi co chua chu "Đóng" can match chinh xac
 * voi text trong DOM. (Day la exception duy nhat cho rule "tieng Viet khong dau".)
 *
 * Hanh vi quang cao:
 *  - Loai 1 (video DAI) : hien Skip sau ~12s -> tap Skip -> hien Close (~8s) -> tap Close
 *  - Loai 2 (video NGAN): hien Close sau ~8s -> tap Close
 *  - Vi tri nut random top-left HOAC top-right
 *  - Nut Close/Skip thuong co clickable=false -> PHAI tap theo toa do (khong dung .click())
 */
public class InterstitialAdHandler extends BasePage {

    // Pattern bounds dang [x1,y1][x2,y2]
    private static final Pattern BOUNDS_PATTERN =
            Pattern.compile("\\[(\\d+),(\\d+)]\\[(\\d+),(\\d+)]");

    // Cac text co the la nut dong/skip quang cao
    private static final String[] DISMISS_TEXTS = {"Đóng", "Close", "Skip", "Bỏ qua", "X"};

    /**
     * Co gang dong quang cao interstitial neu co.
     * Neu khong tim thay quang cao trong AD_MAX_WAIT thi bo qua (coi nhu khong co ad).
     */
    public void dismissAd() {
        log.info("[Ad] Bat dau xu ly quang cao interstitial ===");

        // Buoc 1: tim & tap nut Skip (neu la quang cao video dai)
        boolean tappedSkip = waitAndTapDismiss("Skip", TimeOutConstants.AD_MAX_WAIT);
        if (tappedSkip) {
            log.info("[Ad] Da tap Skip, cho nut Close xuat hien...");
            sleep(1500);
        }

        // Buoc 2: tim & tap nut Close / Đóng
        boolean tappedClose = waitAndTapDismiss("Close", TimeOutConstants.AD_MAX_WAIT);
        if (tappedClose) {
            log.info("[Ad] Da dong quang cao thanh cong.");
        } else {
            log.info("[Ad] Khong tim thay quang cao (hoac da tu dong dong) - tiep tuc.");
        }

        sleep(1000);
        log.info("[Ad] === Ket thuc xu ly quang cao ===");
    }

    /**
     * Poll tim 1 element co text nam trong DISMISS_TEXTS, tap theo tam bounds.
     *
     * @param hint    goi y loai nut dang tim (chi de log)
     * @param maxWait thoi gian toi da (ms)
     * @return true neu tim thay va tap, false neu het gio
     */
    private boolean waitAndTapDismiss(String hint, long maxWait) {
        long deadline = System.currentTimeMillis() + maxWait;

        while (System.currentTimeMillis() < deadline) {
            for (String text : DISMISS_TEXTS) {
                WebElement el = findDismissElement(text);
                if (el != null) {
                    String bounds = el.getAttribute("bounds");
                    int[] center = parseCenter(bounds);
                    if (center != null) {
                        log.info("[Ad] Tim thay nut '{}' (hint={}), bounds={}, tap tai ({},{})",
                                text, hint, bounds, center[0], center[1]);
                        GestureUtils.tap(driver, center[0], center[1]);
                        return true;
                    }
                }
            }
            sleep(TimeOutConstants.AD_POLL_INTERVAL);
        }
        return false;
    }

    /** Tim element theo text (TextView/Button) - tra ve null neu khong co. */
    private WebElement findDismissElement(String text) {
        // Thu match text chinh xac
        By byText = By.xpath(
                "//*[@text='" + text + "' or @content-desc='" + text + "']");
        List<WebElement> els = driver.findElements(byText);
        if (!els.isEmpty()) {
            return els.get(0);
        }
        return null;
    }

    /** Tinh toa do tam tu chuoi bounds [x1,y1][x2,y2]. */
    private int[] parseCenter(String bounds) {
        if (bounds == null) {
            return null;
        }
        Matcher m = BOUNDS_PATTERN.matcher(bounds);
        if (m.find()) {
            int x1 = Integer.parseInt(m.group(1));
            int y1 = Integer.parseInt(m.group(2));
            int x2 = Integer.parseInt(m.group(3));
            int y2 = Integer.parseInt(m.group(4));
            return new int[]{(x1 + x2) / 2, (y1 + y2) / 2};
        }
        return null;
    }
}
