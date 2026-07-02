package testcases.artists;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ArtistsPage;
import pages.HomePage;
import pages.TracksPage;
import report.ExtentReportManager;

/**
 * Module: Artists - Navigation (TC_ART_007..016).
 */
public class Artists02_Verify_Navigation extends BaseTest {

    private ArtistsPage goArtists(HomePage home) {
        ArtistsPage artists = new ArtistsPage();
        home.tapNavArtists();
        home.waitUntil(artists::isArtistsScreenDisplayed, 6000);
        Assert.assertTrue(artists.isArtistsScreenDisplayed(), "Khong vao duoc man Artists");
        return artists;
    }

    private ArtistsPage goArtistDetail(HomePage home) {
        ArtistsPage artists = goArtists(home);
        artists.tapArtistCard(0);
        home.waitUntil(artists::isArtistDetailOpen, 6000);
        Assert.assertTrue(artists.isArtistDetailOpen(), "Khong mo duoc Artist Detail");
        return artists;
    }

    @Test(description = "TC_ART_007: Click artist mo man Artist Detail")
    public void TC_ART_007_open_detail() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        artists.tapArtistCard(0);
        home.sleep(1500);
        Assert.assertTrue(artists.isArtistDetailOpen(), "Khong chuyen sang Artist Detail");
        ExtentReportManager.getTest().log(Status.PASS, "Click artist -> Artist Detail.");
    }

    @Test(description = "TC_ART_008: Artist Detail co header, back va 3 cham")
    public void TC_ART_008_detail_header() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtistDetail(home);

        Assert.assertTrue(artists.isArtistDetailOpen(), "Header detail khong day du");
        artists.tapDetailMenu();
        home.sleep(1000);
        Assert.assertTrue(artists.isFourActionSheetOpen(), "Nut 3 cham khong mo duoc sheet");
        ExtentReportManager.getTest().log(Status.PASS, "Artist Detail co header + back + 3 cham (mo duoc sheet).");
        artists.closeSheetViaBack();
    }

    @Test(description = "TC_ART_009: Artist Detail hero co name va N songs")
    public void TC_ART_009_hero_name_songs() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtistDetail(home);

        Assert.assertFalse(artists.getDetailHeroName().isEmpty(), "Hero khong co ten");
        Assert.assertTrue(artists.getDetailHeroSongCount() > 0, "Hero khong co so bai");
        ExtentReportManager.getTest().log(Status.PASS,
                "Hero: \"" + artists.getDetailHeroName() + "\" - " + artists.getDetailHeroSongCount() + " songs");
    }

    @Test(description = "TC_ART_010: Artist Detail co Play all va Shuffle")
    public void TC_ART_010_play_shuffle() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtistDetail(home);

        Assert.assertTrue(artists.isDetailWithTracksOpen(), "Thieu Play all / Shuffle");
        ExtentReportManager.getTest().log(Status.PASS, "Artist Detail co Play all + Shuffle.");
    }

    @Test(description = "TC_ART_011: Artist Detail co section Albums va Tracks")
    public void TC_ART_011_sections() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtistDetail(home);

        Assert.assertTrue(artists.isAlbumsSectionDisplayed(), "Khong co section Albums");
        Assert.assertTrue(artists.getDetailRowCount() > 0, "Section Tracks rong");
        ExtentReportManager.getTest().log(Status.PASS, "Co section Albums + Tracks (rows=" + artists.getDetailRowCount() + ").");
    }

    @Test(description = "TC_ART_012: Back tu Artist Detail ve Artists")
    public void TC_ART_012_back_to_artists() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtistDetail(home);

        artists.tapDetailBack();
        home.sleep(1200);
        Assert.assertTrue(artists.isArtistsScreenDisplayed(), "Khong ve duoc man Artists");
        ExtentReportManager.getTest().log(Status.PASS, "Back tu Detail -> Artists.");
    }

    @Test(description = "TC_ART_013: Section Tracks co track item day du thong tin")
    public void TC_ART_013_track_item_info() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtistDetail(home);

        Assert.assertTrue(artists.firstDetailRowHasTitleAndDuration(), "Track item thieu title/duration");
        ExtentReportManager.getTest().log(Status.PASS,
                "Track item day du: \"" + artists.getFirstDetailTrackTitle() + "\".");
    }

    @Test(description = "TC_ART_014: Sort button tren section Tracks mo Sort dialog")
    public void TC_ART_014_tracks_sort_button() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtistDetail(home);

        artists.tapArtistDetailTracksSort();
        home.sleep(900);
        Assert.assertTrue(artists.isSortDialogOpen(), "Nut sort section Tracks khong mo duoc dialog");
        ExtentReportManager.getTest().log(Status.PASS, "Nut sort section Tracks hoat dong.");
        artists.closeSortViaBack();
    }

    @Test(description = "TC_ART_015: Click track trong Artist Detail phat bai")
    public void TC_ART_015_play_track() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtistDetail(home);

        artists.playDetailTrack(0);
        home.sleep(2200);
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Click track khong phat (khong co mini player)");
        ExtentReportManager.getTest().log(Status.PASS, "Click track trong Detail -> phat qua mini player.");
    }

    @Test(description = "TC_ART_016: Track edit trong artist mo sheet 7 action (giong man Tracks)")
    public void TC_ART_016_track_edit_seven_actions() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtistDetail(home);
        TracksPage tracksMenu = new TracksPage();

        artists.openDetailTrackMenu(0);
        home.sleep(1000);
        Assert.assertTrue(tracksMenu.isTrackMenuOpen(), "Khong mo duoc menu track");
        Assert.assertTrue(tracksMenu.areAllMenuActionsDisplayed(),
                "Menu track khong du 7 action (khac artist 4 action)");
        ExtentReportManager.getTest().log(Status.PASS, "Track edit trong artist = sheet 7 action.");
        tracksMenu.closeMenuViaBack();
    }
}