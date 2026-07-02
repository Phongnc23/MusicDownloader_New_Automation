package testcases.tracks;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.TracksPage;
import report.ExtentReportManager;

/**
 * Module: Tracks - Edit sheet (context menu) & dialogs (TC_TRK_026..034).
 * Edit sheet 7 action: Play / Add to playing queue / Add to playlist / Rename / File information /
 * Share track / Delete from device.
 */
public class Tracks04_Verify_EditSheet_And_Dialogs extends BaseTest {

    private TracksPage goTracks(HomePage home) {
        TracksPage tracks = new TracksPage();
        home.tapNavTracks();
        home.waitUntil(tracks::isTracksScreenDisplayed, 6000);
        Assert.assertTrue(tracks.isTracksScreenDisplayed(), "Khong vao duoc man Tracks");
        return tracks;
    }

    @Test(description = "TC_TRK_026: Edit sheet mo voi day du 7 action")
    public void TC_TRK_026_sheet_all_actions() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.openTrackMenu(0);
        home.sleep(1000);
        Assert.assertTrue(tracks.isTrackMenuOpen(), "Khong mo duoc edit sheet");
        Assert.assertTrue(tracks.areAllMenuActionsDisplayed(), "Edit sheet thieu action (can du 7)");
        ExtentReportManager.getTest().log(Status.PASS, "Edit sheet mo day du 7 action.");
        tracks.closeMenuViaScrim();
    }

    @Test(description = "TC_TRK_027: Dong sheet bang Scrim, BACK hoac Swipe down")
    public void TC_TRK_027_sheet_close_ways() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.openTrackMenu(0); home.sleep(900);
        tracks.closeMenuViaScrim(); home.sleep(800);
        Assert.assertFalse(tracks.isTrackMenuOpen(), "Khong dong sheet bang Scrim");

        tracks.openTrackMenu(0); home.sleep(900);
        tracks.closeMenuViaBack(); home.sleep(800);
        Assert.assertFalse(tracks.isTrackMenuOpen(), "Khong dong sheet bang BACK");

        tracks.openTrackMenu(0); home.sleep(900);
        tracks.closeMenuViaSwipeDown(); home.sleep(900);
        Assert.assertFalse(tracks.isTrackMenuOpen(), "Khong dong sheet bang Swipe down");

        ExtentReportManager.getTest().log(Status.PASS, "Dong edit sheet bang Scrim/BACK/Swipe down OK.");
    }

    @Test(description = "TC_TRK_028: Sheet hien dung track duoc chon")
    public void TC_TRK_028_sheet_shows_selected_track() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        String title = tracks.getFirstTrackTitle();
        tracks.openTrackMenu(0); home.sleep(900);
        Assert.assertTrue(tracks.menuShowsTitle(title), "Sheet khong hien tieu de track da chon: " + title);
        ExtentReportManager.getTest().log(Status.PASS, "Sheet hien dung track: " + title);
        tracks.closeMenuViaScrim();
    }

    @Test(description = "TC_TRK_029: Add to playlist mo dialog co My Favorite")
    public void TC_TRK_029_add_playlist_dialog() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.openTrackMenu(0); home.sleep(900);
        tracks.tapMenuAddPlaylist(); home.sleep(1000);
        Assert.assertTrue(tracks.isAddToPlaylistOpen(), "Khong mo duoc Add to playlist");
        Assert.assertTrue(tracks.isPlaylistListed("My Favorite"), "Khong thay playlist 'My Favorite'");
        ExtentReportManager.getTest().log(Status.PASS, "Add to playlist mo, co My Favorite.");
    }

    @Test(description = "TC_TRK_030: Rename mo dialog voi text prefilled + char count")
    public void TC_TRK_030_rename_dialog_prefilled() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        String title = tracks.getFirstTrackTitle();
        tracks.openTrackMenu(0); home.sleep(900);
        tracks.tapMenuRename(); home.sleep(1000);
        Assert.assertTrue(tracks.isRenameDialogOpen(), "Khong mo duoc Rename dialog");
        Assert.assertFalse(tracks.getRenameText().isEmpty(), "Rename khong prefill tieu de");
        Assert.assertTrue(tracks.getRenameCounter().matches("\\d+/60"), "Char count khong dung dang N/60");
        ExtentReportManager.getTest().log(Status.PASS,
                "Rename prefilled=\"" + title + "\", counter=" + tracks.getRenameCounter());
        tracks.tapRenameCancel();
    }

    @Test(description = "TC_TRK_031: File info mo man voi day du fields")
    public void TC_TRK_031_file_info_fields() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.openTrackMenu(0); home.sleep(900);
        tracks.tapMenuFileInfo(); home.sleep(1000);
        Assert.assertTrue(tracks.isTrackInfoOpen(), "Khong mo duoc Track information");
        Assert.assertTrue(tracks.areAllInfoFieldsDisplayed(), "Thieu field thong tin");
        ExtentReportManager.getTest().log(Status.PASS, "Track information hien day du fields.");
        tracks.closeInfo();
    }

    @Test(description = "TC_TRK_032: Delete mo confirm dialog co ten track (khong xoa - CANCEL)")
    public void TC_TRK_032_delete_confirm_no_delete() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        int before = tracks.getTrackCount();
        tracks.openTrackMenu(0); home.sleep(900);
        tracks.tapMenuDelete(); home.sleep(900);
        Assert.assertTrue(tracks.isDeleteConfirmOpen(), "Khong mo duoc confirm dialog");
        Assert.assertTrue(tracks.isDeleteTitleShown(), "Thieu tieu de 'Delete from device'");
        Assert.assertTrue(tracks.deleteMessageContains("Do you want to delete"),
                "Confirm khong hoi dung dang single ('Do you want to delete the ...?')");
        tracks.tapDeleteCancel(); home.sleep(900);
        Assert.assertEquals(tracks.getTrackCount(), before, "CANCEL ma so track van doi");
        ExtentReportManager.getTest().log(Status.PASS, "Delete confirm hien dung, CANCEL khong xoa.");
    }

    @Test(description = "TC_TRK_033: Heart tren Play Now TOGGLE favorite (My Favorite thay doi 1)")
    public void TC_TRK_033_heart_toggle_favorite() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        // Baseline so bai trong My Favorite
        tracks.openTrackMenu(2); home.sleep(900);
        tracks.tapMenuAddPlaylist(); home.sleep(900);
        int favBefore = tracks.getFavoriteCount();
        tracks.closeMenuViaBack(); home.sleep(800); // dong add-to-playlist sheet

        // Phat bai 2 -> Play Now -> tap Heart (toa do uoc luong)
        tracks.playTrack(2); home.sleep(2000);
        home.tapMiniPlayer(); home.sleep(1500);
        Assert.assertTrue(tracks.isPlayNowOpen(), "Khong mo duoc Play Now");
        tracks.pnTapHeart(); home.sleep(1500);
        tracks.pnCollapse(); home.sleep(1200);

        // Kiem My Favorite
        tracks.openTrackMenu(2); home.sleep(900);
        tracks.tapMenuAddPlaylist(); home.sleep(900);
        int favAfter = tracks.getFavoriteCount();
        ExtentReportManager.getTest().log(Status.INFO, "My Favorite truoc=" + favBefore + " sau=" + favAfter
                + " (Heart la TOGGLE: bai chua fav -> +1; bai da fav -> -1)");
        Assert.assertTrue(favAfter >= 0 && favBefore >= 0, "Khong doc duoc so My Favorite");
        // Heart la nut TOGGLE: neu bai CHUA favorite -> tap them (+1); neu DA favorite (tu lan chay
        // truoc, noReset) -> tap bo (-1). Nen chi assert so thay doi DUNG 1 (bat ky chieu nao),
        // KHONG assert luon tang (se fail khi bai da san favorite).
        Assert.assertEquals(Math.abs(favAfter - favBefore), 1,
                "Tap Heart nhung so My Favorite khong thay doi dung 1 (truoc=" + favBefore + " sau=" + favAfter + ")");
        ExtentReportManager.getTest().log(Status.PASS, "Heart toggle My Favorite (thay doi 1 bai).");
        tracks.closeMenuViaBack();
    }

    @Test(description = "TC_TRK_034: Create new playlist - CANCEL khong tao, SAVE tao moi")
    public void TC_TRK_034_create_playlist_cancel_save() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        String name = "QA_PL_" + (System.currentTimeMillis() % 100000);

        // CANCEL
        tracks.openTrackMenu(0); home.sleep(900);
        tracks.tapMenuAddPlaylist(); home.sleep(900);
        tracks.tapCreateNewPlaylist(); home.sleep(900);
        Assert.assertTrue(tracks.isCreatePlaylistDialogOpen(), "Khong mo duoc Create new playlist");
        tracks.enterPlaylistName(name + "_CANCEL");
        tracks.tapCreateCancel(); home.sleep(900);
        Assert.assertFalse(tracks.isPlaylistListed(name + "_CANCEL"),
                "CANCEL nhung playlist van duoc tao");
        ExtentReportManager.getTest().log(Status.INFO, "CANCEL: khong tao playlist.");
        home.pressBack(); home.sleep(600); // dong add-to-playlist sheet con lai

        // SAVE
        tracks.openTrackMenu(0); home.sleep(900);
        tracks.tapMenuAddPlaylist(); home.sleep(900);
        tracks.tapCreateNewPlaylist(); home.sleep(900);
        tracks.enterPlaylistName(name);
        tracks.tapCreateSave(); home.sleep(1200);

        // Mo lai add-to-playlist de kiem playlist moi xuat hien
        if (!tracks.isAddToPlaylistOpen()) {
            tracks.openTrackMenu(0); home.sleep(900);
            tracks.tapMenuAddPlaylist(); home.sleep(900);
        }
        Assert.assertTrue(tracks.isPlaylistListed(name), "SAVE nhung khong thay playlist moi: " + name);
        ExtentReportManager.getTest().log(Status.PASS, "Create new playlist: CANCEL khong tao, SAVE tao '" + name + "'.");
        home.pressBack();
    }
}
