package testcases.home;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Home
 * Muc tieu: Kiem tra Mini Player (thanh phat duoi cung). Mini player nam trong HomePage.
 *
 * Precondition: phai co 1 track da load/dang phat (persist qua noReset).
 * Neu khong co, test SKIP voi huong dan. Khi xong module Tracks se them
 * helper ensureSongPlaying() de bo phu thuoc nay.
 */
public class Home06_Verify_Mini_Player extends BaseTest {

    /** Tra ve HomePage, skip neu mini player chua hien. */
    private HomePage requireMiniPlayer() {
        HomePage home = new HomePage();
        if (!home.isMiniPlayerDisplayed()) {
            throw new SkipException(
                    "Khong co track trong mini player. Phat 1 bai truoc khi chay Home06.");
        }
        return home;
    }

    @Test(description = "TC_HOME_018: Mini player hien ten bai dang phat")
    public void TC_HOME_018_mini_player_shows_track() {
        HomePage home = requireMiniPlayer();
        String title = home.getMiniPlayerTrackTitle();
        ExtentReportManager.getTest().log(Status.INFO, "Ten bai mini player: " + title);
        Assert.assertFalse(title.trim().isEmpty(), "Mini player khong hien ten bai");
        ExtentReportManager.getTest().log(Status.PASS, "Mini player hien ten bai: " + title);
    }

    @Test(description = "TC_HOME_019: Play/Pause trong mini player dung/phat dung")
    public void TC_HOME_019_play_pause() {
        HomePage home = requireMiniPlayer();

        boolean advancing = home.isMiniPlayerProgressAdvancing(4000);
        if (!advancing) {
            ExtentReportManager.getTest().log(Status.INFO, "Dang khong phat -> tap de phat");
            home.tapMiniPlayerPlayPause();
            advancing = home.isMiniPlayerProgressAdvancing(4000);
        }
        Assert.assertTrue(advancing, "Khong phat duoc (progress khong tang khi play)");
        ExtentReportManager.getTest().log(Status.INFO, "Da xac nhan dang phat (progress tang)");

        home.tapMiniPlayerPlayPause();
        boolean stillAdvancing = home.isMiniPlayerProgressAdvancing(4000);
        Assert.assertFalse(stillAdvancing, "Pause khong dung (progress van tang)");
        ExtentReportManager.getTest().log(Status.PASS, "Play/Pause hoat dong dung.");

        home.tapMiniPlayerPlayPause(); // khoi phuc: phat lai
    }

    @Test(description = "TC_HOME_020: Tap nut queue mo man Playing Queue")
    public void TC_HOME_020_open_queue() {
        HomePage home = requireMiniPlayer();
        home.tapMiniPlayerQueue();
        home.sleep(1500);
        Assert.assertFalse(home.isHomeDisplayed(), "Khong roi Home -> chua mo Playing Queue");
        ExtentReportManager.getTest().log(Status.PASS,
                "Mo man Playing Queue OK (chi tiet queue se verify o module Player).");

        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home");
    }

    @Test(description = "TC_HOME_021: Tap mini player mo full player (Play Now)")
    public void TC_HOME_021_open_full_player() {
        HomePage home = requireMiniPlayer();
        home.tapMiniPlayer();
        home.sleep(1500);
        // App Flutter GIU node Home trong a11y tree phia sau full player -> isHomeDisplayed
        // van true. Phai verify bang ORACLE DUONG: full player co node "Playing now".
        Assert.assertTrue(home.isFullPlayerDisplayed(),
                "Tap mini player khong mo full player (khong thay 'Playing now')");
        ExtentReportManager.getTest().log(Status.PASS,
                "Mo full player OK (chi tiet full player se verify o module Player).");

        // Don dep: dong full player ve Home
        home.pressBack();
        home.sleep(1000);
        Assert.assertFalse(home.isFullPlayerDisplayed(), "Khong dong duoc full player");
    }
}