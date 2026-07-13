package testcases.home;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.TracksPage;
import report.ExtentReportManager;

/**
 * Module: Home
 * Muc tieu: Kiem tra hien thi cac thanh phan tren man Home.
 *
 * Ghi chu: TC_HOME_008 verify mini player HIEN khi co track da load (persist qua
 * noReset). Cac thao tac chi tiet voi mini player nam o class Home06.
 */
public class Home03_Verify_UI_Display extends BaseTest {

    @Test(description = "TC_HOME_003: Hien thi tieu de Home")
    public void TC_HOME_003_home_title() {
        HomePage home = new HomePage();
        Assert.assertTrue(home.isHomeTitleDisplayed(), "Tieu de 'Home' khong hien");
        ExtentReportManager.getTest().log(Status.PASS, "Tieu de Home hien dung.");
    }

    @Test(description = "TC_HOME_004: Hien thi icon hamburger menu goc trai")
    public void TC_HOME_004_drawer_icon() {
        HomePage home = new HomePage();
        Assert.assertTrue(home.isDrawerIconDisplayed(), "Icon menu (goc trai) khong hien");
        ExtentReportManager.getTest().log(Status.PASS, "Icon drawer hien dung.");
    }

    @Test(description = "TC_HOME_005: Hien thi icon tim kiem goc phai")
    public void TC_HOME_005_search_icon() {
        HomePage home = new HomePage();
        Assert.assertTrue(home.isSearchIconDisplayed(), "Icon search (goc phai) khong hien");
        ExtentReportManager.getTest().log(Status.PASS, "Icon search hien dung.");
    }

    @Test(description = "TC_HOME_006: Hien thi thanh search 'Search music online...'")
    public void TC_HOME_006_search_bar() {
        HomePage home = new HomePage();
        Assert.assertTrue(home.isSearchBarDisplayed(), "Thanh search khong hien dung placeholder");
        ExtentReportManager.getTest().log(Status.PASS, "Thanh search hien dung placeholder.");
    }

    @Test(description = "TC_HOME_007: Hien thi 4 nut Quick Action")
    public void TC_HOME_007_quick_actions() {
        HomePage home = new HomePage();
        ExtentReportManager.getTest().log(Status.INFO,
                "Kiem tra Downloaded / Sleep timer / Rate us / Settings");
        Assert.assertTrue(home.areAllQuickActionsDisplayed(),
                "Thieu 1 hoac nhieu nut Quick Action");
        ExtentReportManager.getTest().log(Status.PASS, "Du 4 nut Quick Action.");
    }

    @Test(description = "TC_HOME_008: Mini player hien khi co bai da load/dang phat")
    public void TC_HOME_008_mini_player_displayed() {
        HomePage home = new HomePage();
        // Cai moi / mo lan dau: nguoi dung CHUA phat bai nao -> chua co mini player.
        // Tu dam bao dieu kien: vao Tracks phat 1 bai roi ve Home moi verify.
        if (!home.isMiniPlayerDisplayed()) {
            ExtentReportManager.getTest().log(Status.INFO,
                    "Chua co mini player (chua phat bai nao) -> vao Tracks phat 1 bai roi ve Home");
            TracksPage tracks = new TracksPage();
            home.tapNavTracks();
            home.waitUntil(tracks::isTracksScreenDisplayed, 6000);
            Assert.assertTrue(tracks.isTracksScreenDisplayed(), "Khong vao duoc man Tracks de phat bai");
            tracks.tapPlayAll();
            home.waitUntil(home::isMiniPlayerDisplayed, 2500);
            home.tapNavHome();
            home.waitUntil(home::isHomeTitleDisplayed, 3000);
        } else {
            ExtentReportManager.getTest().log(Status.INFO,
                    "Da co track load (persist qua noReset) -> mini player phai hien");
        }
        Assert.assertTrue(home.isMiniPlayerDisplayed(),
                "Mini player khong hien du da phat 1 bai (can co track load).");
        ExtentReportManager.getTest().log(Status.PASS, "Mini player hien dung khi co track.");
    }

    @Test(description = "TC_HOME_009: Hien thi Bottom Navigation du 5 tab")
    public void TC_HOME_009_bottom_nav() {
        HomePage home = new HomePage();
        ExtentReportManager.getTest().log(Status.INFO,
                "Kiem tra 5 tab Home/Tracks/Artists/Albums/Playlists");
        Assert.assertTrue(home.isBottomNavDisplayed(), "Bottom nav khong du 5 tab");
        ExtentReportManager.getTest().log(Status.PASS, "Bottom nav du 5 tab.");
    }

    @Test(description = "TC_HOME_010: Hien thi tong the man Home dung bo cuc")
    public void TC_HOME_010_overall_layout() {
        HomePage home = new HomePage();
        boolean ok = home.isHomeTitleDisplayed()
                && home.isDrawerIconDisplayed()
                && home.isSearchIconDisplayed()
                && home.isSearchBarDisplayed()
                && home.areAllQuickActionsDisplayed()
                && home.isBottomNavDisplayed();
        Assert.assertTrue(ok, "Mot so thanh phan Home thieu/sai bo cuc");
        ExtentReportManager.getTest().log(Status.PASS, "Toan bo man Home hien day du.");
    }

    // ===== Case mo rong (grounded tu DOM) =====

    @Test(description = "TC_HOME_E01: Card Downloaded hien so luong track hop le")
    public void TC_HOME_E01_downloaded_count_valid() {
        HomePage home = new HomePage();
        int count = home.getDownloadedCount();
        ExtentReportManager.getTest().log(Status.INFO, "So track Downloaded doc duoc: " + count);
        Assert.assertTrue(count >= 0, "Khong doc duoc so luong track tren card Downloaded");
        ExtentReportManager.getTest().log(Status.PASS, "Card Downloaded hien so track = " + count);
    }

    @Test(description = "TC_HOME_E02: Card Sleep timer hien trang thai 'Set' khi chua hen gio")
    public void TC_HOME_E02_sleep_timer_state_idle() {
        HomePage home = new HomePage();
        String state = home.getSleepTimerState();
        ExtentReportManager.getTest().log(Status.INFO, "Trang thai Sleep timer: " + state);
        Assert.assertEquals(state, "Set", "Trang thai Sleep timer khi idle khong phai 'Set'");
        ExtentReportManager.getTest().log(Status.PASS, "Sleep timer hien 'Set' khi idle.");
    }
}