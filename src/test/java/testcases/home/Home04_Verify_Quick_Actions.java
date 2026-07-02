package testcases.home;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Home
 * Muc tieu: Kiem tra cac Quick Action tren Home dieu huong dung.
 *
 * Cach verify dieu huong (Flutter single-Activity):
 *  - Card mo MAN HINH FULL (Search / Downloaded / Settings / Search In Library):
 *    sau khi tap -> Home bien mat -> press BACK -> Home tro lai.
 *  - Drawer: dung closeMenuDrawer() (KHONG dung BACK - BACK bung exit dialog).
 *  - Sleep timer / Rate us: verify mo + thu hoi duoc; assertion noi dung dialog o module Menu.
 */
public class Home04_Verify_Quick_Actions extends BaseTest {

    @Test(description = "TC_HOME_011: Tap thanh search mo man Search Online")
    public void TC_HOME_011_open_search_online() {
        HomePage home = new HomePage();
        home.tapSearchBar();
        home.sleep(1500);
        Assert.assertFalse(home.isHomeDisplayed(), "Khong roi Home -> chua mo Search Online");
        ExtentReportManager.getTest().log(Status.PASS, "Mo man Search Online OK.");

        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong quay lai duoc Home");
    }

    @Test(description = "TC_HOME_012: Tap icon search goc phai mo Search In Library")
    public void TC_HOME_012_open_search_in_library() {
        HomePage home = new HomePage();
        home.tapSearchIcon();
        home.sleep(1500);
        Assert.assertFalse(home.isHomeDisplayed(), "Khong roi Home -> chua mo Search In Library");
        ExtentReportManager.getTest().log(Status.PASS, "Mo man Search In Library OK.");

        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong quay lai duoc Home");
    }

    @Test(description = "TC_HOME_013: Tap nut Downloaded mo man Downloaded")
    public void TC_HOME_013_open_downloaded() {
        HomePage home = new HomePage();
        home.tapDownloaded();
        home.sleep(1500);
        Assert.assertFalse(home.isHomeDisplayed(), "Khong roi Home -> chua mo Downloaded");
        ExtentReportManager.getTest().log(Status.PASS, "Mo man Downloaded OK.");

        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong quay lai duoc Home");
    }

    @Test(description = "TC_HOME_014: Tap Sleep timer mo dialog (mo + thu hoi duoc)")
    public void TC_HOME_014_open_sleep_timer() {
        HomePage home = new HomePage();
        home.tapSleepTimer();
        home.sleep(1200);
        ExtentReportManager.getTest().log(Status.INFO,
                "Da tap Sleep timer (assertion noi dung dialog se bo sung o module Menu)");

        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(),
                "Sau khi dong Sleep timer khong ve duoc Home / app loi");
        ExtentReportManager.getTest().log(Status.PASS, "Mo/dong Sleep timer khong crash, ve Home OK.");
    }

    @Test(description = "TC_HOME_015: Tap Rate us mo dialog danh gia / Play Store")
    public void TC_HOME_015_open_rate_us() {
        HomePage home = new HomePage();
        home.tapRateUs();
        home.sleep(1500);

        String pkg = getDriver().getCurrentPackage();
        ExtentReportManager.getTest().log(Status.INFO, "Package hien tai sau tap Rate us: " + pkg);
        // Rate us co the mo dialog trong app HOAC bat Play Store (package khac)
        boolean external = pkg != null && !pkg.equals(constants.AppConstants.APP_PACKAGE);
        if (external) {
            ExtentReportManager.getTest().log(Status.INFO, "Da bat ung dung ngoai (Play Store) - hop le");
            getDriver().activateApp(constants.AppConstants.APP_PACKAGE);
            home.sleep(1000);
        }
        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home sau Rate us");
        ExtentReportManager.getTest().log(Status.PASS, "Rate us hoat dong, ve Home OK.");
    }

    @Test(description = "TC_HOME_016: Tap nut Settings mo man Settings")
    public void TC_HOME_016_open_settings() {
        HomePage home = new HomePage();
        home.tapSettings();
        home.sleep(1500);
        Assert.assertFalse(home.isHomeDisplayed(), "Khong roi Home -> chua mo Settings");
        ExtentReportManager.getTest().log(Status.PASS, "Mo man Settings OK.");

        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong quay lai duoc Home");
    }

    @Test(description = "TC_HOME_017: Tap hamburger mo drawer va dong lai dung")
    public void TC_HOME_017_open_close_drawer() {
        HomePage home = new HomePage();
        home.tapDrawerIcon();
        home.sleep(1200);
        Assert.assertTrue(home.isDrawerOpen(),
                "Drawer khong mo (khong thay item 'Exit app')");
        ExtentReportManager.getTest().log(Status.PASS, "Drawer mo dung.");

        // LUU Y: KHONG dung BACK de dong drawer (BACK o drawer bung exit dialog).
        home.closeMenuDrawer();
        Assert.assertFalse(home.isDrawerOpen(), "Drawer khong dong");
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve Home sau khi dong drawer");
        ExtentReportManager.getTest().log(Status.PASS, "Drawer dong (scrim/swipe), ve Home OK.");
    }
}