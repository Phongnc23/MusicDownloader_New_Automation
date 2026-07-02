package testcases.artists;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ArtistsPage;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Artists - Edit Sheet (TC_ART_017..021).
 */
public class Artists03_Verify_Edit_Sheet extends BaseTest {

    private ArtistsPage goArtists(HomePage home) {
        ArtistsPage artists = new ArtistsPage();
        home.tapNavArtists();
        home.waitUntil(artists::isArtistsScreenDisplayed, 6000);
        Assert.assertTrue(artists.isArtistsScreenDisplayed(), "Khong vao duoc man Artists");
        return artists;
    }

    @Test(description = "TC_ART_017: Click 3 cham artist mo sheet")
    public void TC_ART_017_open_sheet() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        artists.openArtistMenuFromList();
        home.sleep(1000);
        Assert.assertTrue(artists.isFourActionSheetOpen(), "Khong mo duoc edit sheet artist");
        ExtentReportManager.getTest().log(Status.PASS, "Click 3 cham -> edit sheet mo.");
        artists.closeSheetViaBack();
    }

    @Test(description = "TC_ART_018: Sheet co 4 action (khong Rename/Delete/File info)")
    public void TC_ART_018_four_actions_only() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        artists.openArtistMenuFromList();
        home.sleep(1000);
        Assert.assertTrue(artists.areFourActionsDisplayed(), "Thieu 1 trong 4 action (Play/Queue/Playlist/Share)");
        Assert.assertTrue(artists.sheetHasNoExtraActions(),
                "Sheet artist khong duoc co Rename/Delete/File information");
        ExtentReportManager.getTest().log(Status.PASS, "Sheet artist chi co 4 action.");
        artists.closeSheetViaBack();
    }

    @Test(description = "TC_ART_019: Sheet hien thi artist info (name + N songs)")
    public void TC_ART_019_sheet_info() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        int cardCount = artists.getFirstArtistTrackCount();
        artists.openArtistMenuFromList();
        home.sleep(1000);
        int sheetCount = artists.getSheetSongCount();
        Assert.assertTrue(sheetCount > 0, "Sheet khong hien so bai");
        Assert.assertEquals(sheetCount, cardCount, "So bai tren sheet (" + sheetCount
                + ") khac so tracks tren card (" + cardCount + ")");
        ExtentReportManager.getTest().log(Status.PASS, "Sheet hien dung artist info: " + sheetCount + " songs.");
        artists.closeSheetViaBack();
    }

    @Test(description = "TC_ART_020: Tap Scrim dong sheet")
    public void TC_ART_020_scrim_close() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        artists.openArtistMenuFromList();
        home.sleep(1000);
        Assert.assertTrue(artists.isFourActionSheetOpen(), "Truoc dieu kien: sheet chua mo");
        artists.closeSheetViaScrim();
        home.sleep(800);
        Assert.assertFalse(artists.isFourActionSheetOpen(), "Tap Scrim khong dong duoc sheet");
        ExtentReportManager.getTest().log(Status.PASS, "Tap Scrim dong sheet.");
    }

    @Test(description = "TC_ART_021: Press BACK dong sheet")
    public void TC_ART_021_back_close() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        artists.openArtistMenuFromList();
        home.sleep(1000);
        Assert.assertTrue(artists.isFourActionSheetOpen(), "Truoc dieu kien: sheet chua mo");
        artists.closeSheetViaBack();
        home.sleep(800);
        Assert.assertFalse(artists.isFourActionSheetOpen(), "BACK khong dong duoc sheet");
        ExtentReportManager.getTest().log(Status.PASS, "BACK dong sheet.");
    }
}