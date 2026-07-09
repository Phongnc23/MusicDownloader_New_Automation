package testcases.albums;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AlbumsPage;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Albums - Edit Actions (TC_ALB_013..015).
 */
public class Albums05_Verify_Edit_Actions extends BaseTest {

    private AlbumsPage goAlbums(HomePage home) {
        AlbumsPage albums = new AlbumsPage();
        albums.gotoAlbumsList(home);
        Assert.assertTrue(albums.isAlbumsScreenDisplayed(), "Khong vao duoc man Albums");
        return albums;
    }

    @Test(description = "TC_ALB_013: Play album mini player phat")
    public void TC_ALB_013_play() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);

        albums.openAlbumMenuFromList();
        home.sleep(1000);
        albums.tapSheetPlay();
        home.sleep(2000);
        Assert.assertFalse(albums.isFourActionSheetOpen(), "Play xong sheet khong dong");
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Play album khong phat (khong co mini player)");
        ExtentReportManager.getTest().log(Status.PASS, "Play album -> mini player phat + sheet dong.");
    }

    @Test(description = "TC_ALB_014: Add to playing queue sheet dong va van o app")
    public void TC_ALB_014_add_queue() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);

        albums.openAlbumMenuFromList();
        home.sleep(1000);
        albums.tapSheetAddQueue();
        home.sleep(1500);
        Assert.assertFalse(albums.isFourActionSheetOpen(), "Add to queue xong sheet khong dong");
        Assert.assertTrue(albums.isAlbumsScreenDisplayed(), "App khong on dinh sau Add to queue");
        ExtentReportManager.getTest().log(Status.PASS, "Add to playing queue - sheet dong, app on dinh.");
    }

    @Test(description = "TC_ALB_015: Add to playlist mo dialog")
    public void TC_ALB_015_add_playlist() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);

        albums.openAlbumMenuFromList();
        home.sleep(1000);
        albums.tapSheetAddList();
        home.sleep(1000);
        Assert.assertTrue(albums.isAddToPlaylistOpen(), "Khong mo duoc dialog Add to playlist");
        // MINH CHUNG: chup dialog Add to playlist dang mo NGAY luc nay, truoc khi back
        ExtentReportManager.attachProof("Dialog Add to playlist mo - minh chung");
        home.pressBack();
    }
}