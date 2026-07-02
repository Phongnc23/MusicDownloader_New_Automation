package testcases.menu;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.MenuPage;
import report.ExtentReportManager;

/**
 * Module: Menu (Drawer)
 * Muc tieu: Kiem tra drawer mo va hien thi day du header + 9 item + Version.
 * Drawer mo tu Home (home.tapDrawerIcon). Moi test launch app moi nen tu mo drawer.
 */
public class Menu01_Verify_Drawer_Display extends BaseTest {

    /**
     * Mo drawer va doi den khi drawer mo DAY DU (header render xong, khong chi item "Exit app").
     * Tren cold start header render muon hon item -> phai cho header, neu khong test display fail.
     */
    private void openDrawer(HomePage home, MenuPage menu) {
        home.tapDrawerIcon();
        boolean open = home.waitUntil(
                () -> home.isDrawerOpen() && menu.isDrawerHeaderDisplayed(), 6000);
        Assert.assertTrue(open, "Khong mo duoc drawer (header chua hien)");
    }

    @Test(description = "TC_MENU_001: Drawer mo va hien thi day du")
    public void TC_MENU_001_drawer_display_full() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openDrawer(home, menu);
        boolean ok = menu.isDrawerHeaderDisplayed() && menu.areAllDrawerItemsDisplayed();
        Assert.assertTrue(ok, "Drawer thieu header hoac item");
        ExtentReportManager.getTest().log(Status.PASS, "Drawer mo, hien thi day du header + item.");
    }

    @Test(description = "TC_MENU_002: Hien thi header drawer (ten app + tagline)")
    public void TC_MENU_002_drawer_header() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openDrawer(home, menu);
        Assert.assertTrue(menu.isDrawerHeaderDisplayed(),
                "Khong thay 'Music Downloader' / 'Enjoy Listening'");
        ExtentReportManager.getTest().log(Status.PASS, "Header drawer hien dung ten app + tagline.");
    }

    @Test(description = "TC_MENU_003: Hien thi day du 9 menu item")
    public void TC_MENU_003_nine_items() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openDrawer(home, menu);
        ExtentReportManager.getTest().log(Status.INFO,
                "Equalizer/Downloaded/Sleep timer/Privacy policy/Rate us/Share app/Settings/Version/Exit app");
        Assert.assertTrue(menu.areAllDrawerItemsDisplayed(), "Thieu 1 hoac nhieu menu item");
        ExtentReportManager.getTest().log(Status.PASS, "Du 9 menu item.");
    }

    @Test(description = "TC_MENU_004: Hien thi Version voi so 9999")
    public void TC_MENU_004_version_9999() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openDrawer(home, menu);
        String version = menu.getDrawerVersionText();
        ExtentReportManager.getTest().log(Status.INFO, "Version doc duoc: " + version.replace("\n", " "));
        Assert.assertTrue(menu.isVersion9999Displayed(), "Version khong hien so 9999");
        ExtentReportManager.getTest().log(Status.PASS, "Version hien dung so 9999.");
    }
}