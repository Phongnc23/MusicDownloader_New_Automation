package testcases.home;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import constants.AppConstants;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Home
 * Muc tieu: Kiem tra dialog xac nhan thoat khi nhan BACK o Home.
 *
 * DOM dialog (bottom sheet): title "Are you sure you want to exit?",
 * nut "Exit" (trai) va "Cancel" (phai). Ban build khong co ad trong dialog.
 *
 * LUU Y vong doi: moi test co @BeforeMethod (launch app) + @AfterMethod (quit driver)
 * rieng -> TC_HOME_030 (thoat app) khong anh huong cac test khac (deu isolate).
 */
public class Home07_Verify_Exit_Dialog extends BaseTest {

    @Test(description = "TC_HOME_028: Nhan BACK o Home bung dialog xac nhan thoat")
    public void TC_HOME_028_back_shows_exit_dialog() {
        HomePage home = new HomePage();

        Assert.assertTrue(home.isHomeDisplayed(), "Truoc dieu kien: chua o Home");
        home.openExitDialogViaBack();

        Assert.assertTrue(home.isExitDialogDisplayed(),
                "Khong hien dialog xac nhan thoat (title/Exit/Cancel)");
        ExtentReportManager.getTest().log(Status.PASS, "Dialog xac nhan thoat hien dung.");

        // Don dep: Cancel de o lai Home (khong de session ket o dialog)
        home.tapCancelOnDialog();
    }

    @Test(description = "TC_HOME_029: Bam Cancel tren exit dialog o lai Home")
    public void TC_HOME_029_cancel_stays_home() {
        HomePage home = new HomePage();

        home.openExitDialogViaBack();
        Assert.assertTrue(home.isExitDialogDisplayed(), "Dialog thoat khong hien de test Cancel");

        home.tapCancelOnDialog();
        home.sleep(800);

        Assert.assertFalse(home.isExitDialogDisplayed(), "Dialog khong dong sau Cancel");
        Assert.assertTrue(home.isHomeDisplayed(), "Khong o lai Home sau Cancel");
        ExtentReportManager.getTest().log(Status.PASS, "Cancel dong dialog, o lai Home OK.");
    }

    @Test(description = "TC_HOME_030: Bam Exit tren exit dialog thoat app (DESTRUCTIVE)")
    public void TC_HOME_030_exit_closes_app() {
        HomePage home = new HomePage();

        home.openExitDialogViaBack();
        Assert.assertTrue(home.isExitDialogDisplayed(), "Dialog thoat khong hien de test Exit");

        home.tapExitOnDialog();
        home.sleep(2000); // cho app dong han

        String pkg = getDriver().getCurrentPackage();
        ExtentReportManager.getTest().log(Status.INFO, "Package sau khi Exit: " + pkg);
        Assert.assertNotEquals(pkg, AppConstants.APP_PACKAGE,
                "App khong thoat (van o foreground)");
        ExtentReportManager.getTest().log(Status.PASS, "Exit dong app thanh cong (DESTRUCTIVE).");
    }
}