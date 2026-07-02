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
 * Muc tieu: Kiem tra noi dung man Settings.
 *
 * Mo Settings qua CARD Settings o Home (home.tapSettings) - cung man voi drawer Settings,
 * don gian hon. Duong dan drawer -> Settings da co o TC_MENU_010.
 */
public class Menu04_Verify_Settings_Screen extends BaseTest {

    /** Mo man Settings va dam bao da vao. */
    private void openSettings(HomePage home, MenuPage menu) {
        home.tapSettings();
        home.sleep(1500);
        Assert.assertTrue(menu.isSettingsScreenDisplayed(), "Khong mo duoc man Settings");
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

        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home");
    }

    @Test(description = "TC_MENU_022: Languages hien thi gia tri 'Device'")
    public void TC_MENU_022_languages_device() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openSettings(home, menu);
        Assert.assertTrue(menu.hasLanguagesDevice(), "Languages khong hien gia tri 'Device'");
        ExtentReportManager.getTest().log(Status.PASS, "Languages hien 'Device' dung.");

        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home");
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

        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home");
    }

    @Test(description = "TC_MENU_024: Click Back quay ve Home tu Settings")
    public void TC_MENU_024_back_to_home() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openSettings(home, menu);
        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong quay ve Home tu Settings");
        ExtentReportManager.getTest().log(Status.PASS, "Back tu Settings ve Home OK.");
    }
}