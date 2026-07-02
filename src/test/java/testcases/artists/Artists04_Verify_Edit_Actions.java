package testcases.artists;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ArtistsPage;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Artists - Edit Actions (TC_ART_022..025).
 * Ghi chu: "toan bo N bai" vao queue/playlist kho verify chinh xac qua UI -> assert sheet dong + on dinh.
 */
public class Artists04_Verify_Edit_Actions extends BaseTest {

    private ArtistsPage goArtists(HomePage home) {
        ArtistsPage artists = new ArtistsPage();
        home.tapNavArtists();
        home.waitUntil(artists::isArtistsScreenDisplayed, 6000);
        Assert.assertTrue(artists.isArtistsScreenDisplayed(), "Khong vao duoc man Artists");
        return artists;
    }

    @Test(description = "TC_ART_022: Play artist bai dau phat, sheet dong")
    public void TC_ART_022_play_closes_sheet() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        artists.openArtistMenuFromList();
        home.sleep(1000);
        artists.tapSheetPlay();
        home.sleep(2000);
        Assert.assertFalse(artists.isFourActionSheetOpen(), "Play xong sheet khong dong");
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Play artist khong phat (khong co mini player)");
        ExtentReportManager.getTest().log(Status.PASS, "Play artist -> phat bai dau + sheet dong.");
    }

    @Test(description = "TC_ART_023: Add to playing queue toan bo bai vao queue")
    public void TC_ART_023_add_queue() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        int songs = artists.getFirstArtistTrackCount();
        artists.openArtistMenuFromList();
        home.sleep(1000);
        artists.tapSheetAddQueue();
        home.sleep(1500);
        Assert.assertFalse(artists.isFourActionSheetOpen(), "Add to queue xong sheet khong dong");
        Assert.assertTrue(artists.isArtistsScreenDisplayed(), "App khong on dinh sau Add to queue");
        ExtentReportManager.getTest().log(Status.PASS,
                "Add to playing queue (" + songs + " bai cua artist) - sheet dong, app on dinh.");
    }

    @Test(description = "TC_ART_024: Add to playlist mo dialog")
    public void TC_ART_024_add_playlist_dialog() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        artists.openArtistMenuFromList();
        home.sleep(1000);
        artists.tapSheetAddList();
        home.sleep(1000);
        Assert.assertTrue(artists.isAddToPlaylistOpen(), "Khong mo duoc dialog Add to playlist");
        ExtentReportManager.getTest().log(Status.PASS, "Add to playlist mo dialog.");
        home.pressBack();
    }

    @Test(description = "TC_ART_025: Add artist to My Favorite toan bo bai vao playlist")
    public void TC_ART_025_add_my_favorite() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        int songs = artists.getFirstArtistTrackCount();
        artists.openArtistMenuFromList();
        home.sleep(1000);
        artists.tapSheetAddList();
        home.sleep(1000);
        Assert.assertTrue(artists.isAddToPlaylistOpen(), "Khong mo duoc Add to playlist");
        artists.tapPlaylistByName("My Favorite");
        home.sleep(1200);
        Assert.assertFalse(artists.isAddToPlaylistOpen(), "Chon My Favorite xong dialog khong dong");
        ExtentReportManager.getTest().log(Status.PASS,
                "Add artist (" + songs + " bai) vao My Favorite - dialog dong.");
    }
}