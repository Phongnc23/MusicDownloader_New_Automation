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
        // MINH CHUNG: chup Play Now dang hien thi truoc khi collapse
        ExtentReportManager.attachProof("Play Now dang hien thi - minh chung");
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

        // MINH CHUNG: chup Play Now on dinh sau khi tap cac control truoc khi collapse
        ExtentReportManager.attachProof("Play Now on dinh sau khi tap cac control - minh chung");
        tracks.pnCollapse();
    }

    @Test(description = "TC_TRK_051: Nut Next chuyen bai sau (xac nhan qua chi so queue)")
    public void TC_TRK_051_next_track() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.tapPlayAll(); home.sleep(2000);
        String titleBefore = home.getMiniPlayerTrackTitle();

        // XAC NHAN NEXT bang CHI SO QUEUE "(N/total)" - ben vung voi thu vien co bai TRUNG TEN.
        // Thu vien co bai viral tai NHIEU BAN SAO (hau to id nguon "-998054" nam TRONG title -> cac ban sao
        // co title GIONG HET, cung ~72 phut) -> KHONG the dung title de biet da Next hay chua. Playing Queue
        // header hien vi tri bai dang phat "(N/total)" -> doc N truoc va sau Next: N phai TANG = Next da tien queue.
        home.tapMiniPlayerQueue(); home.sleep(1500);
        Assert.assertTrue(tracks.isPlayingQueueOpen(), "Khong mo duoc Playing Queue de doc vi tri truoc Next");
        int posBefore = tracks.getQueuePosition();
        Assert.assertTrue(posBefore > 0, "Khong doc duoc vi tri queue truoc Next (N/total)");
        tracks.tapQueueBack(); home.sleep(1000);

        // Bam Next 2 lan tren Play Now. Bai dau 71:58 KHONG tu auto-next -> Next that su phai tien queue.
        for (int i = 0; i < 2; i++) {
            home.tapMiniPlayer(); home.sleep(1400);
            if (!tracks.isPlayNowOpen()) { home.tapMiniPlayer(); home.sleep(1000); }
            Assert.assertTrue(tracks.isPlayNowOpen(), "Khong mo duoc Play Now de bam Next");
            tracks.pnTapNext(); home.sleep(1600);
            tracks.pnCollapse(); home.sleep(1000);
        }

        // Doc vi tri SAU -> phai TANG (Next da tien queue). Bai sieu ngan co the auto-next -> N tang > 2, van dung.
        home.tapMiniPlayerQueue(); home.sleep(1500);
        Assert.assertTrue(tracks.isPlayingQueueOpen(), "Khong mo lai duoc Playing Queue de doc vi tri sau Next");
        int posAfter = tracks.getQueuePosition();
        ExtentReportManager.getTest().log(Status.INFO,
                "Queue position: truoc=" + posBefore + " sau=" + posAfter + " | title truoc Next=" + titleBefore);
        // MINH CHUNG: chup Playing Queue voi vi tri da tien (bai dang phat highlight o dong moi)
        ExtentReportManager.attachProof("Playing Queue sau Next - vi tri " + posBefore + " -> " + posAfter);
        tracks.tapQueueBack(); home.sleep(800);

        Assert.assertTrue(posAfter > posBefore,
                "Next KHONG tien queue: vi tri truoc=" + posBefore + " sau=" + posAfter
              + " (co the ket queue hoac dang repeat-one)");
        ExtentReportManager.getTest().log(Status.PASS,
                "Next tien queue tu vi tri " + posBefore + " -> " + posAfter + " (xac nhan qua chi so N/total).");
    }

    @Test(description = "TC_TRK_052: Nut Previous (man on dinh, bai van phat)")
    public void TC_TRK_052_previous() {
        HomePage home = new HomePage();
        TracksPage tracks = goPlayNow(home);

        tracks.pnTapNext(); home.sleep(1500);   // sang bai 2 truoc
        tracks.pnTapPrev(); home.sleep(1500);   // Previous
        Assert.assertTrue(tracks.isPlayNowOpen(), "Sau Previous man Play Now bi mat");
        // MINH CHUNG: chup Play Now on dinh sau khi bam Previous truoc khi collapse
        ExtentReportManager.attachProof("Da bam Previous, Play Now on dinh - minh chung");
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
        // MINH CHUNG: chup Play Now on dinh sau khi seek truoc khi collapse
        ExtentReportManager.attachProof("Play Now on dinh sau khi seek - minh chung");
        tracks.pnCollapse();
    }

    @Test(description = "TC_TRK_054: Icon Add to playlist mo dialog")
    public void TC_TRK_054_icon_add_playlist() {
        HomePage home = new HomePage();
        TracksPage tracks = goPlayNow(home);

        tracks.pnTapAddPlaylist(); home.sleep(1200);
        Assert.assertTrue(tracks.isAddToPlaylistOpen(), "Icon Add to playlist khong mo dialog");
        // MINH CHUNG: chup Add to playlist dang mo tu Play Now truoc khi back
        ExtentReportManager.attachProof("Add to playlist da mo tu Play Now - minh chung");
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
        // MINH CHUNG: chup edit sheet mo tu 3 cham tren Play Now truoc khi dong
        ExtentReportManager.attachProof("Edit sheet mo tu 3 cham tren Play Now - minh chung");
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
        // MINH CHUNG: chup Playing Queue mo tu Play Now (da click item) truoc khi back
        ExtentReportManager.attachProof("Playing Queue mo tu Play Now, da click item - minh chung");
        tracks.tapQueueBack();
    }

    @Test(description = "TC_TRK_059: Down arrow/Back dong Play Now, bai van phat")
    public void TC_TRK_059_collapse_keeps_playing() {
        HomePage home = new HomePage();
        TracksPage tracks = goPlayNow(home);

        tracks.pnCollapse(); home.sleep(1300);
        Assert.assertFalse(tracks.isPlayNowOpen(), "Khong dong duoc Play Now");
        Assert.assertTrue(tracks.isTracksScreenDisplayed(), "Khong ve Tracks sau khi dong Play Now");
        // TIEU CHI (theo QA): bai VAN chay sau collapse = pass. "%" content-desc KHONG dang tin voi bai
        // DAI (duration <unknown> -> ket 0% du bar van chay) -> xac nhan mini player VAN co bai (title).
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Dong Play Now -> mat mini player (bai khong con)");
        Assert.assertFalse(home.getMiniPlayerTrackTitle().isEmpty(),
                "Dong Play Now nhung bai khong con phat");
        boolean active = home.isPlaybackActive(9000); // best-effort, chi log tham khao
        ExtentReportManager.getTest().log(Status.INFO, "Playback active (best-effort) = " + active);
        ExtentReportManager.getTest().log(Status.PASS,
                "Down/Back dong Play Now, mini player van co bai dang phat.");
    }
}