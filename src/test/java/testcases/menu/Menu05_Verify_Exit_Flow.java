package testcases.menu;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.MenuPage;
import report.ExtentReportManager;

/**
 * Module: Menu (Drawer) - Exit flow
 * Muc tieu: Kiem tra item Exit app trong drawer mo dialog xac nhan thoat.
 * Dialog thoat dung lai HomePage (giong man Home07).
 */
public class Menu05_Verify_Exit_Flow extends BaseTest {

    private void openDrawer(HomePage home) {
        home.tapDrawerIcon();
        home.sleep(1000);
        Assert.assertTrue(home.isDrawerOpen(), "Khong mo duoc drawer");
    }

    @Test(description = "TC_MENU_025: Click Exit app hien Exit confirmation dialog")
    public void TC_MENU_025_exit_shows_dialog() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openDrawer(home);
        menu.tapExitApp();
        home.sleep(1200);
        Assert.assertTrue(home.isExitDialogDisplayed(),
                "Khong hien dialog xac nhan thoat (title/Exit/Cancel)");
        ExtentReportManager.getTest().log(Status.PASS, "Exit app hien dialog xac nhan thoat.");

        // Don dep: Cancel de o lai app
        home.tapCancelOnDialog();
    }

    @Test(description = "TC_MENU_026: Click Cancel trong Exit dialog -> dialog dong, app van chay")
    public void TC_MENU_026_cancel_keeps_app() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openDrawer(home);
        menu.tapExitApp();
        home.sleep(1200);
        Assert.assertTrue(home.isExitDialogDisplayed(), "Dialog thoat khong hien de test Cancel");

        home.tapCancelOnDialog();
        home.sleep(800);
        Assert.assertFalse(home.isExitDialogDisplayed(), "Dialog khong dong sau Cancel");
        Assert.assertTrue(home.isAppLaunched(), "App da thoat (khong con o foreground)");
        Assert.assertTrue(home.isHomeDisplayed(), "Khong o lai Home sau Cancel");
        ExtentReportManager.getTest().log(Status.PASS, "Cancel dong dialog, app van chay OK.");
    }
}