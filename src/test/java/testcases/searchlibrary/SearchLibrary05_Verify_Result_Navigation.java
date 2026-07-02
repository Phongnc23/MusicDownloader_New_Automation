package testcases.searchlibrary;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AlbumsPage;
import pages.HomePage;
import pages.PlaylistsPage;
import pages.SearchInLibraryPage;
import report.ExtentReportManager;

/**
 * Module: Search In Library - Result Navigation (TC_SL_019,020,021).
 * 019: click track result -> phat hoac roi man search.
 * 020: click album result -> mo Album Detail.
 * 021: click playlist result -> mo Playlist Detail.
 */
public class SearchLibrary05_Verify_Result_Navigation extends BaseTest {

    private static final String Q_TRACK = "import";
    private static final String Q_ALBUM = "Voice";
    private static final String ALBUM_NAME = "VoiceChanger";
    private static final String Q_PLAYLIST = "QA_PL";
    private static final String PLAYLIST_NAME = "QA_PL_6710";

    private SearchInLibraryPage goSearch(HomePage home) {
        SearchInLibraryPage s = new SearchInLibraryPage();
        s.openSearch(home);
        Assert.assertTrue(s.isSearchScreenDisplayed(), "Khong mo duoc man Search In Library");
        return s;
    }

    @Test(description = "TC_SL_019: Click track result phat hoac roi search")
    public void TC_SL_019_click_track_result() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);

        s.typeQuery(Q_TRACK);
        home.sleep(1300);
        s.tapTabTracks();
        home.sleep(1000);
        Assert.assertTrue(s.hasTrackResults(), "Khong co track result de click");
        s.tapFirstTrackResult();
        home.sleep(1800);
        // App phat INLINE ngay tren man search (khong co full player); mini player nam duoi day
        // -> ban phim (dang mo tu luc go query) che mat -> phai an ban phim truoc khi kiem tra.
        home.hideKeyboardSafe();
        home.sleep(800);

        boolean played = home.isMiniPlayerDisplayed();
        boolean leftSearch = !s.isSearchScreenDisplayed();
        Assert.assertTrue(played || leftSearch, "Click track khong phat va cung khong roi man search");
        ExtentReportManager.getTest().log(Status.PASS,
                "Click track result -> " + (played ? "dang phat (mini player)" : "da roi man search") + ".");
    }

    @Test(description = "TC_SL_020: Click album result mo Album Detail")
    public void TC_SL_020_click_album_result() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);
        AlbumsPage albums = new AlbumsPage();

        s.typeQuery(Q_ALBUM);
        home.sleep(1200);
        s.tapTabAlbums();
        home.sleep(1200);
        Assert.assertTrue(s.resultContains(ALBUM_NAME), "Khong thay album '" + ALBUM_NAME + "' de click");
        s.tapResultByName(ALBUM_NAME);
        home.sleep(1500);

        Assert.assertTrue(albums.isDetailWithTracksOpen(), "Click album result khong mo Album Detail");
        ExtentReportManager.getTest().log(Status.PASS, "Click album result -> mo Album Detail.");
    }

    @Test(description = "TC_SL_021: Click playlist result mo Playlist Detail")
    public void TC_SL_021_click_playlist_result() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);
        PlaylistsPage pl = new PlaylistsPage();

        s.typeQuery(Q_PLAYLIST);
        home.sleep(1200);
        s.tapTabPlaylists();
        home.sleep(1200);
        Assert.assertTrue(s.resultContains(PLAYLIST_NAME), "Khong thay playlist '" + PLAYLIST_NAME + "' de click");
        s.tapResultByName(PLAYLIST_NAME);
        home.sleep(1500);

        Assert.assertTrue(pl.isDetailWithControlsOpen(), "Click playlist result khong mo Playlist Detail");
        ExtentReportManager.getTest().log(Status.PASS, "Click playlist result -> mo Playlist Detail.");
    }
}