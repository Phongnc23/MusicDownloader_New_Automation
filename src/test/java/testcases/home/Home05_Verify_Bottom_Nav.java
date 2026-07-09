package testcases.home;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Home
 * Muc tieu: Kiem tra dieu huong qua Bottom Navigation (5 tab) + quay ve Home.
 *
 * Ghi chu: Cac case Mini Player (TC_HOME_018..021) o class Home06.
 * Cach verify: tap tab khac -> Home bien mat -> tap tab Home -> Home tro lai.
 */
public class Home05_Verify_Bottom_Nav extends BaseTest {

    @Test(description = "TC_HOME_022: Tap tab Tracks chuyen sang man Tracks")
    public void TC_HOME_022_nav_tracks() {
        HomePage home = new HomePage();
        home.tapNavTracks();
        home.sleep(1200);
        Assert.assertFalse(home.isHomeDisplayed(), "Khong chuyen sang Tracks");
        ExtentReportManager.attachProof("Da chuyen sang tab Tracks - minh chung");

        home.tapNavHome();
        home.sleep(1200);
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home");
    }

    @Test(description = "TC_HOME_023: Tap tab Artists chuyen sang man Artists")
    public void TC_HOME_023_nav_artists() {
        HomePage home = new HomePage();
        home.tapNavArtists();
        home.sleep(1200);
        Assert.assertFalse(home.isHomeDisplayed(), "Khong chuyen sang Artists");
        ExtentReportManager.attachProof("Da chuyen sang tab Artists - minh chung");

        home.tapNavHome();
        home.sleep(1200);
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home");
    }

    @Test(description = "TC_HOME_024: Tap tab Albums chuyen sang man Albums")
    public void TC_HOME_024_nav_albums() {
        HomePage home = new HomePage();
        home.tapNavAlbums();
        home.sleep(1200);
        Assert.assertFalse(home.isHomeDisplayed(), "Khong chuyen sang Albums");
        ExtentReportManager.attachProof("Da chuyen sang tab Albums - minh chung");

        home.tapNavHome();
        home.sleep(1200);
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home");
    }

    @Test(description = "TC_HOME_025: Tap tab Playlists chuyen sang man Playlists")
    public void TC_HOME_025_nav_playlists() {
        HomePage home = new HomePage();
        home.tapNavPlaylists();
        home.sleep(1200);
        Assert.assertFalse(home.isHomeDisplayed(), "Khong chuyen sang Playlists");
        ExtentReportManager.attachProof("Da chuyen sang tab Playlists - minh chung");

        home.tapNavHome();
        home.sleep(1200);
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home");
    }

    @Test(description = "TC_HOME_026: Tap tab Home khi dang o Home van giu nguyen")
    public void TC_HOME_026_nav_home_when_on_home() {
        HomePage home = new HomePage();
        Assert.assertTrue(home.isHomeDisplayed(), "Truoc dieu kien: chua o Home");
        home.tapNavHome();
        home.sleep(1000);
        Assert.assertTrue(home.isHomeDisplayed(), "Tap Home khi dang o Home gay loi/roi man");
        ExtentReportManager.getTest().log(Status.PASS, "Tap Home tren Home van giu nguyen, khong loi.");
    }

    @Test(description = "TC_HOME_027: Dieu huong qua nhieu tab roi quay ve Home")
    public void TC_HOME_027_round_trip_back_home() {
        HomePage home = new HomePage();
        home.tapNavTracks();  home.sleep(1000);
        home.tapNavArtists(); home.sleep(1000);
        home.tapNavAlbums();  home.sleep(1000);
        Assert.assertFalse(home.isHomeDisplayed(), "Dang o tab khac ma van thay Home");
        ExtentReportManager.attachProof("Da duyet qua nhieu tab (dang o Albums) - minh chung");

        home.tapNavHome();
        home.sleep(1200);
        Assert.assertTrue(home.isHomeDisplayed(), "Khong quay ve duoc Home sau khi duyet nhieu tab");
        ExtentReportManager.getTest().log(Status.PASS, "Duyet nhieu tab roi ve Home OK.");
    }
}