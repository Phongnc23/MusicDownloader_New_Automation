package testcases.albums;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.AlbumsPage;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Albums - Select Mode khi nhan giu album (TC_ALB_027..032).
 * Nhan giu 1 album -> man chon: title "N item selected", nut X, nut chon-tat-ca,
 * album card co o chon (tap toggle), bottom bar 3 action: Add to queue / Add to list / Share file.
 * (Nhan giu = select mode; tap 3 cham = edit sheet 4 action - hai luong khac nhau.)
 */
public class Albums09_Verify_SelectMode extends BaseTest {

    private AlbumsPage goAlbums(HomePage home) {
        AlbumsPage albums = new AlbumsPage();
        albums.gotoAlbumsList(home);
        Assert.assertTrue(albums.isAlbumsScreenDisplayed(), "Khong vao duoc man Albums");
        return albums;
    }

    private AlbumsPage enterSelectMode(HomePage home, AlbumsPage albums) {
        albums.longPressFirstAlbum();
        home.sleep(1200);
        Assert.assertTrue(albums.isSelectModeOpen(), "Nhan giu khong mo duoc select mode");
        return albums;
    }

    /**
     * Sau MOI test cua file nay app co the con ket o SELECT MODE -> BACK ve Albums list
     * de full regression khong bi ket man select sang module/test sau.
     * Chay TRUOC BaseTest.tearDown (subclass @AfterMethod chay truoc superclass).
     */
    @AfterMethod(alwaysRun = true)
    public void backToAlbumsList() {
        try { new AlbumsPage().gotoAlbumsList(new HomePage()); } catch (Exception ignored) {}
    }

    @Test(description = "TC_ALB_027: Nhan giu album mo man select mode")
    public void TC_ALB_027_long_press_open_select() {
        HomePage home = new HomePage();
        AlbumsPage albums = enterSelectMode(home, goAlbums(home));

        Assert.assertTrue(albums.isSelectModeActionsDisplayed(),
                "Bottom bar thieu action (Add to queue/Add to list/Share file)");
        ExtentReportManager.getTest().log(Status.PASS,
                "Nhan giu album -> select mode (title + bottom bar 3 action).");
        albums.closeSelectMode();
    }

    @Test(description = "TC_ALB_028: Tap album trong select mode chon 1 item")
    public void TC_ALB_028_tap_to_select() {
        HomePage home = new HomePage();
        AlbumsPage albums = enterSelectMode(home, goAlbums(home));

        Assert.assertEquals(albums.getSelectedCount(), 0, "Ban dau phai la 0 item selected");
        albums.tapAlbumInSelectMode(0);
        home.sleep(900);
        Assert.assertEquals(albums.getSelectedCount(), 1, "Sau khi tap phai la 1 item selected");
        ExtentReportManager.getTest().log(Status.PASS, "Tap album -> 1 item selected.");
        albums.closeSelectMode();
    }

    @Test(description = "TC_ALB_029: Chon tat ca trong select mode")
    public void TC_ALB_029_select_all() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);
        int total = albums.getAlbumsCount(); // doc truoc khi vao select mode
        enterSelectMode(home, albums);

        albums.tapSelectAll();
        home.sleep(900);
        Assert.assertEquals(albums.getSelectedCount(), total,
                "Chon tat ca khong khop tong so album (" + total + ")");
        ExtentReportManager.getTest().log(Status.PASS, "Chon tat ca -> " + total + " item selected.");
        albums.closeSelectMode();
    }

    @Test(description = "TC_ALB_030: Bottom bar co 3 action Add to queue / Add to list / Share file")
    public void TC_ALB_030_bottom_actions() {
        HomePage home = new HomePage();
        AlbumsPage albums = enterSelectMode(home, goAlbums(home));

        Assert.assertTrue(albums.isSelectModeActionsDisplayed(), "Thieu 1 trong 3 action bottom bar");
        ExtentReportManager.getTest().log(Status.PASS, "Bottom bar du 3 action.");
        albums.closeSelectMode();
    }

    @Test(description = "TC_ALB_031: Nut X dong select mode ve Albums list")
    public void TC_ALB_031_close_select() {
        HomePage home = new HomePage();
        AlbumsPage albums = enterSelectMode(home, goAlbums(home));

        albums.closeSelectMode();
        home.sleep(1000);
        Assert.assertFalse(albums.isSelectModeOpen(), "X khong dong duoc select mode");
        Assert.assertTrue(albums.isAlbumsScreenDisplayed(), "Khong ve duoc man Albums sau khi dong");
        ExtentReportManager.getTest().log(Status.PASS, "Nut X -> thoat select mode, ve Albums.");
    }

    @Test(description = "TC_ALB_032: Chon album + Add to list mo dialog Add to playlist")
    public void TC_ALB_032_select_add_to_list() {
        HomePage home = new HomePage();
        AlbumsPage albums = enterSelectMode(home, goAlbums(home));

        albums.tapAlbumInSelectMode(0);
        home.sleep(900);
        Assert.assertEquals(albums.getSelectedCount(), 1, "Chua chon duoc album truoc khi Add to list");
        albums.tapSelectAddList();
        home.sleep(1200);
        Assert.assertTrue(albums.isAddToPlaylistOpen(), "Add to list khong mo dialog Add to playlist");
        ExtentReportManager.getTest().log(Status.PASS, "Chon album + Add to list -> dialog Add to playlist.");
        home.pressBack();
    }
}