package testcases.menu;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.MenuPage;
import report.ExtentReportManager;

/**
 * Module: Menu (Drawer) - Settings screen
 * Muc tieu: Kiem tra noi dung + hanh vi cac item man Settings.
 *
 * Mo Settings qua CARD Settings o Home (home.tapSettings). Cac case DEU ket thuc TREN man Settings
 * (khong back ve Home giua chung) - cac item chuyen huong ra ngoai (Play Store / share sheet /
 * WebView) chi BACK 1 lan de quay lai Settings. Rieng TC_MENU_024 (back ve Home) chay CUOI CUNG
 * (priority=1) - sau khi test xong toan bo moi quay ve Home.
 *
 * (Moi test van isolate: @BeforeMethod cua BaseTest tao driver moi + openSettings lai tu dau.)
 */
public class Menu04_Verify_Settings_Screen extends BaseTest {

    /** Mo man Settings va dam bao da vao. */
    private void openSettings(HomePage home, MenuPage menu) {
        home.tapSettings();
        home.sleep(1500);
        Assert.assertTrue(menu.isSettingsScreenDisplayed(), "Khong mo duoc man Settings");
    }

    /** Sau khi item chuyen huong (Play Store / share sheet / WebView): BACK 1 lan -> ve lai Settings. */
    private void backToSettings(HomePage home, MenuPage menu) {
        home.pressBack();
        home.sleep(1500);
        Assert.assertTrue(menu.isSettingsScreenDisplayed(), "Back 1 lan khong quay lai man Settings");
        ExtentReportManager.getTest().log(Status.PASS, "Back 1 lan -> quay lai man Settings OK.");
    }

    @Test(description = "TC_MENU_021: Settings co Download Folder voi duong dan")
    public void TC_MENU_021_download_folder_path() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openSettings(home, menu);
        String path = menu.getDownloadFolderPath();
        ExtentReportManager.getTest().log(Status.INFO, "Download Folder path: " + path);
        Assert.assertFalse(path.trim().isEmpty(), "Download Folder khong hien duong dan");
        ExtentReportManager.getTest().log(Status.PASS, "Download Folder hien path dung.");
    }

    @Test(description = "TC_MENU_022: Languages hien thi gia tri 'Device'")
    public void TC_MENU_022_languages_device() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openSettings(home, menu);
        Assert.assertTrue(menu.hasLanguagesDevice(), "Languages khong hien gia tri 'Device'");
        ExtentReportManager.getTest().log(Status.PASS, "Languages hien 'Device' dung.");
    }

    @Test(description = "TC_MENU_023: Settings co day du 6 item")
    public void TC_MENU_023_six_items() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openSettings(home, menu);
        ExtentReportManager.getTest().log(Status.INFO,
                "Download Folder / Languages / Rate us / Privacy policy / Share app / Version");
        Assert.assertTrue(menu.areAllSettingsItemsDisplayed(), "Thieu 1 hoac nhieu item Settings");
        ExtentReportManager.getTest().log(Status.PASS, "Du 6 item Settings.");
    }

    @Test(description = "TC_MENU_027: Settings > Languages doi ngon ngu THAT (Spanish) roi revert ve mac dinh")
    public void TC_MENU_027_change_language() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openSettings(home, menu);
        menu.openLanguageDialog();
        home.sleep(1500);
        Assert.assertTrue(menu.isChangeLanguageDialogOpen(),
                "Tap Languages khong mo dialog Change language");

        try {
            // Doi sang Spanish (Apply) -> UI khong con tieng Anh.
            menu.changeLanguageToSpanish();
            menu.openLanguageDialog();      // mo lai dialog (bang toa do - da o Spanish)
            home.sleep(1500);
            Assert.assertFalse(menu.isChangeLanguageDialogOpen(),
                    "Ngon ngu chua doi (title 'Change language' van la tieng Anh)");
            ExtentReportManager.getTest().log(Status.PASS,
                    "Doi ngon ngu sang Spanish thanh cong (title tieng Anh bien mat).");
        } finally {
            // BAT BUOC revert ve English (Device) du co loi -> tranh hong locator tieng Anh cua CA suite.
            boolean restored = menu.ensureAppEnglish();
            ExtentReportManager.getTest().log(Status.INFO,
                    "Revert ve English: " + (restored ? "OK" : "THAT BAI - CAN KIEM TRA THIET BI"));
            Assert.assertTrue(restored, "KHONG revert duoc ve English sau khi doi ngon ngu");
        }

        // Ket thuc TREN man Settings (da English).
        Assert.assertTrue(menu.isSettingsScreenDisplayed(), "Sau revert khong o lai man Settings");
        ExtentReportManager.getTest().log(Status.PASS, "Doi ngon ngu + revert ve mac dinh, o lai Settings OK.");
    }

    @Test(description = "TC_MENU_028: Settings > Rate us mo Play Store, back lai ve Settings")
    public void TC_MENU_028_rate_us() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openSettings(home, menu);
        menu.tapRateUsSettings();
        home.sleep(2000);
        ExtentReportManager.getTest().log(Status.INFO, "Package hien tai: " + getDriver().getCurrentPackage());
        Assert.assertTrue(menu.isOnExternalApp(), "Rate us khong roi app sang Play Store / app ngoai");
        // MINH CHUNG: chup app ngoai (Play Store) NGAY luc nay, truoc khi back ve Settings
        ExtentReportManager.attachProof("Settings > Rate us mo app ngoai (Play Store) - minh chung");
        backToSettings(home, menu);
    }

    @Test(description = "TC_MENU_029: Settings > Privacy policy mo WebView, back lai ve Settings")
    public void TC_MENU_029_privacy_policy() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openSettings(home, menu);
        menu.tapPrivacyPolicy();
        home.sleep(1500);
        Assert.assertTrue(menu.isPrivacyPolicyScreenDisplayed(),
                "Privacy policy khong mo WebView");
        // MINH CHUNG: chup trang Privacy policy (WebView) NGAY luc nay, truoc khi back ve Settings
        ExtentReportManager.attachProof("Settings > Privacy policy mo WebView - minh chung");
        backToSettings(home, menu);
    }

    @Test(description = "TC_MENU_030: Settings > Share app mo Android share sheet, back lai ve Settings")
    public void TC_MENU_030_share_app() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openSettings(home, menu);
        menu.tapShareApp();
        home.sleep(2000);
        ExtentReportManager.getTest().log(Status.INFO, "Package hien tai: " + getDriver().getCurrentPackage());
        Assert.assertTrue(menu.isOnShareSheet(), "Share app khong mo share sheet");
        // MINH CHUNG: chup Android share sheet NGAY luc nay, truoc khi back ve Settings
        ExtentReportManager.attachProof("Settings > Share app mo Android share sheet - minh chung");
        backToSettings(home, menu);
    }

    // ===== CHAY CUOI CUNG: sau khi test xong toan bo Settings moi quay ve Home =====
    @Test(priority = 1, description = "TC_MENU_024: Click Back quay ve Home tu Settings")
    public void TC_MENU_024_back_to_home() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openSettings(home, menu);
        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong quay ve Home tu Settings");
        ExtentReportManager.getTest().log(Status.PASS, "Back tu Settings ve Home OK.");
    }
}
