package testcases.albums;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AlbumsPage;
import pages.HomePage;
import pages.TracksPage;
import report.ExtentReportManager;

/**
 * Module: Albums - Context menus (TC_ALB_026).
 * Album Detail 3 cham = sheet 4 action album-level; track 3 cham = sheet 7 action (giong Tracks).
 */
public class Albums08_Verify_Context_Menus extends BaseTest {

    private AlbumsPage goAlbumDetail(HomePage home) {
        AlbumsPage albums = new AlbumsPage();
        albums.gotoAlbumsList(home);
        Assert.assertTrue(albums.isAlbumsScreenDisplayed(), "Khong vao duoc man Albums");
        albums.tapAlbumCard(0);
        home.sleep(1500);
        Assert.assertTrue(albums.isDetailWithTracksOpen(), "Khong mo duoc Album Detail");
        return albums;
    }

    @Test(description = "TC_ALB_026: Album Detail 3 cham = 4 action, track 3 cham = 7 action")
    public void TC_ALB_026_two_menus() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbumDetail(home);
        TracksPage tracksMenu = new TracksPage();

        // 1) 3 cham album-level -> 4 action
        albums.tapDetailMenu();
        home.sleep(1000);
        Assert.assertTrue(albums.areFourActionsDisplayed(), "Menu album-level khong du 4 action");
        Assert.assertTrue(albums.sheetHasNoExtraActions(), "Menu album-level khong duoc co Rename/Delete/File info");
        // MINH CHUNG: chup menu album-level 4 action NGAY luc nay, truoc khi dong
        ExtentReportManager.attachProof("Menu album-level 4 action - minh chung");
        albums.closeSheetViaBack();
        home.sleep(900);

        // 2) 3 cham tren track -> 7 action
        albums.openDetailTrackMenu(0);
        home.sleep(1000);
        Assert.assertTrue(tracksMenu.isTrackMenuOpen(), "Khong mo duoc menu track");
        Assert.assertTrue(tracksMenu.areAllMenuActionsDisplayed(), "Menu track khong du 7 action");
        // MINH CHUNG: chup menu track 7 action NGAY luc nay, truoc khi dong
        ExtentReportManager.attachProof("Menu track 7 action - minh chung");
        tracksMenu.closeMenuViaBack();
    }
}