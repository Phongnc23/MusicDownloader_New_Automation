package pages;

import base.BasePage;
import constants.AppConstants;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utils.GestureUtils;

/**
 * Page Object cho MENU (Drawer) cua app Music Downloader (Flutter).
 *
 * Pham vi: cac item trong drawer, cac man hinh dich khi chon item, dialog Sleep timer
 * (INITIAL / ACTIVE / Custom), va man Settings. Viec MO/DONG drawer + Home oracle +
 * exit dialog van dung lai HomePage (drawer la overlay cua Home).
 *
 * Ghi chu locator:
 *  - Item drawer co content-desc. Item RIENG cua drawer (Equalizer, Privacy policy,
 *    Share app, Exit app, Version) -> accessibilityId KHONG nhap nhang -> click truc tiep.
 *  - Item TRUNG ten voi card o Home (Downloaded, Sleep timer, Rate us, Settings) ->
 *    content-desc khop CA card phia sau -> tap theo TOA DO trong drawer (x<675) cho chac.
 *  - Preset Sleep timer co content-desc dang "15\nmins" (co newline) -> tap toa do.
 *  - Toa do suy tu DOM tren thiet bi 1720x2408 (xem .env neu doi may).
 */
public class MenuPage extends BasePage {

    // ===== Toa do tap item drawer (tam item, x = giua drawer 0..675) =====
    private static final int DRAWER_CX        = 337;
    private static final int Y_DOWNLOADED     = 593;
    private static final int Y_SLEEP_TIMER    = 719;
    private static final int Y_RATE_US        = 971;
    private static final int Y_SETTINGS       = 1224;

    // ===== Drawer header / item rieng (accessibilityId) =====
    private final By drawerAppName  = AppiumBy.accessibilityId("Music Downloader");
    private final By drawerTagline  = AppiumBy.accessibilityId("Enjoy Listening");
    private final By itemEqualizer  = AppiumBy.accessibilityId("Equalizer");
    private final By itemDownloaded = AppiumBy.accessibilityId("Downloaded");
    private final By itemSleepTimer = AppiumBy.accessibilityId("Sleep timer");
    private final By itemPrivacy    = AppiumBy.accessibilityId("Privacy policy");
    private final By itemRateUs     = AppiumBy.accessibilityId("Rate us");
    private final By itemShareApp   = AppiumBy.accessibilityId("Share app");
    private final By itemSettings   = AppiumBy.accessibilityId("Settings");
    private final By itemExitApp    = AppiumBy.accessibilityId("Exit app");
    private final By itemVersion    = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"Version\")");

    // ===== Man Privacy policy (WebView) =====
    private final By webView = AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.webkit.WebView\")");

    // ===== Man Settings =====
    private final By settingsDownloadFolder = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"Download Folder\")");
    private final By settingsLanguages = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"Languages\")");

    // ===== Dialog "Change language" (mo tu Settings > Languages) =====
    // Chi KIEM TRA dialog mo roi Cancel - KHONG bao gio Apply (Apply doi ngon ngu that ->
    // toan bo text UI doi -> hong locator tieng Anh cua CA suite).
    private final By langDialogTitle = AppiumBy.accessibilityId("Change language");
    private final By langApply       = AppiumBy.accessibilityId("Apply");
    private final By langCancel      = AppiumBy.accessibilityId("Cancel");
    private final By langOptEnglish  = AppiumBy.accessibilityId("English");

    // ===== Man Downloaded =====
    private final By downloadedTracksCount = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"tracks\")");
    private final By homeSearchBar = AppiumBy.accessibilityId("Search music online...");

    // ===== Sleep timer dialog =====
    private final By stCustom    = AppiumBy.accessibilityId("Custom");
    private final By stSetTimer  = AppiumBy.accessibilityId("Set timer");
    private final By stReset     = AppiumBy.accessibilityId("Reset");
    private final By stCancel    = AppiumBy.accessibilityId("Cancel");
    private final By stScrim     = AppiumBy.accessibilityId("Scrim");
    private final By stMinsAny   = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"mins\")");
    private final By stCountdown = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionStartsWith(\"Timer:\")");

    // Toa do preset (tam o cua) - dialog Sleep timer
    private static final int ST_CX_L = 407, ST_CX_M = 860, ST_CX_R = 1313;
    private static final int ST_CY_TOP = 1593, ST_CY_BOT = 2001;

    // ===== Custom sleep timer dialog =====
    private final By customTitle = AppiumBy.accessibilityId("Custom sleep timer");
    private final By customInput = AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.EditText\")");
    // Khi Custom dialog DANG mo, sleep timer dialog phia sau bi thay -> "Done"/"Cancel"
    // la DUY NHAT -> click theo element (on dinh hon tap toa do, nhat la khi ban phim mo
    // lam layout dialog dich len khien toa do bi truot).
    private final By customDone   = AppiumBy.accessibilityId("Done");
    private final By customCancel = AppiumBy.accessibilityId("Cancel");

    // =========================================================
    //  DRAWER - hien thi
    // =========================================================
    public boolean isDrawerHeaderDisplayed() {
        return existsImmediately(drawerAppName) && existsImmediately(drawerTagline);
    }

    public boolean areAllDrawerItemsDisplayed() {
        return existsImmediately(itemEqualizer)
                && existsImmediately(itemDownloaded)
                && existsImmediately(itemSleepTimer)
                && existsImmediately(itemPrivacy)
                && existsImmediately(itemRateUs)
                && existsImmediately(itemShareApp)
                && existsImmediately(itemSettings)
                && existsImmediately(itemVersion)
                && existsImmediately(itemExitApp);
    }

    public String getDrawerVersionText() {
        return getContentDesc(itemVersion);
    }

    public boolean isVersion9999Displayed() {
        return getDrawerVersionText().contains("9999");
    }

    // ===== Theme toggle (dau drawer, 3 icon trai->phai: DARK(mat trang) | LIGHT(mat troi) | SYSTEM(nua vong)) =====
    // DOM inner [135,299][540,380] chia 3 phan bang nhau -> tap theo TOA DO. (Da xac nhan bang screenshot:
    // trai la mat trang = Dark, giua la mat troi = Light, phai la nua-vong-tron = System.)
    private static final int THEME_Y = 340;
    private static final int THEME_DARK_X   = 202;   // segment trai  (icon mat trang) = Dark
    private static final int THEME_LIGHT_X  = 337;   // segment giua  (icon mat troi)  = Light
    private static final int THEME_SYSTEM_X = 472;   // segment phai  (icon nua vong)  = System

    public void tapThemeLight()  { GestureUtils.tap(driver, THEME_LIGHT_X,  THEME_Y); log.info("Chon theme Light"); }
    public void tapThemeDark()   { GestureUtils.tap(driver, THEME_DARK_X,   THEME_Y); log.info("Chon theme Dark"); }
    public void tapThemeSystem() { GestureUtils.tap(driver, THEME_SYSTEM_X, THEME_Y); log.info("Chon theme System"); }

    // =========================================================
    //  DRAWER - tap item
    // =========================================================
    public void tapEqualizer()    { click(itemEqualizer); log.info("Tap drawer: Equalizer"); }
    public void tapPrivacyPolicy(){ click(itemPrivacy);   log.info("Tap drawer: Privacy policy"); }
    public void tapShareApp()     { click(itemShareApp);  log.info("Tap drawer: Share app"); }
    public void tapExitApp()      { click(itemExitApp);   log.info("Tap drawer: Exit app"); }
    public void tapVersion()      { click(itemVersion);   log.info("Tap drawer: Version"); }

    // Trung ten voi card Home -> tap toa do trong drawer
    public void tapDownloaded() { GestureUtils.tap(driver, DRAWER_CX, Y_DOWNLOADED);  log.info("Tap drawer: Downloaded"); }
    public void tapSleepTimer() { GestureUtils.tap(driver, DRAWER_CX, Y_SLEEP_TIMER); log.info("Tap drawer: Sleep timer"); }
    public void tapRateUs()     { GestureUtils.tap(driver, DRAWER_CX, Y_RATE_US);     log.info("Tap drawer: Rate us"); }
    public void tapSettings()   { GestureUtils.tap(driver, DRAWER_CX, Y_SETTINGS);    log.info("Tap drawer: Settings"); }

    // =========================================================
    //  MAN DICH - oracle
    // =========================================================
    /** Dang o app he thong Settings (Equalizer mo Dolby Atmos). */
    public boolean isOnSystemSettings() {
        return AppConstants.SETTINGS_PACKAGE.equals(driver.getCurrentPackage());
    }

    /** Dang o share sheet he thong. */
    public boolean isOnShareSheet() {
        return AppConstants.INTENT_RESOLVER_PACKAGE.equals(driver.getCurrentPackage());
    }

    /** Da roi app (mo app ngoai, vd Play Store khi Rate us). */
    public boolean isOnExternalApp() {
        String pkg = driver.getCurrentPackage();
        return pkg != null && !AppConstants.APP_PACKAGE.equals(pkg);
    }

    /** Man Downloaded trong app: co dong "N tracks" va KHONG con search bar Home. */
    public boolean isDownloadedScreenDisplayed() {
        return existsImmediately(downloadedTracksCount) && !existsImmediately(homeSearchBar);
    }

    /** Man Privacy policy: co WebView. */
    public boolean isPrivacyPolicyScreenDisplayed() {
        return existsImmediately(webView);
    }

    // =========================================================
    //  MAN SETTINGS
    // =========================================================
    public boolean isSettingsScreenDisplayed() {
        return existsImmediately(settingsDownloadFolder);
    }

    public String getDownloadFolderPath() {
        String d = getContentDesc(settingsDownloadFolder); // "Download Folder\n/storage/..."
        int nl = d.indexOf('\n');
        return nl >= 0 ? d.substring(nl + 1).trim() : "";
    }

    public boolean hasLanguagesDevice() {
        return getContentDesc(settingsLanguages).contains("Device");
    }

    public void tapLanguages() { click(settingsLanguages); log.info("Tap Settings: Languages"); }
    public void tapRateUsSettings() { click(itemRateUs); log.info("Tap Settings: Rate us"); }

    // ===== Toa do dialog Change language (DOM 1720x2408) - LANGUAGE-NEUTRAL =====
    // Dung TOA DO vi khi da doi ngon ngu, moi text (ke ca title/Apply) doi -> accessibilityId
    // tieng Anh khong con khop. Toa do va THU TU option KHONG doi theo ngon ngu.
    private static final int LANG_ROW_X = 860,  LANG_ROW_Y = 384;    // hang "Languages" tren man Settings
    private static final int LANG_OPT_X = 860;
    private static final int LANG_OPT_DEVICE_Y  = 1548;             // row 0 (LUON dau tien) = English tren may nay
    private static final int LANG_OPT_SPANISH_Y = 1845;            // row 3 (thu tu: Device,Arabic,English,Spanish,...)
    private static final int LANG_APPLY_X = 1211, LANG_APPLY_Y = 2300;
    private final By scrimAny = AppiumBy.accessibilityId("Scrim");  // ky thuat, khong dich -> detect dialog mo

    /** Dialog Change language dang mo (title tieng Anh). Chi TRUE khi app DANG English. */
    public boolean isChangeLanguageDialogOpen() {
        return existsImmediately(langDialogTitle)
                || (existsImmediately(langApply) && existsImmediately(langOptEnglish));
    }

    /** Mo dialog Languages bang TOA DO (tap hang Languages). BACK dong dialog cu neu dang mo. */
    public void openLanguageDialog() {
        if (existsImmediately(scrimAny)) { driver.navigate().back(); sleep(700); }
        GestureUtils.tap(driver, LANG_ROW_X, LANG_ROW_Y);
        log.info("Mo dialog Change language (toa do hang Languages)");
    }

    /** Doi sang Spanish (row 3 o dialog English) + Apply. Goi khi dialog DANG mo (English). */
    public void changeLanguageToSpanish() {
        GestureUtils.tap(driver, LANG_OPT_X, LANG_OPT_SPANISH_Y);
        sleep(600);
        GestureUtils.tap(driver, LANG_APPLY_X, LANG_APPLY_Y);
        sleep(2500);
        log.info("Chon Spanish + Apply");
    }

    /**
     * BAT BUOC dua app ve English: chon "Device" (row 0 - LUON dau tien, = English tren may nay) + Apply.
     * Idempotent + coordinate-based -> chay duoc du app dang o ngon ngu nao. GIA DINH dang o man Settings.
     * @return true neu da xac nhan English (thay title "Change language").
     */
    public boolean ensureAppEnglish() {
        for (int i = 0; i < 3; i++) {
            openLanguageDialog();
            sleep(1500);
            if (existsImmediately(langDialogTitle)) {          // "Change language" (English) -> da English
                driver.navigate().back(); sleep(700);           // dong dialog, khong doi gi
                log.info("Da ve English (title 'Change language').");
                return true;
            }
            GestureUtils.tap(driver, LANG_OPT_X, LANG_OPT_DEVICE_Y);   // row 0 = Device
            sleep(600);
            GestureUtils.tap(driver, LANG_APPLY_X, LANG_APPLY_Y);
            sleep(2500);
            log.info("Revert: chon Device (row 0) + Apply, lan {}", i + 1);
        }
        openLanguageDialog(); sleep(1500);
        boolean ok = existsImmediately(langDialogTitle);
        if (existsImmediately(scrimAny)) { driver.navigate().back(); sleep(700); }
        return ok;
    }

    public boolean areAllSettingsItemsDisplayed() {
        return existsImmediately(settingsDownloadFolder)
                && existsImmediately(settingsLanguages)
                && existsImmediately(itemRateUs)
                && existsImmediately(itemPrivacy)
                && existsImmediately(itemShareApp)
                && existsImmediately(itemVersion);
    }

    // =========================================================
    //  SLEEP TIMER dialog - trang thai
    // =========================================================
    /** Dialog Sleep timer dang mo (co "Custom" + cac preset "...mins"). */
    public boolean isSleepTimerDialogOpen() {
        return existsImmediately(stCustom) && existsImmediately(stMinsAny);
    }

    /** Trang thai INITIAL: dialog mo + co nut "Set timer" (chua chay). */
    public boolean isSleepTimerInitial() {
        return isSleepTimerDialogOpen() && existsImmediately(stSetTimer);
    }

    /** Trang thai ACTIVE: co countdown "Timer: ..." hoac nut "Reset". */
    public boolean isSleepTimerActive() {
        return existsImmediately(stCountdown) || existsImmediately(stReset);
    }

    public String getActiveTimerText() {
        return getContentDesc(stCountdown);
    }

    // =========================================================
    //  SLEEP TIMER dialog - thao tac
    // =========================================================
    public void selectPreset15() { GestureUtils.tap(driver, ST_CX_L, ST_CY_TOP); log.info("Chon 15 mins"); }
    public void selectPreset30() { GestureUtils.tap(driver, ST_CX_M, ST_CY_TOP); log.info("Chon 30 mins"); }
    public void selectPreset45() { GestureUtils.tap(driver, ST_CX_R, ST_CY_TOP); log.info("Chon 45 mins"); }
    public void selectPreset60() { GestureUtils.tap(driver, ST_CX_L, ST_CY_BOT); log.info("Chon 60 mins"); }
    public void selectPreset90() { GestureUtils.tap(driver, ST_CX_M, ST_CY_BOT); log.info("Chon 90 mins"); }

    public void tapCustom()           { click(stCustom);   log.info("Tap Custom (sleep timer)"); }
    public void tapSetTimer()         { click(stSetTimer); log.info("Tap Set timer"); }
    public void tapReset()            { click(stReset);    log.info("Tap Reset (sleep timer)"); }
    public void tapCancelSleepDialog(){ click(stCancel);   log.info("Tap Cancel (sleep timer)"); }
    public void tapScrim()            { click(stScrim);    log.info("Tap Scrim (dong sleep dialog)"); }

    /** Neu dialog dang ACTIVE -> Reset ve INITIAL (goi khi dialog DANG mo). */
    public void resetIfActive() {
        if (isSleepTimerActive()) {
            tapReset();
            sleep(600);
            log.info("Da Reset timer dang chay ve INITIAL");
        }
    }

    // =========================================================
    //  CUSTOM sleep timer dialog
    // =========================================================
    public boolean isCustomDialogOpen() {
        return existsImmediately(customTitle);
    }

    public void enterCustomMinutes(String minutes) {
        WebElement in = findElement(customInput);
        in.click();
        in.clear();
        in.sendKeys(minutes);
        log.info("Nhap custom minutes: {}", minutes);
    }

    public void tapCustomDone()   { click(customDone);   log.info("Tap Done (custom)"); }
    public void tapCustomCancel() { click(customCancel); log.info("Tap Cancel (custom)"); }
}