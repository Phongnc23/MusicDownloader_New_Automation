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
 * Module: Playlists - Sheets (TC_PL_012..016).
 * USER_PL: user playlist - TU TAO neu chua co (portable, khong phu thuoc data san tren may).
 */
public class Playlists04_Verify_Sheets extends BaseTest {

    private static final String USER_PL = AppConstants.AUTO_USER_PLAYLIST;

    private PlaylistsPage goPlaylists(HomePage home) {
        PlaylistsPage pl = new PlaylistsPage();
        pl.gotoPlaylistsList(home);
        Assert.assertTrue(pl.isPlaylistsScreenDisplayed(), "Khong vao duoc man Playlists");
        return pl;
    }

    @Test(description = "TC_PL_012: User playlist sheet 6 action (co Rename + Delete)")
    public void TC_PL_012_user_sheet_six_actions() {
        HomePage home = new HomePage();
        PlaylistsPage pl = new PlaylistsPage();

        // Portable: TU TAO user playlist neu chua co.
        pl.ensureUserPlaylist(home, USER_PL);
        Assert.assertTrue(pl.isPlaylistListed(USER_PL), "Khong tao/thay duoc user playlist " + USER_PL);
        pl.openPlaylistMenu(USER_PL);
        home.sleep(900);
        Assert.assertTrue(pl.isUserPlaylistSheetOpen(),
                "Sheet user playlist khong du 6 action (Play/Queue/Playlist/Rename/Share/Delete)");
        pl.closeSheetViaBack();
        ExtentReportManager.getTest().log(Status.PASS, "User playlist sheet 6 action OK.");
    }

    @Test(description = "TC_PL_013: Tap Scrim dong sheet")
    public void TC_PL_013_scrim_close_sheet() {
        HomePage home = new HomePage();
        PlaylistsPage pl = new PlaylistsPage();

        // Portable: TU TAO user playlist neu chua co.
        pl.ensureUserPlaylist(home, USER_PL);
        pl.openPlaylistMenu(USER_PL);
        home.sleep(900);
        Assert.assertTrue(pl.isSheetOpen(), "Sheet khong mo");
        pl.closeSheetViaScrim();
        home.sleep(900);
        Assert.assertFalse(pl.isSheetOpen(), "Scrim khong dong duoc sheet");
        ExtentReportManager.getTest().log(Status.PASS, "Scrim dong sheet OK.");
    }

    @Test(description = "TC_PL_014: Recently Played sheet co Clear, khong Rename/Delete")
    public void TC_PL_014_recently_played_sheet() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        pl.openPlaylistMenu("Recently Played");
        home.sleep(900);
        Assert.assertTrue(pl.isLocalSheetOpen(), "Recently Played khong phai local sheet (co Rename/Delete?)");
        Assert.assertTrue(pl.sheetHasClearRecentlyPlayed(), "Thieu 'Clear recently played'");
        pl.closeSheetViaBack();
        ExtentReportManager.getTest().log(Status.PASS, "Recently Played local sheet + Clear OK.");
    }

    @Test(description = "TC_PL_015: My Favorite la local sheet (khong Rename/Delete), co 'Clear my favorite'")
    public void TC_PL_015_my_favorite_sheet() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        pl.openPlaylistMenu("My Favorite");
        home.sleep(900);
        Assert.assertTrue(pl.isLocalSheetOpen(), "My Favorite khong phai local sheet (co Rename/Delete?)");
        Assert.assertTrue(pl.sheetHasClearMyFavorite(), "Thieu 'Clear my favorite'");
        pl.closeSheetViaBack();
        ExtentReportManager.getTest().log(Status.PASS, "My Favorite local sheet + Clear my favorite OK.");
    }

    @Test(description = "TC_PL_016: Sheet 'Add to playlist' mo picker (Create new + danh sach)")
    public void TC_PL_016_add_to_playlist_picker() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        pl.openPlaylistMenu("Recently Played");
        home.sleep(900);
        Assert.assertTrue(pl.isSheetOpen(), "Sheet khong mo");
        pl.tapSheetAddList();
        home.sleep(1200);
        Assert.assertTrue(pl.isAddToPlaylistPickerOpen(),
                "Khong mo duoc picker 'Add to playlist' (Create new + danh sach playlist)");
        pl.closeSheetViaBack();
        home.sleep(500);
        ExtentReportManager.getTest().log(Status.PASS, "Add to playlist mo picker OK.");
    }
}