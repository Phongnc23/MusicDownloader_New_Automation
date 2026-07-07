package testcases.menu;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.MenuPage;
import report.ExtentReportManager;
import utils.ScreenshotUtils;

/**
 * Module: Menu (Drawer) - Theme toggle (Light | Dark | System).
 * Theme toggle o DAU drawer, 3 icon (trai->phai: Light, Dark, System) KHONG co content-desc
 * -> tap theo TOA DO. UI khong expose theme qua a11y -> VERIFY bang DO SANG NEN Home
 * (light = nen sang, dark = nen toi).
 * Theme KHONG anh huong locator (content-desc giu nguyen) nen khong bat buoc revert; van tra ve
 * Dark (mac dinh) sau moi test cho anh minh chung + module sau nhat quan.
 */
public class Menu06_Verify_Theme_Toggle extends BaseTest {

    // Vung nen TRONG tren Home (duoi 4 card, tren mini player) de do sang theme.
    private static final int BG_X = 860, BG_Y = 1600, BG_HALF = 90;
    private static final int BRIGHT_MIN = 140;  // light: nen sang
    private static final int DARK_MAX   = 90;   // dark: nen toi

    private void openDrawer(HomePage home, MenuPage menu) {
        home.tapDrawerIcon();
        boolean open = home.waitUntil(() -> home.isDrawerOpen() && menu.isDrawerHeaderDisplayed(), 6000);
        Assert.assertTrue(open, "Khong mo duoc drawer");
    }

    /** Mo drawer -> chon theme (0=Light,1=Dark,2=System) -> dong drawer -> tra ve do sang nen Home. */
    private int applyThemeAndReadBg(HomePage home, MenuPage menu, int which) {
        openDrawer(home, menu);
        if (which == 0) menu.tapThemeLight();
        else if (which == 1) menu.tapThemeDark();
        else menu.tapThemeSystem();
        home.sleep(1200);
        home.closeMenuDrawer();
        home.sleep(1200);
        Assert.assertTrue(home.isHomeDisplayed(), "Sau doi theme khong ve Home");
        return ScreenshotUtils.regionBrightness(BG_X, BG_Y, BG_HALF);
    }

    @Test(description = "TC_MENU_031: Doi theme LIGHT - nen Home sang")
    public void TC_MENU_031_theme_light() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        int bg = applyThemeAndReadBg(home, menu, 0);
        ExtentReportManager.getTest().log(Status.INFO, "Do sang nen (Light) = " + bg);
        Assert.assertTrue(bg >= BRIGHT_MIN, "Theme Light nhung nen khong sang (do sang=" + bg + ")");
        ExtentReportManager.getTest().log(Status.PASS, "Theme Light OK (nen sang).");

        applyThemeAndReadBg(home, menu, 1);   // tra ve Dark (mac dinh)
    }

    @Test(description = "TC_MENU_032: Doi theme DARK - nen Home toi")
    public void TC_MENU_032_theme_dark() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        int bg = applyThemeAndReadBg(home, menu, 1);
        ExtentReportManager.getTest().log(Status.INFO, "Do sang nen (Dark) = " + bg);
        Assert.assertTrue(bg >= 0 && bg <= DARK_MAX, "Theme Dark nhung nen khong toi (do sang=" + bg + ")");
        ExtentReportManager.getTest().log(Status.PASS, "Theme Dark OK (nen toi).");
    }

    @Test(description = "TC_MENU_033: Doi theme SYSTEM - ap dung theme he thong")
    public void TC_MENU_033_theme_system() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        int bg = applyThemeAndReadBg(home, menu, 2);
        ExtentReportManager.getTest().log(Status.INFO, "Do sang nen (System) = " + bg);
        Assert.assertTrue(bg >= 0, "Khong doc duoc do sang sau khi chon System");
        Assert.assertTrue(bg <= DARK_MAX || bg >= BRIGHT_MIN,
                "Theme System cho nen khong ro light/dark (do sang=" + bg + ")");
        ExtentReportManager.getTest().log(Status.PASS,
                "Theme System OK (ap dung theme he thong, do sang=" + bg + ").");

        applyThemeAndReadBg(home, menu, 1);   // tra ve Dark (mac dinh)
    }
}
