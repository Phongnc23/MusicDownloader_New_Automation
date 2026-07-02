package testcases.albums;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AlbumsPage;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Albums - Edit Sheet (TC_ALB_010..012).
 */
public class Albums04_Verify_Edit_Sheet extends BaseTest {

    private AlbumsPage goAlbums(HomePage home) {
        AlbumsPage albums = new AlbumsPage();
        albums.gotoAlbumsList(home);
        Assert.assertTrue(albums.isAlbumsScreenDisplayed(), "Khong vao duoc man Albums");
        return albums;
    }

    @Test(description = "TC_ALB_010: Sheet mo voi 4 action va header (name + N songs)")
    public void TC_ALB_010_sheet_four_actions() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);

        int firstCount = albums.getFirstAlbumTrackCount();
        albums.openAlbumMenuFromList();
        home.sleep(1000);
        Assert.assertTrue(albums.areFourActionsDisplayed(), "Thieu 1 trong 4 action");
        Assert.assertTrue(albums.sheetHasNoExtraActions(), "Sheet album khong duoc co Rename/Delete/File info");
        int sheetCount = albums.getSheetSongCount();
        Assert.assertTrue(sheetCount > 0, "Sheet khong hien so bai");
        Assert.assertEquals(sheetCount, firstCount, "So bai sheet (" + sheetCount + ") khac card (" + firstCount + ")");
        ExtentReportManager.getTest().log(Status.PASS, "Sheet 4 action + header dung (" + sheetCount + " songs).");
        albums.closeSheetViaBack();
    }

    @Test(description = "TC_ALB_011: Tap Scrim dong sheet")
    public void TC_ALB_011_scrim_close() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);

        albums.openAlbumMenuFromList();
        home.sleep(1000);
        Assert.assertTrue(albums.isFourActionSheetOpen(), "Truoc dieu kien: sheet chua mo");
        albums.closeSheetViaScrim();
        home.sleep(800);
        Assert.assertFalse(albums.isFourActionSheetOpen(), "Tap Scrim khong dong duoc sheet");
        ExtentReportManager.getTest().log(Status.PASS, "Tap Scrim dong sheet.");
    }

    @Test(description = "TC_ALB_012: Press BACK dong sheet ve Albums")
    public void TC_ALB_012_back_close() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);

        albums.openAlbumMenuFromList();
        home.sleep(1000);
        Assert.assertTrue(albums.isFourActionSheetOpen(), "Truoc dieu kien: sheet chua mo");
        albums.closeSheetViaBack();
        home.sleep(800);
        Assert.assertFalse(albums.isFourActionSheetOpen(), "BACK khong dong duoc sheet");
        Assert.assertTrue(albums.isAlbumsScreenDisplayed(), "BACK khong tro lai Albums");
        ExtentReportManager.getTest().log(Status.PASS, "BACK dong sheet -> Albums.");
    }
}