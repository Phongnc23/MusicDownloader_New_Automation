package testcases.albums;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AlbumsPage;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Albums - Playback trong Album Detail (TC_ALB_023..025).
 */
public class Albums07_Verify_Playback extends BaseTest {

    private AlbumsPage goAlbumDetail(HomePage home) {
        AlbumsPage albums = new AlbumsPage();
        albums.gotoAlbumsList(home);
        Assert.assertTrue(albums.isAlbumsScreenDisplayed(), "Khong vao duoc man Albums");
        albums.tapAlbumCard(0);
        home.sleep(1500);
        Assert.assertTrue(albums.isDetailWithTracksOpen(), "Khong mo duoc Album Detail");
        return albums;
    }

    @Test(description = "TC_ALB_023: Play all album mini player phat")
    public void TC_ALB_023_play_all() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbumDetail(home);

        albums.tapDetailPlayAll();
        home.sleep(2200);
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Play all album khong phat");
        ExtentReportManager.getTest().log(Status.PASS, "Play all album -> mini player phat.");
    }

    @Test(description = "TC_ALB_024: Shuffle album phat")
    public void TC_ALB_024_shuffle() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbumDetail(home);

        albums.tapDetailShuffle();
        home.sleep(2200);
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Shuffle album khong phat");
        Assert.assertTrue(home.isMiniPlayerProgressAdvancing(4000), "Shuffle album khong chay");
        ExtentReportManager.getTest().log(Status.PASS, "Shuffle album -> mini player phat.");
    }

    @Test(description = "TC_ALB_025: Click track trong album phat qua mini player")
    public void TC_ALB_025_play_track() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbumDetail(home);

        albums.playDetailTrack(0);
        home.sleep(2200);
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Click track khong phat (khong co mini player)");
        ExtentReportManager.getTest().log(Status.PASS, "Click track trong album -> phat qua mini player.");
    }
}