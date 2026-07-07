package utils;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Collections;

/**
 * Cac thao tac cu chi (gesture) dung W3C Actions cho Appium 2.x.
 *
 * Dung khi:
 *  - Element co clickable=false (nut Close quang cao) -> phai tap theo toa do
 *  - Element khong co content-desc / resource-id
 *  - Can swipe / scroll / vuot canh man hinh
 */
public class GestureUtils {

    private GestureUtils() {
    }

    /** Tap mot diem theo toa do (x, y). */
    public static void tap(AndroidDriver driver, int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ZERO,
                PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(new org.openqa.selenium.interactions.Pause(finger, Duration.ofMillis(100)));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(tap));
        LogUtils.info("Tap tai toa do ({}, {})", x, y);
    }

    /**
     * Click tai (x, y) bang UiAutomator2 "mobile: clickGesture" (cap instrumentation, giong
     * `adb input tap`). Dung cho cac widget Flutter KHONG nhan W3C pointer tap (vd icon download
     * tren ket qua search / mini player) -> W3C tap "khong vao", clickGesture kich hoat duoc.
     */
    public static void clickGesture(AndroidDriver driver, int x, int y) {
        java.util.Map<String, Object> args = new java.util.HashMap<>();
        args.put("x", x);
        args.put("y", y);
        driver.executeScript("mobile: clickGesture", args);
        LogUtils.info("clickGesture tai ({}, {})", x, y);
    }

    /** Swipe tu (startX, startY) den (endX, endY) trong thoi gian durationMs. */
    public static void swipe(AndroidDriver driver, int startX, int startY,
                             int endX, int endY, int durationMs) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO,
                PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(durationMs),
                PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(swipe));
    }

    /**
     * Cu chi EDGE-BACK tu canh PHAI sang trai (Android gesture nav = Back).
     * Khac swipe phang: cham SAT mep phai, GIU NHE roi moi vuot vao trong -> he thong
     * nhan dien edge gesture on dinh hon (vuot phang nhanh doi luc khong duoc nhan).
     * Dung 1 LAN duy nhat (vd bung exit dialog o Home) - khong lap.
     */
    public static void edgeBackFromRight(AndroidDriver driver) {
        org.openqa.selenium.Dimension size = driver.manage().window().getSize();
        int w = size.width;
        int h = size.height;
        int startX = w - 1;             // sat mep phai (trong vung edge-gesture cua he thong)
        int endX   = (int) (w * 0.25);  // vuot du sau vao trong de vuot back ro rang
        int y      = h / 2;
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence seq = new Sequence(finger, 1);
        seq.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, y));
        seq.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        seq.addAction(new org.openqa.selenium.interactions.Pause(finger, Duration.ofMillis(120)));
        seq.addAction(finger.createPointerMove(Duration.ofMillis(350), PointerInput.Origin.viewport(), endX, y));
        seq.addAction(new org.openqa.selenium.interactions.Pause(finger, Duration.ofMillis(80)));
        seq.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(seq));
        LogUtils.info("Edge-back tu canh phai (1 lan)");
    }

    /** Vuot tu canh trai man hinh sang phai -> cu chi Back (dong app/man hinh). */
    public static void swipeFromLeftEdgeToBack(AndroidDriver driver) {
        org.openqa.selenium.Dimension size = driver.manage().window().getSize();
        int startX = 5;
        int endX   = (int) (size.width * 0.6);
        int y      = size.height / 2;
        swipe(driver, startX, y, endX, y, 300);
        LogUtils.info("Swipe tu canh trai -> Back");
    }

    /** Scroll len (vuot tu duoi len tren). */
    public static void scrollUp(AndroidDriver driver) {
        org.openqa.selenium.Dimension size = driver.manage().window().getSize();
        int x      = size.width / 2;
        int startY = (int) (size.height * 0.7);
        int endY   = (int) (size.height * 0.3);
        swipe(driver, x, startY, x, endY, 400);
    }

    /** Scroll xuong (vuot tu tren xuong duoi). */
    public static void scrollDown(AndroidDriver driver) {
        org.openqa.selenium.Dimension size = driver.manage().window().getSize();
        int x      = size.width / 2;
        int startY = (int) (size.height * 0.3);
        int endY   = (int) (size.height * 0.7);
        swipe(driver, x, startY, x, endY, 400);
    }
}
