package testcases.home;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.TracksPage;
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

    /**
     * Tra ve HomePage co mini player. KHONG con phu thuoc thu tu test:
     * neu mini player chua hien -> tu phat 1 bai tu Tracks roi ve Home. Poll de tranh flaky
     * (mini player render tre sau activateApp -> truoc day check 1 lan -> fail lan dau roi retry moi pass).
     */
    private HomePage requireMiniPlayer() {
        HomePage home = new HomePage();
        // Mini player co the render tre -> poll vai giay truoc khi ket luan la chua co.
        if (home.waitUntil(home::isMiniPlayerDisplayed, 4000)) return home;
        // Van chua co (chua bai nao phat trong suite) -> phat 1 bai tu Tracks roi quay ve Home.
        TracksPage tracks = new TracksPage();
        home.tapNavTracks();
        home.waitUntil(tracks::isTracksScreenDisplayed, 6000);
        if (tracks.isTracksScreenDisplayed()) {
            tracks.playTrack(0);
            home.sleep(1800);
            home.tapNavHome();
        }
        if (home.waitUntil(home::isMiniPlayerDisplayed, 6000)) return home;
        throw new SkipException(
                "Khong the phat bai de hien mini player (thu vien rong?).");
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
        HomePage home = new HomePage();
        TracksPage tracks = new TracksPage();
        // ROBUST: phat 1 bai DAI MOI tu dau (khong nhan mini player cua bai cu da ket o cuoi = 101%
        // -> tap Play khong lam progress tang -> fail oan). Bai dai khong tu auto-next -> on dinh.
        tracks.startFreshLongSongToHome(home);
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Khong co mini player sau khi phat bai dai");

        // Cua so 6s (thay 4s): bien an toan cho bai ~5 phut (1% ~3s) -> chac chan bat duoc % tang.
        boolean advancing = home.isMiniPlayerProgressAdvancing(6000);
        if (!advancing) {
            ExtentReportManager.getTest().log(Status.INFO, "Dang khong phat -> tap de phat");
            home.tapMiniPlayerPlayPause();
            advancing = home.isMiniPlayerProgressAdvancing(6000);
        }
        Assert.assertTrue(advancing, "Khong phat duoc (progress khong tang khi play)");
        ExtentReportManager.getTest().log(Status.INFO, "Da xac nhan dang phat (progress tang)");

        home.tapMiniPlayerPlayPause();
        boolean stillAdvancing = home.isMiniPlayerProgressAdvancing(6000);
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
        // MINH CHUNG: chup man Playing Queue NGAY luc nay, truoc khi back ve Home
        ExtentReportManager.attachProof("Da mo man Playing Queue - minh chung");

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
        // MINH CHUNG: chup full player NGAY luc nay, truoc khi dong ve Home
        ExtentReportManager.attachProof("Da mo full player (Play Now) - minh chung");

        // Don dep: dong full player ve Home
        home.pressBack();
        home.sleep(1000);
        Assert.assertFalse(home.isFullPlayerDisplayed(), "Khong dong duoc full player");
    }
}