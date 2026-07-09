package testcases.search;

import base.SearchOnlineBaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.SearchOnlinePage;
import report.ExtentReportManager;

/**
 * Module: Search Online (Download)
 * Muc tieu: Dieu huong vao/ra man Search Online.
 *  - Thanh search "Search music online..." -> man Search Online (go tu khoa).
 *  - Icon search goc phai -> man Search (trong app moi la Search In Library; chi tiet o module TC_SL).
 *  - BACK tu Search: an ban phim roi ve Home.
 *
 * Extends SearchOnlineBaseTest -> tu ep app ve Home truoc moi test (Search Online chi vao tu Home).
 */
public class Search01_Verify_Navigation extends SearchOnlineBaseTest {

    @Test(description = "TC_DL_001: Click thanh search chuyen sang man Search Online")
    public void TC_DL_001_open_search_online() {
        HomePage home = new HomePage();
        SearchOnlinePage search = new SearchOnlinePage();

        home.tapSearchBar();
        home.waitUntil(search::isSearchScreenDisplayed, 5000);
        Assert.assertTrue(search.isSearchScreenDisplayed(), "Khong mo duoc man Search Online");
        // MINH CHUNG: chup man Search Online NGAY luc nay, truoc khi back ve Home
        ExtentReportManager.attachProof("Da mo man Search Online - minh chung");

        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home");
    }

    @Test(description = "TC_DL_002: Vao Search ban phim tu bat de nhap")
    public void TC_DL_002_keyboard_auto_show() {
        HomePage home = new HomePage();
        SearchOnlinePage search = new SearchOnlinePage();

        home.tapSearchBar();
        home.waitUntil(search::isKeyboardShown, 5000);
        Assert.assertTrue(search.isKeyboardShown(), "Ban phim khong tu bat khi vao Search");
        // MINH CHUNG: chup man Search (ban phim dang bat) NGAY luc nay, truoc khi back
        ExtentReportManager.attachProof("Da mo Search, ban phim tu bat - minh chung");

        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home");
    }

    @Test(description = "TC_DL_003: Click icon search goc phai chuyen sang man Search")
    public void TC_DL_003_open_search_from_icon() {
        HomePage home = new HomePage();

        home.tapSearchIcon();
        home.waitUntil(() -> !home.isHomeDisplayed(), 5000);
        // App moi: icon goc phai mo Search In Library (man search khac) -> roi Home la dat.
        Assert.assertFalse(home.isHomeDisplayed(), "Khong roi Home -> chua mo man Search tu icon");
        // MINH CHUNG: chup man Search mo tu icon goc phai (da roi Home), truoc khi back
        ExtentReportManager.attachProof("Da mo man Search tu icon goc phai (roi Home) - minh chung");

        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home");
    }

    @Test(description = "TC_DL_004: Nhan BACK tu Search an ban phim roi ve Home")
    public void TC_DL_004_back_hide_keyboard_then_home() {
        HomePage home = new HomePage();
        SearchOnlinePage search = new SearchOnlinePage();

        home.tapSearchBar();
        home.waitUntil(search::isKeyboardShown, 5000);
        Assert.assertTrue(search.isKeyboardShown(), "Truoc dieu kien: ban phim chua bat");

        // BACK lan 1: an ban phim
        home.pressBack();
        home.sleep(900);
        Assert.assertFalse(search.isKeyboardShown(), "BACK khong an duoc ban phim");
        ExtentReportManager.getTest().log(Status.INFO, "BACK lan 1 da an ban phim.");

        // Tiep tuc ve Home
        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home sau BACK");
        ExtentReportManager.getTest().log(Status.PASS, "An ban phim roi ve Home OK.");
    }
}