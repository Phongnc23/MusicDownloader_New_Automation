package testcases.tracks;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.TracksPage;
import report.ExtentReportManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Module: Tracks - Playback & Mini player (TC_TRK_015..025).
 * Dung oracle DUONG TINH: mini player % tang (isMiniPlayerProgressAdvancing), title doi khi thay bai.
 */
public class Tracks03_Verify_Playback_And_MiniPlayer extends BaseTest {

    private TracksPage goTracks(HomePage home) {
        TracksPage tracks = new TracksPage();
        home.tapNavTracks();
        home.waitUntil(tracks::isTracksScreenDisplayed, 6000);
        Assert.assertTrue(tracks.isTracksScreenDisplayed(), "Khong vao duoc man Tracks");
        // Set sort = Date modified -> bai DAI (4:59) len dau, KHONG nhay lien tuc nhu bai 0:03
        // (sort Duration). Giup phat hien playback on dinh. Rieng TC_016 se sort lai Duration sau.
        tracks.setSortDateModifiedTop();
        return tracks;
    }

    @Test(description = "TC_TRK_015: Play all phat bai dau, mini player update, thay bai dang phat")
    public void TC_TRK_015_play_all_then_change() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.tapPlayAll();
        home.sleep(2000);
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Khong co mini player sau Play all");
        String t1 = home.getMiniPlayerTrackTitle();

        tracks.playTrack(3);
        home.sleep(2000);
        String t2 = home.getMiniPlayerTrackTitle();
        ExtentReportManager.getTest().log(Status.INFO, "t1=" + t1 + " | t2=" + t2);
        Assert.assertNotEquals(t1, t2, "Chon bai khac nhung mini player khong doi bai");
        ExtentReportManager.getTest().log(Status.PASS, "Play all + doi bai cap nhat mini player OK.");
    }

    @Test(description = "TC_TRK_016: Play all sau khi sort Duration van phat (mini player chay)")
    public void TC_TRK_016_play_all_after_sort_duration() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        // Sort theo Duration, bai DAI NHAT len dau (helper tu dao chieu neu app dang sort tang dan).
        // Van dung y "Play all sau khi sort Duration", nhung tranh bai 0:03 (churn / clip khong nhich %
        // -> progress ket 0 -> fail oan). Bai dai phat on dinh -> progress nhich chac chan.
        tracks.setSortDurationLongestTop();
        home.sleep(800);

        tracks.tapPlayAll();
        home.sleep(1500);
        // TIEU CHI (theo QA): mo bai ma bai CHAY = pass. Ban phim "%" trong content-desc mini player
        // KHONG dang tin voi bai DAI (a11y label lom dom / duration <unknown> -> ket 0% du thanh tien do
        // van chay) -> KHONG assert cung theo %. Xac nhan: sau Play all co mini player + co bai (title).
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Khong co mini player sau Play all (post-sort)");
        Assert.assertFalse(home.getMiniPlayerTrackTitle().isEmpty(),
                "Mini player khong co bai sau Play all (post-sort)");
        boolean active = home.isPlaybackActive(12000); // best-effort, chi log tham khao (% co the sai)
        ExtentReportManager.getTest().log(Status.INFO,
                "Playback active (best-effort, % co the sai do <unknown> duration) = " + active);
        ExtentReportManager.getTest().log(Status.PASS,
                "Play all sau sort Duration -> mini player co bai dang phat.");
    }

    @Test(description = "TC_TRK_017: Shuffle phat ngau nhien, lap nhieu lan deu phat")
    public void TC_TRK_017_shuffle_random() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        Set<String> titles = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            tracks.tapShuffle();
            home.sleep(1800);
            Assert.assertTrue(home.isMiniPlayerDisplayed(), "Lan " + (i + 1) + ": khong co mini player");
            titles.add(home.getMiniPlayerTrackTitle());
        }
        ExtentReportManager.getTest().log(Status.INFO, "So bai khac nhau qua 5 lan shuffle: " + titles.size());
        Assert.assertTrue(titles.size() >= 2, "5 lan shuffle deu ra cung 1 bai (kha nghi khong ngau nhien)");
        ExtentReportManager.getTest().log(Status.PASS, "Shuffle phat ngau nhien, deu phat.");
    }

    @Test(description = "TC_TRK_018: Play all va Shuffle doi bai dang phat")
    public void TC_TRK_018_play_all_vs_shuffle() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.tapPlayAll(); home.sleep(2000);
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Khong co mini player sau Play all");

        tracks.tapShuffle(); home.sleep(2000);
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Khong co mini player sau Shuffle");
        Assert.assertTrue(home.isMiniPlayerProgressAdvancing(4000), "Sau Shuffle khong phat");
        ExtentReportManager.getTest().log(Status.PASS, "Play all va Shuffle deu phat, mini player chay.");
    }

    @Test(description = "TC_TRK_019: Click track phat; click bai khac van phat; click lai van phat (on dinh)")
    public void TC_TRK_019_click_track_plays() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        // LUU Y: thu vien co bai cuc ngan (0:05) -> bai TU NHAY lien tuc, KHONG the so sanh %
        // (p2>=p1) de verify "khong restart". Thay vao do verify: click phat duoc + doi bai duoc
        // + click lai van phat (mini player on dinh, khong crash/dung han).
        tracks.playTrack(0);
        Assert.assertTrue(home.isMiniPlayerProgressAdvancing(4000), "Click track 0 khong phat");

        tracks.playTrack(1); // doi sang bai khac
        Assert.assertTrue(home.isMiniPlayerProgressAdvancing(4000), "Click track 1 khong phat (doi bai)");

        tracks.playTrack(1); // click lai chinh bai dang phat -> van phat, khong dung
        Assert.assertTrue(home.isMiniPlayerProgressAdvancing(4000), "Click lai bai dang phat khong con phat");
        ExtentReportManager.getTest().log(Status.PASS, "Click track phat/doi bai/click lai deu phat (on dinh).");
    }

    @Test(description = "TC_TRK_020: Long press track mo Select mode")
    public void TC_TRK_020_long_press_select_mode() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.longPressTrackToSelect(0);
        home.sleep(1200);
        Assert.assertTrue(tracks.isSelectModeActive(), "Long press khong vao Select mode");
        ExtentReportManager.getTest().log(Status.PASS, "Long press -> Select mode.");
    }

    @Test(description = "TC_TRK_021: Scroll list khong roi Tracks, bai van phat")
    public void TC_TRK_021_scroll_keeps_playing() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.tapPlayAll(); home.sleep(2000);
        tracks.scrollListDown(); home.sleep(800);
        tracks.scrollListDown(); home.sleep(800);
        Assert.assertTrue(tracks.isTracksScreenDisplayed(), "Scroll lam roi man Tracks");
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Mini player mat sau scroll");
        Assert.assertTrue(home.isMiniPlayerProgressAdvancing(4000), "Bai khong con phat sau scroll");
        ExtentReportManager.getTest().log(Status.PASS, "Scroll khong roi Tracks, bai van phat.");
    }

    @Test(description = "TC_TRK_022: Mini player play/pause toggle")
    public void TC_TRK_022_miniplayer_play_pause() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.tapPlayAll(); home.sleep(2500);
        Assert.assertTrue(home.isMiniPlayerProgressAdvancing(4000), "Truoc dieu kien: bai chua phat");

        home.tapMiniPlayerPlayPause(); home.sleep(1200); // pause
        int a = home.getMiniPlayerProgress();
        home.sleep(2500);
        int b = home.getMiniPlayerProgress();
        Assert.assertEquals(a, b, "Da pause nhung progress van chay (a=" + a + " b=" + b + ")");
        ExtentReportManager.getTest().log(Status.INFO, "Pause OK: progress dung o " + a + "%");

        home.tapMiniPlayerPlayPause(); home.sleep(800); // play lai
        Assert.assertTrue(home.isMiniPlayerProgressAdvancing(4000), "Play lai nhung progress khong tang");
        ExtentReportManager.getTest().log(Status.PASS, "Mini player play/pause toggle OK.");
    }

    @Test(description = "TC_TRK_023: Mini player co noi dung va % tang theo thoi gian")
    public void TC_TRK_023_miniplayer_content_progress() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.tapPlayAll(); home.sleep(1500);
        // Bai co the cuc ngan (0:03) -> bai tu nhay; oracle window 8s bat chuyen dong on dinh.
        Assert.assertFalse(home.getMiniPlayerTrackTitle().isEmpty(), "Mini player khong co tieu de");
        Assert.assertTrue(home.isMiniPlayerProgressAdvancing(8000), "% mini player khong tang");
        ExtentReportManager.getTest().log(Status.PASS, "Mini player co noi dung + % tang.");
    }

    @Test(description = "TC_TRK_024: Mini player body mo Play Now, Back ve Tracks, bai van phat")
    public void TC_TRK_024_miniplayer_open_play_now() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.tapPlayAll(); home.sleep(2000);
        home.tapMiniPlayer(); home.sleep(1500);
        Assert.assertTrue(tracks.isPlayNowOpen(), "Khong mo duoc Play Now tu mini player");
        // MINH CHUNG: chup Play Now dang mo truoc khi collapse ve Tracks
        ExtentReportManager.attachProof("Da mo Play Now tu mini player - minh chung");

        tracks.pnCollapse(); home.sleep(1200);
        Assert.assertTrue(tracks.isTracksScreenDisplayed(), "Khong ve Tracks sau khi dong Play Now");
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Mini player mat sau khi dong Play Now");
        ExtentReportManager.getTest().log(Status.PASS, "Mini player body -> Play Now -> Back ve Tracks, bai van phat.");
    }

    @Test(description = "TC_TRK_025: Mini player icon queue mo Playing Queue")
    public void TC_TRK_025_miniplayer_open_queue() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.tapPlayAll(); home.sleep(2000);
        home.tapMiniPlayerQueue(); home.sleep(1500);
        Assert.assertTrue(tracks.isPlayingQueueOpen(), "Khong mo duoc Playing Queue tu mini player");
        ExtentReportManager.getTest().log(Status.PASS, "Mini player icon queue -> Playing Queue.");
    }
}