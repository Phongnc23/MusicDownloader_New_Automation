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
 * Muc tieu: App launch thanh cong va vao thang man Home.
 * (Ban build khong co quang cao -> khong can xu ly interstitial.)
 */
public class Home01_Verify_App_Launched extends BaseTest {

    @Test(description = "TC_HOME_001: App launch thanh cong vao Home")
    public void TC_HOME_001_app_launched() {
        HomePage home = new HomePage();

        ExtentReportManager.getTest().log(Status.INFO,
                "Verify package = " + AppConstants.APP_PACKAGE);
        Assert.assertTrue(home.isAppLaunched(),
                "App khong chay dung package: " + AppConstants.APP_PACKAGE);

        ExtentReportManager.getTest().log(Status.INFO, "Verify dang o man Home");
        Assert.assertTrue(home.isHomeDisplayed(), "App khong vao duoc man Home");

        ExtentReportManager.getTest().log(Status.PASS, "App launch thanh cong, dang o Home.");
    }
}