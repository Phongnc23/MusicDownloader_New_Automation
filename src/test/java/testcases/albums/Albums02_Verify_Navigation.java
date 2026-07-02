package testcases.albums;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AlbumsPage;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Albums - Navigation (TC_ALB_004..006).
 */
public class Albums02_Verify_Navigation extends BaseTest {

    private AlbumsPage goAlbums(HomePage home) {
        AlbumsPage albums = new AlbumsPage();
        albums.gotoAlbumsList(home);
        Assert.assertTrue(albums.isAlbumsScreenDisplayed(), "Khong vao duoc man Albums");
        return albums;
    }

    @Test(description = "TC_ALB_004: Click album mo Album Detail (name + N songs)")
    public void TC_ALB_004_open_detail() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);

        String name = albums.getFirstAlbumName();
        albums.tapAlbumCard(0);
        home.sleep(1500);
        Assert.assertTrue(albums.isAlbumDetailOpen(name), "Khong mo duoc Album Detail cua " + name);
        Assert.assertTrue(albums.getDetailHeroSongCount() > 0, "Hero khong co so bai");
        ExtentReportManager.getTest().log(Status.PASS,
                "Mo Album Detail: \"" + albums.getDetailHeroName() + "\" - " + albums.getDetailHeroSongCount() + " songs.");
    }

    @Test(description = "TC_ALB_005: Album Detail UI - header, hero, play/shuffle, tracks section")
    public void TC_ALB_005_detail_ui() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);

        albums.tapAlbumCard(0);
        home.sleep(1500);
        Assert.assertTrue(albums.isDetailWithTracksOpen(), "Thieu Play all / Shuffle");
        Assert.assertTrue(albums.getDetailHeroSongCount() > 0, "Hero khong co so bai");
        Assert.assertTrue(albums.getDetailRowCount() > 0, "Section Tracks rong");
        ExtentReportManager.getTest().log(Status.PASS,
                "Album Detail UI day du (hero + play/shuffle + tracks=" + albums.getDetailRowCount() + ").");
    }

    @Test(description = "TC_ALB_006: Back tu Album Detail ve Albums")
    public void TC_ALB_006_back_to_albums() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);

        albums.tapAlbumCard(0);
        home.sleep(1500);
        Assert.assertTrue(albums.isDetailWithTracksOpen(), "Chua vao Album Detail");
        albums.tapDetailBack();
        home.sleep(1200);
        Assert.assertTrue(albums.isAlbumsScreenDisplayed(), "Khong ve duoc man Albums");
        ExtentReportManager.getTest().log(Status.PASS, "Back tu Album Detail -> Albums.");
    }
}