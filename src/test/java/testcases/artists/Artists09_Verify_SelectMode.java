package testcases.artists;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.ArtistsPage;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Artists - Select Mode khi nhan giu artist (TC_ART_048..053).
 * Nhan giu 1 artist -> mo man chon: title "N item selected", nut X (dong), nut chon-tat-ca,
 * artist card co o chon (tap de toggle), bottom bar 3 action: Add to queue / Add to list / Share file.
 * (Luu y: nhan giu = select mode; con tap 3 cham = edit sheet 4 action - hai luong khac nhau.)
 */
public class Artists09_Verify_SelectMode extends BaseTest {

    private ArtistsPage goArtists(HomePage home) {
        ArtistsPage artists = new ArtistsPage();
        home.tapNavArtists();
        home.waitUntil(artists::isArtistsScreenDisplayed, 6000);
        Assert.assertTrue(artists.isArtistsScreenDisplayed(), "Khong vao duoc man Artists");
        return artists;
    }

    private ArtistsPage enterSelectMode(HomePage home) {
        ArtistsPage artists = goArtists(home);
        artists.longPressFirstArtist();
        home.sleep(1200);
        Assert.assertTrue(artists.isSelectModeOpen(), "Nhan giu khong mo duoc select mode");
        return artists;
    }

    /**
     * Sau MOI test cua file nay app co the con ket o SELECT MODE (TC_ART_053 ket trong select mode)
     * -> BACK thoat roi ve Artists list. Tranh module sau (Search) khoi dau o man select khong co
     * bottom nav. Chay TRUOC BaseTest.tearDown (subclass @AfterMethod chay truoc superclass).
     */
    @AfterMethod(alwaysRun = true)
    public void backToArtistsList() {
        try {
            ArtistsPage artists = new ArtistsPage();
            HomePage home = new HomePage();
            for (int i = 0; i < 4 && artists.isSelectModeOpen(); i++) { home.pressBack(); home.sleep(500); }
            home.tapNavArtists();
        } catch (Exception ignored) {}
    }

    @Test(description = "TC_ART_048: Nhan giu artist mo man select mode")
    public void TC_ART_048_long_press_open_select() {
        HomePage home = new HomePage();
        ArtistsPage artists = enterSelectMode(home);

        Assert.assertTrue(artists.isSelectModeActionsDisplayed(),
                "Bottom bar thieu action (Add to queue/Add to list/Share file)");
        // MINH CHUNG: select mode mo (title + bottom bar 3 action), chup truoc khi thoat
        ExtentReportManager.attachProof("Select mode mo - title + bottom bar 3 action - minh chung");
        artists.closeSelectMode();
    }

    @Test(description = "TC_ART_049: Tap artist trong select mode chon 1 item")
    public void TC_ART_049_tap_to_select() {
        HomePage home = new HomePage();
        ArtistsPage artists = enterSelectMode(home);

        Assert.assertEquals(artists.getSelectedCount(), 0, "Ban dau phai la 0 item selected");
        artists.tapArtistInSelectMode(0);
        home.sleep(900);
        Assert.assertEquals(artists.getSelectedCount(), 1, "Sau khi tap phai la 1 item selected");
        // MINH CHUNG: da chon 1 item trong select mode, chup truoc khi thoat
        ExtentReportManager.attachProof("Select mode - 1 item selected - minh chung");
        artists.closeSelectMode();
    }

    @Test(description = "TC_ART_050: Chon tat ca trong select mode")
    public void TC_ART_050_select_all() {
        HomePage home = new HomePage();
        ArtistsPage artists = enterSelectMode(home);

        int total = artists.getArtistCardCount();
        artists.tapSelectAll();
        home.sleep(900);
        Assert.assertEquals(artists.getSelectedCount(), total,
                "Chon tat ca khong khop so artist (" + total + ")");
        // MINH CHUNG: da chon tat ca trong select mode, chup truoc khi thoat
        ExtentReportManager.attachProof("Select mode - chon tat ca " + total + " item - minh chung");
        artists.closeSelectMode();
    }

    @Test(description = "TC_ART_051: Bottom bar co 3 action Add to queue / Add to list / Share file")
    public void TC_ART_051_bottom_actions() {
        HomePage home = new HomePage();
        ArtistsPage artists = enterSelectMode(home);

        Assert.assertTrue(artists.isSelectModeActionsDisplayed(), "Thieu 1 trong 3 action bottom bar");
        // MINH CHUNG: bottom bar du 3 action trong select mode, chup truoc khi thoat
        ExtentReportManager.attachProof("Select mode - bottom bar 3 action (Add queue/Add list/Share file) - minh chung");
        artists.closeSelectMode();
    }

    @Test(description = "TC_ART_052: Nut X dong select mode ve Artists list")
    public void TC_ART_052_close_select() {
        HomePage home = new HomePage();
        ArtistsPage artists = enterSelectMode(home);

        artists.closeSelectMode();
        home.sleep(1000);
        Assert.assertFalse(artists.isSelectModeOpen(), "X khong dong duoc select mode");
        Assert.assertTrue(artists.isArtistsScreenDisplayed(), "Khong ve duoc man Artists sau khi dong");
        ExtentReportManager.getTest().log(Status.PASS, "Nut X -> thoat select mode, ve Artists.");
    }

    @Test(description = "TC_ART_053: Chon artist + Add to list mo dialog Add to playlist")
    public void TC_ART_053_select_add_to_list() {
        HomePage home = new HomePage();
        ArtistsPage artists = enterSelectMode(home);

        artists.tapArtistInSelectMode(0);
        home.sleep(900);
        Assert.assertEquals(artists.getSelectedCount(), 1, "Chua chon duoc artist truoc khi Add to list");
        artists.tapSelectAddList();
        home.sleep(1200);
        Assert.assertTrue(artists.isAddToPlaylistOpen(), "Add to list khong mo dialog Add to playlist");
        // MINH CHUNG: dialog Add to playlist mo tu select mode, chup truoc khi back
        ExtentReportManager.attachProof("Dialog Add to playlist mo tu select mode - minh chung");
        home.pressBack();
    }
}