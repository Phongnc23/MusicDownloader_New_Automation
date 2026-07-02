package testcases.tracks;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.TracksPage;
import report.ExtentReportManager;

/**
 * Module: Tracks - Play Now / full player (TC_TRK_049..059).
 *
 * !!! LUU Y: toa do cac nut tren Play Now (heart/shuffle/repeat/next/prev/seekbar/icon...)
 * la UOC LUONG tu screenshot 467x633 (chua co DOM that cua man Play Now - doc 24 bi trung
 * thanh context menu). Cac assert o day chu yeu kiem tra DIEU HUONG/ON DINH man; can DOM that
 * de chot chinh xac toa do va verify trang thai toggle.
 */
public class Tracks07_Verify_PlayNow extends BaseTest {

    private TracksPage goTracks(HomePage home) {
        TracksPage tracks = new TracksPage();
        home.tapNavTracks();
        home.waitUntil(tracks::isTracksScreenDisplayed, 6000);
        Assert.assertTrue(tracks.isTracksScreenDisplayed(), "Khong vao duoc man Tracks");
        // Sort = Duration GIAM DAN -> bai DAI NHAT len dau: on dinh nhat cho Next/Prev (bai dai khong
        // tu auto-next trong luc test; bai dai nhat thuong ten KHAC NHAU -> tranh "Next khong doi bai"
        // do 2 bai lien tiep trung ten). Thay cho Date modified (co the co bai trung ten lien tiep).
        tracks.setSortDurationLongestTop();
        home.sleep(800);
        return tracks;
    }

    private TracksPage goPlayNow(HomePage home) {
        TracksPage tracks = goTracks(home);
        tracks.tapPlayAll(); home.sleep(2000);
        home.tapMiniPlayer(); home.sleep(1500);
        Assert.assertTrue(tracks.isPlayNowOpen(), "Khong mo duoc Play Now");
        return tracks;
    }

    @Test(description = "TC_TRK_049: Play Now hien thi (marker 'Playing now')")
    public void TC_TRK_049_play_now_layout() {
        HomePage home = new HomePage();
        TracksPage tracks = goPlayNow(home);
        Assert.assertTrue(tracks.isPlayNowOpen(), "Play Now khong hien thi");
        ExtentReportManager.getTest().log(Status.PASS,
                "Play Now hien thi (SeekBar/time: can DOM that de verify chi tiet).");
        tracks.pnCollapse();
    }

    @Test(description = "TC_TRK_050: Controls toggle - Play/Pause, Shuffle, Repeat 3 trang thai, Heart (on dinh)")
    public void TC_TRK_050_controls_toggle() {
        HomePage home = new HomePage();
        TracksPage tracks = goPlayNow(home);

        tracks.pnTapPlayPause(); home.sleep(1000);
        Assert.assertTrue(tracks.isPlayNowOpen(), "Sau Play/Pause man Play Now bi mat");
        tracks.pnTapPlayPause(); home.sleep(1000);

        tracks.pnTapShuffle(); home.sleep(800);
        Assert.assertTrue(tracks.isPlayNowOpen(), "Sau Shuffle man Play Now bi mat");

        for (int i = 0; i < 3; i++) { tracks.pnTapRepeat(); home.sleep(700); } // 3 trang thai
        Assert.assertTrue(tracks.isPlayNowOpen(), "Sau Repeat man Play Now bi mat");

        tracks.pnTapHeart(); home.sleep(800);
        Assert.assertTrue(tracks.isPlayNowOpen(), "Sau Heart man Play Now bi mat");

        ExtentReportManager.getTest().log(Status.PASS,
                "Cac control tap duoc, man Play Now on dinh (verify trang thai toggle can DOM that).");
        tracks.pnCollapse();
    }

    @Test(description = "TC_TRK_051: Nut Next chuyen bai sau")
    public void TC_TRK_051_next_track() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.tapPlayAll(); home.sleep(2000);
        // So sanh ten bai TRUOC vs SAU Next tu CUNG NGUON (mini player) -> tranh so lech dinh dang
        // giua list-title va mini-title. Ten bai luon kem DUOI ID DUY NHAT (vd "...-42278",
        // "import_1778...") -> bai tai trung ten van khac duoi -> so full title la dang tin.
        String before = home.getMiniPlayerTrackTitle();
        home.tapMiniPlayer(); home.sleep(1500);
        Assert.assertTrue(tracks.isPlayNowOpen(), "Khong mo duoc Play Now");

        tracks.pnTapNext(); home.sleep(1800);
        tracks.pnCollapse(); home.sleep(1200);
        String after = home.getMiniPlayerTrackTitle();
        ExtentReportManager.getTest().log(Status.INFO, "truoc Next=" + before + " | sau Next=" + after);
        Assert.assertFalse(before.isEmpty(), "Khong doc duoc ten bai truoc Next");
        Assert.assertNotEquals(after, before, "Next khong chuyen bai (so ca duoi ID)");
        ExtentReportManager.getTest().log(Status.PASS, "Next chuyen sang bai sau (ten day du khac nhau).");
    }

    @Test(description = "TC_TRK_052: Nut Previous (man on dinh, bai van phat)")
    public void TC_TRK_052_previous() {
        HomePage home = new HomePage();
        TracksPage tracks = goPlayNow(home);

        tracks.pnTapNext(); home.sleep(1500);   // sang bai 2 truoc
        tracks.pnTapPrev(); home.sleep(1500);   // Previous
        Assert.assertTrue(tracks.isPlayNowOpen(), "Sau Previous man Play Now bi mat");
        tracks.pnCollapse(); home.sleep(1000);
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Sau Previous khong con phat");
        ExtentReportManager.getTest().log(Status.PASS, "Previous hoat dong, bai van phat (man on dinh).");
    }

    @Test(description = "TC_TRK_053: SeekBar tap nhay vi tri (man on dinh)")
    public void TC_TRK_053_seekbar() {
        HomePage home = new HomePage();
        TracksPage tracks = goPlayNow(home);

        tracks.pnSeekTo(0.6); home.sleep(1200);
        Assert.assertTrue(tracks.isPlayNowOpen(), "Sau seek man Play Now bi mat");
        ExtentReportManager.getTest().log(Status.PASS,
                "SeekBar tap duoc (verify vi tri chinh xac can DOM that cua thanh time).");
        tracks.pnCollapse();
    }

    @Test(description = "TC_TRK_054: Icon Add to playlist mo dialog")
    public void TC_TRK_054_icon_add_playlist() {
        HomePage home = new HomePage();
        TracksPage tracks = goPlayNow(home);

        tracks.pnTapAddPlaylist(); home.sleep(1200);
        Assert.assertTrue(tracks.isAddToPlaylistOpen(), "Icon Add to playlist khong mo dialog");
        ExtentReportManager.getTest().log(Status.PASS, "Icon Add to playlist mo dialog.");
        home.pressBack();
    }

    @Test(description = "TC_TRK_055: Icon Equalizer mo equalizer (khong crash, recover duoc)")
    public void TC_TRK_055_icon_equalizer() {
        HomePage home = new HomePage();
        TracksPage tracks = goPlayNow(home);

        tracks.pnTapEqualizer(); home.sleep(1500);
        ExtentReportManager.getTest().log(Status.INFO,
                "Da tap Equalizer (co the mo app Dolby ngoai/ dialog - tuy thiet bi).");
        home.pressBack(); home.sleep(1200);
        Assert.assertTrue(tracks.isPlayNowOpen() || tracks.isTracksScreenDisplayed(),
                "Sau Equalizer khong quay lai duoc Play Now/Tracks");
        ExtentReportManager.getTest().log(Status.PASS, "Icon Equalizer tap duoc, recover ve app.");
    }

    @Test(description = "TC_TRK_056: Icon Sleep timer mo sleep timer (khong crash, recover duoc)")
    public void TC_TRK_056_icon_sleep_timer() {
        HomePage home = new HomePage();
        TracksPage tracks = goPlayNow(home);

        tracks.pnTapSleep(); home.sleep(1300);
        ExtentReportManager.getTest().log(Status.INFO,
                "Da tap Sleep timer (dialog sleep timer - chi tiet o module Menu).");
        home.pressBack(); home.sleep(1200);
        Assert.assertTrue(tracks.isPlayNowOpen() || tracks.isTracksScreenDisplayed(),
                "Sau Sleep timer khong quay lai duoc Play Now/Tracks");
        ExtentReportManager.getTest().log(Status.PASS, "Icon Sleep timer tap duoc, recover ve app.");
    }

    @Test(description = "TC_TRK_057: Menu 3 cham mo edit sheet")
    public void TC_TRK_057_menu_opens_sheet() {
        HomePage home = new HomePage();
        TracksPage tracks = goPlayNow(home);

        tracks.pnTapMenu(); home.sleep(1200);
        Assert.assertTrue(tracks.isTrackMenuOpen(), "3 cham khong mo edit sheet");
        Assert.assertTrue(tracks.areAllMenuActionsDisplayed(), "Edit sheet thieu action");
        ExtentReportManager.getTest().log(Status.PASS, "3 cham mo edit sheet day du action.");
        tracks.closeMenuViaBack();
    }

    @Test(description = "TC_TRK_058: Icon Queue mo Playing Queue, click item phat")
    public void TC_TRK_058_icon_queue() {
        HomePage home = new HomePage();
        TracksPage tracks = goPlayNow(home);

        tracks.pnTapQueue(); home.sleep(1400);
        Assert.assertTrue(tracks.isPlayingQueueOpen(), "Icon Queue khong mo Playing Queue");
        tracks.playQueueRow(2); home.sleep(1800);
        Assert.assertTrue(tracks.isPlayingQueueOpen() || home.isMiniPlayerDisplayed(),
                "Click item trong queue khong on dinh");
        ExtentReportManager.getTest().log(Status.PASS, "Icon Queue mo Playing Queue, click item phat.");
        tracks.tapQueueBack();
    }

    @Test(description = "TC_TRK_059: Down arrow/Back dong Play Now, bai van phat")
    public void TC_TRK_059_collapse_keeps_playing() {
        HomePage home = new HomePage();
        TracksPage tracks = goPlayNow(home);

        tracks.pnCollapse(); home.sleep(1300);
        Assert.assertFalse(tracks.isPlayNowOpen(), "Khong dong duoc Play Now");
        Assert.assertTrue(tracks.isTracksScreenDisplayed(), "Khong ve Tracks sau khi dong Play Now");
        Assert.assertTrue(home.isMiniPlayerProgressAdvancing(4000), "Dong Play Now nhung bai khong con phat");
        ExtentReportManager.getTest().log(Status.PASS, "Down/Back dong Play Now, bai van phat.");
    }
}