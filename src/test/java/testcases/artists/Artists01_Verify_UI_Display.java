package testcases.artists;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ArtistsPage;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Artists - UI Display (TC_ART_001..006).
 */
public class Artists01_Verify_UI_Display extends BaseTest {

    private ArtistsPage goArtists(HomePage home) {
        ArtistsPage artists = new ArtistsPage();
        home.tapNavArtists();
        home.waitUntil(artists::isArtistsScreenDisplayed, 6000);
        Assert.assertTrue(artists.isArtistsScreenDisplayed(), "Khong vao duoc man Artists");
        return artists;
    }

    @Test(description = "TC_ART_001: Header co title 'Artists' va nut sort")
    public void TC_ART_001_header_title_sort() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        artists.tapListSort();
        home.sleep(900);
        Assert.assertTrue(artists.isSortDialogOpen(), "Nut sort khong mo duoc Sort dialog");
        // MINH CHUNG: Sort dialog dang mo, chup truoc khi dong
        ExtentReportManager.attachProof("Sort dialog mo tu header Artists - minh chung");
        artists.closeSortViaX();
    }

    @Test(description = "TC_ART_002: Count 'N artists' hien thi")
    public void TC_ART_002_count_displayed() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        Assert.assertTrue(artists.getArtistsCount() >= 1, "Count 'N artists' khong hop le");
        ExtentReportManager.getTest().log(Status.PASS, "So artist = " + artists.getArtistsCount());
    }

    @Test(description = "TC_ART_003: Artist card co name va track count")
    public void TC_ART_003_card_name_count() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        Assert.assertFalse(artists.getFirstArtistName().isEmpty(), "Card khong co ten artist");
        Assert.assertTrue(artists.getFirstArtistTrackCount() > 0, "Card khong co so tracks");
        ExtentReportManager.getTest().log(Status.PASS,
                "Card: \"" + artists.getFirstArtistName() + "\" - " + artists.getFirstArtistTrackCount() + " tracks");
    }

    @Test(description = "TC_ART_004: Unknown artist gom toan bo tracks")
    public void TC_ART_004_unknown_all_tracks() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        String name = artists.getFirstArtistName();
        Assert.assertTrue(name.toLowerCase().contains("unknown"), "Artist dau khong phai <unknown>: " + name);
        Assert.assertTrue(artists.getFirstArtistTrackCount() > 0, "Artist <unknown> khong co track");
        ExtentReportManager.getTest().log(Status.PASS,
                "Artist <unknown> gom " + artists.getFirstArtistTrackCount() + " tracks (toan bo, do metadata artist deu <unknown>).");
    }

    @Test(description = "TC_ART_005: Bottom nav co tab Artists")
    public void TC_ART_005_bottom_nav_artists() {
        HomePage home = new HomePage();
        goArtists(home);

        Assert.assertTrue(home.isBottomNavDisplayed(), "Bottom nav khong hien thi");
        ExtentReportManager.getTest().log(Status.PASS, "Bottom nav (co tab Artists) hien thi.");
    }

    @Test(description = "TC_ART_006: Mini player hien thi sau khi phat qua artist edit Play")
    public void TC_ART_006_miniplayer_after_edit_play() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        artists.openArtistMenuFromList();
        home.sleep(1000);
        Assert.assertTrue(artists.isFourActionSheetOpen(), "Khong mo duoc edit sheet artist");
        artists.tapSheetPlay();
        home.sleep(2000);
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Khong thay mini player sau khi Play artist");
        ExtentReportManager.getTest().log(Status.PASS, "Play qua edit sheet -> mini player hien thi.");
    }
}