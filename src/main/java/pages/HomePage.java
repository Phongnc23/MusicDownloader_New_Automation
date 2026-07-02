package pages;

import base.BasePage;
import constants.AppConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import utils.GestureUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Page Object cho man hinh Home (app Music Downloader moi - Flutter).
 *
 * GOP TAT CA trong 1 file (khong tach component): header, search bar, quick actions,
 * bottom nav, MINI PLAYER, EXIT DIALOG, DRAWER.
 *
 * Dac diem DOM:
 *  - Card/tab la ImageView co content-desc -> accessibilityId / UiSelector
 *  - 2 icon goc (drawer trai, search phai) KHONG co content-desc -> tap theo toa do (bounds)
 *  - "Home" co 2 element: title (View) + bottom tab (ImageView) -> phan biet bang clickable/class
 *  - Single-Activity -> verify dieu huong qua su xuat hien/bien mat element (isHomeDisplayed)
 */
public class HomePage extends BasePage {

    // ===== HEADER (content-desc) =====
    private final By homeTitle = AppiumBy.androidUIAutomator("new UiSelector().description(\"Home\").clickable(false)");
    // 2 icon goc khong co content-desc -> verify ton tai bang bounds-xpath
    private final By drawerIcon = By.xpath("//android.widget.ImageView[@bounds='[9,64][117,154]']");
    private final By searchIcon = By.xpath("//android.widget.ImageView[@bounds='[1594,64][1702,154]']");

    // ===== SEARCH BAR + QUICK ACTIONS =====
    private final By searchBar      = AppiumBy.accessibilityId("Search music online...");
    private final By downloadedCard = AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Downloaded\")");
    private final By sleepTimerCard = AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Sleep timer\")");
    private final By rateUsCard     = AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Rate us\")");
    private final By settingsCard   = AppiumBy.androidUIAutomator("new UiSelector().description(\"Settings\")");

    // ===== BOTTOM NAVIGATION =====
    private final By navHome      = AppiumBy.androidUIAutomator("new UiSelector().description(\"Home\").clickable(true)");
    private final By navTracks    = AppiumBy.accessibilityId("Tracks");
    private final By navArtists   = AppiumBy.accessibilityId("Artists");
    private final By navAlbums    = AppiumBy.accessibilityId("Albums");
    private final By navPlaylists = AppiumBy.accessibilityId("Playlists");

    // ===== MINI PLAYER (gop trong HomePage) =====
    // Container = View scrollable+clickable; info = ImageView content-desc dang "2%, <unknown>"
    private final By miniPlayerContainer = AppiumBy.androidUIAutomator("new UiSelector().scrollable(true).clickable(true)");
    private final By miniPlayerInfo      = AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"%, \")");
    // Ten bai = View con co content-desc KHONG chua '%'
    private final By miniPlayerTitle = By.xpath(
            "(//android.view.View[@scrollable='true']" +
                    "//android.view.View[string(@content-desc)!='' and not(contains(@content-desc,'%'))])[1]");

    // ===== FULL PLAYER (mo khi tap mini player) =====
    // Full player co node content-desc bat dau "Playing now"; Home KHONG co -> oracle dang tin.
    private final By fullPlayerInfo = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"Playing now\")");

    // ===== DRAWER (gop trong HomePage) =====
    // "Exit app" chi co trong drawer -> nhan biet drawer dang mo
    private final By drawerExitApp = AppiumBy.accessibilityId("Exit app");

    // ===== EXIT DIALOG (gop trong HomePage) =====
    private final By exitDialogTitle  = AppiumBy.accessibilityId("Are you sure you want to exit?");
    private final By exitDialogExit   = AppiumBy.accessibilityId("Exit");
    private final By exitDialogCancel = AppiumBy.accessibilityId("Cancel");

    private static final Pattern PROGRESS_PATTERN = Pattern.compile("(\\d+)%");

    // =========================================================
    //  VERIFY - hien thi
    // =========================================================
    public boolean isAppLaunched() {
        String current = driver.getCurrentPackage();
        log.info("Current package: {}", current);
        return AppConstants.APP_PACKAGE.equals(current);
    }

    public boolean isHomeTitleDisplayed()  { return existsImmediately(homeTitle); }
    public boolean isDrawerIconDisplayed() { return existsImmediately(drawerIcon); }
    public boolean isSearchIconDisplayed() { return existsImmediately(searchIcon); }
    public boolean isSearchBarDisplayed()  { return existsImmediately(searchBar); }
    public boolean isDownloadedDisplayed() { return existsImmediately(downloadedCard); }
    public boolean isSleepTimerDisplayed() { return existsImmediately(sleepTimerCard); }
    public boolean isRateUsDisplayed()     { return existsImmediately(rateUsCard); }
    public boolean isSettingsDisplayed()   { return existsImmediately(settingsCard); }

    public boolean areAllQuickActionsDisplayed() {
        return isDownloadedDisplayed() && isSleepTimerDisplayed()
                && isRateUsDisplayed() && isSettingsDisplayed();
    }

    public boolean isBottomNavDisplayed() {
        return existsImmediately(navHome) && existsImmediately(navTracks)
                && existsImmediately(navArtists) && existsImmediately(navAlbums)
                && existsImmediately(navPlaylists);
    }

    /** Oracle: dang o Home (search bar + Downloaded card cung hien). */
    public boolean isHomeDisplayed() {
        return existsImmediately(searchBar) && existsImmediately(downloadedCard);
    }

    /** Drawer dang mo (nhan biet bang item "Exit app" - chi co trong drawer). */
    public boolean isDrawerOpen() {
        return existsImmediately(drawerExitApp);
    }

    public boolean isExitDialogDisplayed() {
        return existsImmediately(exitDialogTitle)
                || (existsImmediately(exitDialogExit) && existsImmediately(exitDialogCancel));
    }

    // =========================================================
    //  VERIFY - du lieu dong
    // =========================================================
    public int getDownloadedCount() {
        String desc = getContentDesc(downloadedCard);
        Matcher m = Pattern.compile("(\\d+)").matcher(desc);
        return m.find() ? Integer.parseInt(m.group(1)) : -1;
    }

    public String getSleepTimerState() {
        String desc = getContentDesc(sleepTimerCard);
        String[] parts = desc.split("\\n");
        return parts.length > 1 ? parts[parts.length - 1].trim() : desc.trim();
    }

    // =========================================================
    //  MINI PLAYER (gop)
    // =========================================================
    public boolean isMiniPlayerDisplayed() {
        return existsImmediately(miniPlayerContainer) || existsImmediately(miniPlayerInfo);
    }

    /** Full player (man "Playing now") dang mo. */
    public boolean isFullPlayerDisplayed() {
        return existsImmediately(fullPlayerInfo);
    }

    /** Ten bai dang phat (rong neu khong co). */
    public String getMiniPlayerTrackTitle() {
        return existsImmediately(miniPlayerTitle) ? getContentDesc(miniPlayerTitle) : "";
    }

    /** Tien do % tu content-desc info ("2%, <unknown>"). -1 neu khong doc duoc. */
    public int getMiniPlayerProgress() {
        if (!existsImmediately(miniPlayerInfo)) return -1;
        Matcher m = PROGRESS_PATTERN.matcher(getContentDesc(miniPlayerInfo));
        return m.find() ? Integer.parseInt(m.group(1)) : -1;
    }

    /**
     * "Dang phat?": poll trong waitMs (300ms/lan), tra ve true NGAY khi thay DAU HIEU CHUYEN DONG
     * giua 2 lan doc lien tiep. Thu vien co bai CUC NGAN (0:03-0:07) -> bai tu chuyen lien tuc
     * (auto-next) khien mini player chap chon. Coi la dang phat khi giua 2 mau lien tiep:
     *   (a) % TANG (cung bai chay tiep), HOAC
     *   (b) % TUT manh (>2) = sang bai moi, HOAC
     *   (c) TITLE doi (auto-next sang bai khac), HOAC
     *   (d) tu khong doc duoc (-1) sang doc duoc % > 0.
     * Paused/dung han -> moi mau giong nhau -> false.
     */
    public boolean isMiniPlayerProgressAdvancing(long waitMs) {
        int prev = getMiniPlayerProgress();
        String prevTitle = getMiniPlayerTrackTitle();
        long deadline = System.currentTimeMillis() + waitMs;
        while (System.currentTimeMillis() < deadline) {
            sleep(300);
            int cur = getMiniPlayerProgress();
            String t = getMiniPlayerTrackTitle();
            boolean titleChanged = !t.isEmpty() && !prevTitle.isEmpty() && !t.equals(prevTitle);
            boolean progAdvanced = cur >= 0 && prev >= 0 && cur > prev;
            boolean songSwitched = cur >= 0 && prev >= 0 && cur < prev - 2; // % tut = bai moi
            boolean cameAlive   = prev < 0 && cur > 0;
            if (titleChanged || progAdvanced || songSwitched || cameAlive) {
                log.info("[MiniPlayer] dang phat (prev={}% cur={}% titleChanged={})", prev, cur, titleChanged);
                return true;
            }
            if (cur >= 0) prev = cur;
            if (!t.isEmpty()) prevTitle = t;
        }
        log.info("[MiniPlayer] khong thay dau hieu phat (prev={}%)", prev);
        return false;
    }

    /**
     * Tap vung info mini player -> mo full player (Play Now).
     * Content-desc mini player ("32%, <unknown>") doi MOI GIAY -> Flutter recreate node ->
     * click(element) hay dinh StaleElementReference. Nen tap theo TOA DO tam vung info
     * (tranh nut play/pause + queue ben phai x>1486) + retry.
     */
    public void tapMiniPlayer() {
        for (int i = 0; i < 3; i++) {
            try {
                Rectangle r = findElement(miniPlayerInfo).getRect();
                int x = Math.min(r.getX() + r.getWidth() / 2, 800); // vung title trai, xa 2 nut phai
                int y = r.getY() + r.getHeight() / 2;
                GestureUtils.tap(driver, x, y);
                log.info("Tap mini player -> full player (x={} y={})", x, y);
                return;
            } catch (Exception e) {
                log.warn("tapMiniPlayer lan {} loi (stale?) : {}", i + 1, e.getMessage());
                sleep(400);
            }
        }
        log.warn("tapMiniPlayer: that bai sau 3 lan");
    }

    public void tapMiniPlayerPlayPause() { tapMiniPlayerButton(false); }
    public void tapMiniPlayerQueue()     { tapMiniPlayerButton(true); }

    /**
     * Neu mini player DANG PHAT (churn) -> pause de man hinh DUNG lai. Dung o tearDown:
     * @AfterMethod chi quit driver chu KHONG tat app -> nhac (bai 0:03) van phat nen giua cac
     * test/file -> test sau doc UI luc churn -> stale/flaky. Pause o day cat dut churn leak.
     */
    public void pausePlaybackIfPlaying() {
        try {
            if (!isMiniPlayerDisplayed()) return;
            int p1 = getMiniPlayerProgress();
            String t1 = getMiniPlayerTrackTitle();
            sleep(500);
            int p2 = getMiniPlayerProgress();
            String t2 = getMiniPlayerTrackTitle();
            boolean playing = (p1 >= 0 && p2 >= 0 && p1 != p2) || (!t1.isEmpty() && !t1.equals(t2));
            if (playing) {
                tapMiniPlayerPlayPause();
                log.info("tearDown: da pause playback (tranh churn leak sang test sau)");
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Tap 1 trong 2 nut narrow trong dai mini player (center-y ~2190).
     * rightmost=true -> queue (nut phai); false -> play/pause (nut trai).
     * Tim theo vi tri (khong hard-code toa do) de ben voi update doi layout.
     */
    private void tapMiniPlayerButton(boolean rightmost) {
        List<WebElement> imgs = driver.findElements(AppiumBy.androidUIAutomator(
                "new UiSelector().className(\"android.widget.ImageView\").clickable(true)"));
        WebElement target = null;
        int best = rightmost ? -1 : Integer.MAX_VALUE;
        for (WebElement el : imgs) {
            try {
                Rectangle r = el.getRect();
                int cy = r.getY() + r.getHeight() / 2;
                int cx = r.getX() + r.getWidth() / 2;
                // 2 nut narrow trong dai mini player; loai container full-width (width lon)
                if (cy > 2100 && cy < 2255 && r.getWidth() < 300) {
                    if (rightmost ? cx > best : cx < best) {
                        best = cx;
                        target = el;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        if (target != null) {
            Rectangle r = target.getRect();
            GestureUtils.tap(driver, r.getX() + r.getWidth() / 2, r.getY() + r.getHeight() / 2);
            log.info("Tap mini player button (rightmost={})", rightmost);
        } else {
            log.warn("Khong tim duoc nut mini player (rightmost={})", rightmost);
        }
    }

    // =========================================================
    //  ACTION - header / search / quick actions
    // =========================================================
    public void tapDrawerIcon() {
        GestureUtils.tap(driver, AppConstants.DRAWER_ICON_X, AppConstants.DRAWER_ICON_Y);
        log.info("Tap drawer icon (goc trai)");
    }

    public void tapSearchIcon() {
        GestureUtils.tap(driver, AppConstants.SEARCH_ICON_X, AppConstants.SEARCH_ICON_Y);
        log.info("Tap search icon (goc phai)");
    }

    public void tapSearchBar()  { click(searchBar);     log.info("Tap search bar"); }
    public void tapDownloaded() { click(downloadedCard); log.info("Tap Downloaded card"); }
    public void tapSleepTimer() { click(sleepTimerCard); log.info("Tap Sleep timer card"); }
    public void tapRateUs()     { click(rateUsCard);    log.info("Tap Rate us card"); }
    public void tapSettings()   { click(settingsCard);  log.info("Tap Settings card"); }

    public void tapNavHome()      { click(navHome);      log.info("Tap tab Home"); }
    public void tapNavTracks()    { click(navTracks);    log.info("Tap tab Tracks"); }
    public void tapNavArtists()   { click(navArtists);   log.info("Tap tab Artists"); }
    public void tapNavAlbums()    { click(navAlbums);    log.info("Tap tab Albums"); }
    public void tapNavPlaylists() { click(navPlaylists); log.info("Tap tab Playlists"); }

    // =========================================================
    //  ACTION - drawer
    // =========================================================
    /**
     * Dong drawer. QUAN TRONG: KHONG dung BACK — BACK o drawer (top-level Flutter)
     * BUNG dialog "Are you sure you want to exit?" chu KHONG dong drawer.
     * Cach tin cay: tap scrim ben phai drawer; fallback vuot phai->trai. Retry + verify.
     */
    public void closeMenuDrawer() {
        for (int attempt = 0; attempt < 4; attempt++) {
            if (!isDrawerOpen()) {
                if (attempt > 0) log.info("Drawer da dong sau {} lan thu", attempt);
                return;
            }
            Dimension size = driver.manage().window().getSize();
            int w = size.getWidth();
            int h = size.getHeight();
            if (attempt % 2 == 0) {
                GestureUtils.tap(driver, (int) (w * 0.85), h / 2); // tap scrim ngoai drawer
                log.info("Tap scrim de dong drawer");
            } else {
                GestureUtils.swipe(driver, (int) (w * 0.35), h / 2, (int) (w * 0.02), h / 2, 400);
                log.info("Vuot phai->trai de dong drawer");
            }
            sleep(900);
        }
        if (isDrawerOpen()) {
            log.warn("closeMenuDrawer: drawer VAN MO sau 4 lan thu");
        }
    }

    // =========================================================
    //  ACTION - exit dialog
    // =========================================================
    /**
     * Bung dialog xac nhan thoat bang cu chi VUOT TU CANH PHAI -> TRAI (edge-swipe back).
     * (Phim BACK KeyEvent doi luc khong bung dialog tren build Flutter nay.)
     *
     * QUAN TRONG: KHONG vuot lien tuc. Vuot lan 2 khi dialog DA mo = mot back nua ->
     * DONG dialog vua mo (hien len roi tat ngay). Vi vay: vuot 1 lan, CHO DU LAU (3s)
     * cho dialog animate in + a11y tree cap nhat; chi vuot lai khi sau 3s VAN khong co
     * dialog (tuc gesture lan dau khong duoc nhan) -> khong bao gio vuot khi dialog dang mo.
     */
    public void openExitDialogViaBack() {
        // O man Home: 1 lan vuot back -> dialog hien (binh thuong chi can dung 1 lan).
        // TUYET DOI KHONG vuot khi dialog DANG mo (vuot = 1 back nua -> dong dialog vua hien,
        // gay canh "hien roi tat"). Vi vay sau khi vuot phai CHO DU LAU (3s) cho dialog
        // animate in; CHI vuot lai khi het 3s VAN khong thay dialog -> tuc gesture lan truoc
        // KHONG duoc he thong nhan (man dang o Home, khong co dialog) -> vuot lai hoan toan an toan.
        for (int attempt = 1; attempt <= 3; attempt++) {
            GestureUtils.edgeBackFromRight(driver);
            log.info("Vuot canh phai->trai (edge back) lan {} de bung exit dialog", attempt);
            long deadline = System.currentTimeMillis() + 3000;
            while (System.currentTimeMillis() < deadline) {
                if (isExitDialogDisplayed()) {
                    log.info("Exit dialog da hien sau {} lan vuot", attempt);
                    return;
                }
                sleep(200);
            }
            log.info("Het 3s khong thay dialog -> gesture lan {} khong duoc nhan (man o Home, "
                    + "khong co dialog) -> vuot lai an toan", attempt);
        }
        log.warn("openExitDialogViaBack: chua thay exit dialog sau 3 lan vuot");
    }
    public void tapExitOnDialog()   { click(exitDialogExit);   log.info("Tap Exit tren exit dialog"); }
    public void tapCancelOnDialog() { click(exitDialogCancel); log.info("Tap Cancel tren exit dialog"); }

    // =========================================================
    //  DIEU HUONG - tien ich
    // =========================================================
    public void pressBack() {
        driver.pressKey(new KeyEvent(AndroidKey.BACK));
        log.info("Press BACK");
    }

    public void hideKeyboardSafe() {
        try {
            if (driver.isKeyboardShown()) {
                driver.hideKeyboard();
            }
        } catch (Exception ignored) {
        }
    }

    /** Quay ve Home bang BACK toi da 4 lan (KHONG dung khi drawer mo - dung closeMenuDrawer). */
    public void pressBackToHome() {
        for (int i = 0; i < 4; i++) {
            if (isHomeDisplayed()) return;
            hideKeyboardSafe();
            pressBack();
            sleep(800);
        }
    }

    public void returnToHomeViaNav() {
        if (existsImmediately(navHome)) {
            tapNavHome();
            sleep(800);
        }
    }
}