package testcases.artists;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ArtistsPage;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Artists - Detail Edit Sheet (TC_ART_043..046).
 * Sheet mo tu nut 3 cham tren ARTIST DETAIL = 4 action (Play / Add to playing queue /
 * Add to playlist / Share track) + header "<name>\nN songs", KHONG co Rename/Delete/File info.
 * (Cau truc giong sheet o list nhung mo tu man Detail.)
 */
public class Artists07_Verify_Detail_Edit_Sheet extends BaseTest {

    private ArtistsPage goArtistDetail(HomePage home) {
        ArtistsPage artists = new ArtistsPage();
        home.tapNavArtists();
        home.waitUntil(artists::isArtistsScreenDisplayed, 6000);
        Assert.assertTrue(artists.isArtistsScreenDisplayed(), "Khong vao duoc man Artists");
        artists.tapArtistCard(0);
        home.waitUntil(artists::isArtistDetailOpen, 6000);
        Assert.assertTrue(artists.isArtistDetailOpen(), "Khong mo duoc Artist Detail");
        return artists;
    }

    @Test(description = "TC_ART_043: Click 3 cham tren Artist Detail mo edit sheet")
    public void TC_ART_043_open_detail_sheet() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtistDetail(home);

        artists.tapDetailMenu();
        home.waitUntil(artists::isFourActionSheetOpen, 4000);
        Assert.assertTrue(artists.isFourActionSheetOpen(), "3 cham tren Detail khong mo duoc edit sheet");
        ExtentReportManager.getTest().log(Status.PASS, "3 cham Artist Detail -> edit sheet mo.");
        artists.closeSheetViaBack();
    }

    @Test(description = "TC_ART_044: Edit sheet detail co 4 action (khong Rename/Delete/File info)")
    public void TC_ART_044_four_actions_only() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtistDetail(home);

        artists.tapDetailMenu();
        home.waitUntil(artists::isFourActionSheetOpen, 4000);
        Assert.assertTrue(artists.areFourActionsDisplayed(), "Thieu 1 trong 4 action (Play/Queue/Playlist/Share)");
        Assert.assertTrue(artists.sheetHasNoExtraActions(),
                "Sheet detail khong duoc co Rename/Delete/File information");
        ExtentReportManager.getTest().log(Status.PASS, "Sheet detail chi co 4 action.");
        artists.closeSheetViaBack();
    }

    @Test(description = "TC_ART_045: Sheet detail hien dung artist info (name + N songs)")
    public void TC_ART_045_sheet_info() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtistDetail(home);

        String heroName = artists.getDetailHeroName();
        int heroSongs = artists.getDetailHeroSongCount();
        artists.tapDetailMenu();
        home.waitUntil(artists::isFourActionSheetOpen, 4000);
        int sheetSongs = artists.getSheetSongCount();
        Assert.assertTrue(sheetSongs > 0, "Sheet detail khong hien so bai");
        Assert.assertEquals(sheetSongs, heroSongs,
                "So bai sheet (" + sheetSongs + ") khac hero detail (" + heroSongs + ")");
        ExtentReportManager.getTest().log(Status.PASS,
                "Sheet detail hien dung info: \"" + heroName + "\" - " + sheetSongs + " songs.");
        artists.closeSheetViaBack();
    }

    @Test(description = "TC_ART_046: Sheet detail dong bang Scrim va BACK")
    public void TC_ART_046_close_scrim_back() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtistDetail(home);

        // Cach 1: Scrim
        artists.tapDetailMenu();
        home.waitUntil(artists::isFourActionSheetOpen, 4000);
        artists.closeSheetViaScrim();
        home.waitUntil(() -> !artists.isFourActionSheetOpen(), 3000);
        Assert.assertFalse(artists.isFourActionSheetOpen(), "Tap Scrim khong dong duoc sheet detail");

        // Cach 2: BACK
        artists.tapDetailMenu();
        home.waitUntil(artists::isFourActionSheetOpen, 4000);
        artists.closeSheetViaBack();
        home.waitUntil(() -> !artists.isFourActionSheetOpen(), 3000);
        Assert.assertFalse(artists.isFourActionSheetOpen(), "BACK khong dong duoc sheet detail");

        ExtentReportManager.getTest().log(Status.PASS, "Sheet detail dong bang Scrim va BACK.");
    }
}
