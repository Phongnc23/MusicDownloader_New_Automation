package testcases.playlists;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.PlaylistsPage;
import report.ExtentReportManager;

/**
 * Module: Playlists - UI Display (TC_PL_001..002).
 */
public class Playlists01_Verify_UI_Display extends BaseTest {

    private PlaylistsPage goPlaylists(HomePage home) {
        PlaylistsPage pl = new PlaylistsPage();
        pl.gotoPlaylistsList(home);
        Assert.assertTrue(pl.isPlaylistsScreenDisplayed(), "Khong vao duoc man Playlists");
        return pl;
    }

    @Test(description = "TC_PL_001: Header, label 'Local', nut Create va bottom nav hien thi")
    public void TC_PL_001_header_local_create_nav() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        Assert.assertTrue(pl.isLocalLabelDisplayed(), "Khong thay label 'Local playlist'");
        Assert.assertTrue(pl.isCreateButtonDisplayed(), "Khong thay nut 'Create new playlist'");
        Assert.assertTrue(home.isBottomNavDisplayed(), "Bottom nav khong hien thi");
        ExtentReportManager.getTest().log(Status.PASS, "Header + Local label + Create + bottom nav OK.");
    }

    @Test(description = "TC_PL_002: Local playlists (My Favorite/Recently Played) co count + user playlists")
    public void TC_PL_002_local_and_user_playlists() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        Assert.assertTrue(pl.isMyFavoriteListed(), "Khong thay My Favorite");
        Assert.assertTrue(pl.isRecentlyPlayedListed(), "Khong thay Recently Played");
        Assert.assertTrue(pl.getTrackCountOf("My Favorite") > 0, "My Favorite khong co count");
        Assert.assertTrue(pl.getTrackCountOf("Recently Played") > 0, "Recently Played khong co count");
        Assert.assertTrue(pl.getUserPlaylistCount() >= 0, "Khong thay label 'My playlist (N)'");
        ExtentReportManager.getTest().log(Status.PASS,
                "My Favorite=" + pl.getTrackCountOf("My Favorite") + " tracks, Recently Played="
                        + pl.getTrackCountOf("Recently Played") + " tracks, user playlists="
                        + pl.getUserPlaylistCount());
    }
}