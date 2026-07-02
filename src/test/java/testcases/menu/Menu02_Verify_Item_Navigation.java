package testcases.menu;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import constants.AppConstants;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.MenuPage;
import report.ExtentReportManager;

/**
 * Module: Menu (Drawer)
 * Muc tieu: Kiem tra chon tung item trong drawer dieu huong dung.
 *
 * LUU Y (theo hanh vi app): back ve tu man dich -> drawer DA DONG, nen moi test
 * phai mo lai drawer. Vi moi test launch app moi nen viec nay tu nhien.
 *  - Equalizer -> app he thong (Dolby Atmos), Rate us -> Play Store, Share app -> share sheet
 *    (deu la package ngoai) -> quay lai bang activateApp.
 *  - Downloaded / Privacy / Settings -> man trong app -> BACK ve Home.
 *  - Version -> chi dong drawer, ve Home.
 */
public class Menu02_Verify_Item_Navigation extends BaseTest {

    private void openDrawer(HomePage home) {
        home.tapDrawerIcon();
        home.sleep(1000);
        Assert.assertTrue(home.isDrawerOpen(), "Khong mo duoc drawer");
    }

    /** Quay lai app sau khi mo app ngoai, dam bao ve Home (drawer dong). */
    private void returnToHomeFromExternal(HomePage home) {
        getDriver().activateApp(AppConstants.APP_PACKAGE);
        home.sleep(1500);
        if (home.isDrawerOpen()) home.closeMenuDrawer();
        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home sau khi quay lai app");
    }

    @Test(description = "TC_MENU_005: Click Equalizer mo Android Dolby Atmos settings")
    public void TC_MENU_005_equalizer_opens_system_settings() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openDrawer(home);
        menu.tapEqualizer();
        home.sleep(2000);
        ExtentReportManager.getTest().log(Status.INFO, "Package hien tai: " + getDriver().getCurrentPackage());
        Assert.assertTrue(menu.isOnSystemSettings(),
                "Khong mo app he thong Settings (" + AppConstants.SETTINGS_PACKAGE + ")");
        ExtentReportManager.getTest().log(Status.PASS, "Equalizer mo Settings he thong OK.");
        returnToHomeFromExternal(home);
    }

    @Test(description = "TC_MENU_006: Click Downloaded mo man Downloaded trong app")
    public void TC_MENU_006_downloaded_opens_screen() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openDrawer(home);
        menu.tapDownloaded();
        home.sleep(1500);
        Assert.assertTrue(menu.isDownloadedScreenDisplayed(), "Khong mo man Downloaded");
        ExtentReportManager.getTest().log(Status.PASS, "Mo man Downloaded OK.");

        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home");
    }

    @Test(description = "TC_MENU_007: Click Privacy policy mo trang Privacy")
    public void TC_MENU_007_privacy_policy() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openDrawer(home);
        menu.tapPrivacyPolicy();
        home.sleep(1500);
        Assert.assertTrue(menu.isPrivacyPolicyScreenDisplayed(),
                "Khong mo trang Privacy policy (khong thay WebView)");
        ExtentReportManager.getTest().log(Status.PASS, "Mo trang Privacy policy OK.");

        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home");
    }

    @Test(description = "TC_MENU_008: Click Rate us mo Play Store")
    public void TC_MENU_008_rate_us_play_store() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openDrawer(home);
        menu.tapRateUs();
        home.sleep(2000);
        ExtentReportManager.getTest().log(Status.INFO, "Package hien tai: " + getDriver().getCurrentPackage());
        Assert.assertTrue(menu.isOnExternalApp(), "Khong roi app sang Play Store / app ngoai");
        ExtentReportManager.getTest().log(Status.PASS, "Rate us mo app ngoai (Play Store) OK.");
        returnToHomeFromExternal(home);
    }

    @Test(description = "TC_MENU_009: Click Share app mo Android share sheet")
    public void TC_MENU_009_share_sheet() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openDrawer(home);
        menu.tapShareApp();
        home.sleep(2000);
        ExtentReportManager.getTest().log(Status.INFO, "Package hien tai: " + getDriver().getCurrentPackage());
        Assert.assertTrue(menu.isOnShareSheet(),
                "Khong mo share sheet (" + AppConstants.INTENT_RESOLVER_PACKAGE + ")");
        ExtentReportManager.getTest().log(Status.PASS, "Share app mo share sheet OK.");
        returnToHomeFromExternal(home);
    }

    @Test(description = "TC_MENU_010: Click Settings mo man Settings trong app")
    public void TC_MENU_010_settings_opens_screen() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openDrawer(home);
        menu.tapSettings();
        home.sleep(1500);
        Assert.assertTrue(menu.isSettingsScreenDisplayed(), "Khong mo man Settings");
        ExtentReportManager.getTest().log(Status.PASS, "Mo man Settings OK.");

        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home");
    }

    @Test(description = "TC_MENU_011: Click Version dong drawer, quay ve Home")
    public void TC_MENU_011_version_back_home() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openDrawer(home);
        menu.tapVersion();
        home.sleep(1000);
        Assert.assertFalse(home.isDrawerOpen(), "Drawer khong dong sau khi tap Version");
        Assert.assertTrue(home.isHomeDisplayed(), "Khong quay ve Home sau khi tap Version");
        ExtentReportManager.getTest().log(Status.PASS, "Version dong drawer, ve Home OK.");
    }
}