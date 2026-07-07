package testcases.playlists;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import constants.AppConstants;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.PlaylistsPage;
import report.ExtentReportManager;

/**
 * Module: Playlists - Detail Navigation (TC_PL_003..007).
 * EMPTY_PL: user playlist RONG - TU TAO neu chua co (portable, khong phu thuoc data san tren may).
 */
public class Playlists02_Verify_Detail_Navigation extends BaseTest {

    private static final String EMPTY_PL = AppConstants.AUTO_USER_PLAYLIST;

    private PlaylistsPage goPlaylists(HomePage home) {
        PlaylistsPage pl = new PlaylistsPage();
        pl.gotoPlaylistsList(home);
        Assert.assertTrue(pl.isPlaylistsScreenDisplayed(), "Khong vao duoc man Playlists");
        return pl;
    }

    @Test(description = "TC_PL_003: My Favorite detail (header, hero, Play/Shuffle) va Back ve list")
    public void TC_PL_003_my_favorite_detail() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        pl.tapPlaylistByName("My Favorite");
        home.sleep(1500);
        Assert.assertTrue(pl.isPlaylistDetailOpen("My Favorite"), "Khong mo duoc My Favorite detail");
        Assert.assertTrue(pl.isDetailWithControlsOpen(), "Thieu Play all / Shuffle");
        Assert.assertTrue(pl.getDetailHeroTrackCount() > 0, "Hero khong co so track");
        pl.tapDetailBack();
        home.sleep(1200);
        Assert.assertTrue(pl.isPlaylistsScreenDisplayed(), "Back khong ve duoc Playlists");
        ExtentReportManager.getTest().log(Status.PASS, "My Favorite detail OK + Back ve list.");
    }

    @Test(description = "TC_PL_004: Recently Played detail mo va hero")
    public void TC_PL_004_recently_played_detail() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        pl.tapPlaylistByName("Recently Played");
        home.sleep(1500);
        Assert.assertTrue(pl.isPlaylistDetailOpen("Recently Played"), "Khong mo duoc Recently Played detail");
        Assert.assertTrue(pl.getDetailHeroTrackCount() > 0, "Hero khong co so track");
        ExtentReportManager.getTest().log(Status.PASS,
                "Recently Played detail: " + pl.getDetailHeroTrackCount() + " tracks.");
    }

    @Test(description = "TC_PL_005: Play all trong detail thuc su phat")
    public void TC_PL_005_play_all() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        pl.tapPlaylistByName("Recently Played");
        home.sleep(1500);
        Assert.assertTrue(pl.isDetailWithControlsOpen(), "Chua vao detail");
        pl.tapDetailPlayAll();
        home.sleep(2200);
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Play all khong phat");
        ExtentReportManager.getTest().log(Status.PASS, "Play all -> phat.");
    }

    @Test(description = "TC_PL_006: Click track trong detail phat")
    public void TC_PL_006_play_track() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        pl.tapPlaylistByName("Recently Played");
        home.sleep(1500);
        Assert.assertTrue(pl.getDetailRowCount() > 0, "Detail khong co track");
        pl.playDetailTrack(0);
        home.sleep(2200);
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Click track khong phat");
        ExtentReportManager.getTest().log(Status.PASS, "Click track -> phat.");
    }

    @Test(description = "TC_PL_007: Empty playlist (0 track) detail mo, hero '0 tracks', co Add new track")
    public void TC_PL_007_empty_playlist_detail() {
        HomePage home = new HomePage();
        PlaylistsPage pl = new PlaylistsPage();

        // Portable: TU TAO playlist rong neu chua co (khong phu thuoc fixture san tren may).
        pl.ensureUserPlaylist(home, EMPTY_PL);
        Assert.assertTrue(pl.isPlaylistsScreenDisplayed(), "Khong vao duoc man Playlists");
        Assert.assertTrue(pl.isPlaylistListed(EMPTY_PL), "Khong tao/thay duoc playlist rong " + EMPTY_PL);
        pl.tapPlaylistByName(EMPTY_PL);
        home.sleep(1500);
        Assert.assertTrue(pl.isDetailWithControlsOpen(), "Chua vao detail playlist rong");
        Assert.assertEquals(pl.getDetailHeroTrackCount(), 0, "Hero khong phai '0 tracks'");
        Assert.assertTrue(pl.isEmptyPlaylistShown(), "Khong thay trang thai 'Empty playlist'");
        Assert.assertTrue(pl.isAddNewTrackDisplayed(), "Khong thay 'Add new track'");
        ExtentReportManager.getTest().log(Status.PASS, "Empty playlist detail + Add new track OK.");
    }
}