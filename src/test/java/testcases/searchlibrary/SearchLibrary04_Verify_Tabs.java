package testcases.searchlibrary;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.SearchInLibraryPage;
import report.ExtentReportManager;

/**
 * Module: Search In Library - Tabs/Filter (TC_SL_005,006,014,015,016,017,018).
 * Trang thai tab chon KHONG doc duoc -> verify bang HIEU UNG loc.
 * Track row = co " • "; Album/Artist/Playlist row = co " tracks" (khong " • ").
 */
public class SearchLibrary04_Verify_Tabs extends BaseTest {

    private static final String Q_TRACK = "import";   // ra tracks
    private static final String Q_ALBUM = "Voice";    // ra album "VoiceChanger"
    private static final String ALBUM_NAME = "VoiceChanger";
    private static final String Q_PLAYLIST = "QA_PL"; // ra playlist QA_PL_*
    private static final String PLAYLIST_NAME = "QA_PL";
    private static final String Q_BOTH = "Music";     // khop ca track (MUSIC VIDEO) lan album (Music Download)

    private SearchInLibraryPage goSearch(HomePage home) {
        SearchInLibraryPage s = new SearchInLibraryPage();
        s.openSearch(home);
        Assert.assertTrue(s.isSearchScreenDisplayed(), "Khong mo duoc man Search In Library");
        return s;
    }

    @Test(description = "TC_SL_005: Click tab chuyen tab (man on dinh)")
    public void TC_SL_005_switch_tabs() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);

        s.tapTabTracks();   home.sleep(700);
        Assert.assertTrue(s.isSearchScreenDisplayed(), "Sau tap Tracks man khong on dinh");
        s.tapTabAlbums();   home.sleep(700);
        Assert.assertTrue(s.isSearchScreenDisplayed(), "Sau tap Albums man khong on dinh");
        s.tapTabArtists();  home.sleep(700);
        Assert.assertTrue(s.isSearchScreenDisplayed(), "Sau tap Artists man khong on dinh");
        s.tapTabPlaylists(); home.sleep(700);
        Assert.assertTrue(s.isSearchScreenDisplayed(), "Sau tap Playlists man khong on dinh");
        s.tapTabAll();      home.sleep(700);
        Assert.assertTrue(s.isSearchScreenDisplayed(), "Sau tap All man khong on dinh");
        ExtentReportManager.getTest().log(Status.PASS, "Chuyen qua lai 5 tab - man on dinh.");
    }

    @Test(description = "TC_SL_006: 5 tab van truy cap duoc khi co query")
    public void TC_SL_006_tabs_with_query() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);

        s.typeQuery(Q_TRACK);
        home.sleep(1300);
        Assert.assertTrue(s.areFilterTabsDisplayed(), "Co query nhung khong con du 5 tab");
        s.tapTabAlbums(); home.sleep(800);
        Assert.assertTrue(s.areFilterTabsDisplayed(), "Sau doi tab van phai du 5 tab");
        ExtentReportManager.getTest().log(Status.PASS, "Co query van truy cap duoc 5 tab.");
    }

    @Test(description = "TC_SL_014: Tab Tracks chi hien tracks")
    public void TC_SL_014_tab_tracks_only() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);

        s.typeQuery(Q_TRACK);
        home.sleep(1200);
        s.tapTabTracks();
        home.sleep(1200);
        Assert.assertTrue(s.hasTrackResults(), "Tab Tracks khong co track nao");
        Assert.assertFalse(s.hasCollectionResults(), "Tab Tracks lai hien row dang 'N tracks' (album/playlist)");
        ExtentReportManager.getTest().log(Status.PASS, "Tab Tracks chi hien tracks.");
    }

    @Test(description = "TC_SL_015: Tab Albums chi hien albums")
    public void TC_SL_015_tab_albums_only() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);

        s.typeQuery(Q_ALBUM);
        home.sleep(1200);
        s.tapTabAlbums();
        home.sleep(1200);
        Assert.assertTrue(s.resultContains(ALBUM_NAME), "Tab Albums khong thay '" + ALBUM_NAME + "' (sua hang so)");
        Assert.assertFalse(s.hasTrackResults(), "Tab Albums lai hien track (co ' • ')");
        ExtentReportManager.getTest().log(Status.PASS, "Tab Albums chi hien albums.");
    }

    @Test(description = "TC_SL_016: Tab Playlists chi hien playlists")
    public void TC_SL_016_tab_playlists_only() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);

        s.typeQuery(Q_PLAYLIST);
        home.sleep(1200);
        s.tapTabPlaylists();
        home.sleep(1200);
        Assert.assertTrue(s.resultContains(PLAYLIST_NAME), "Tab Playlists khong thay '" + PLAYLIST_NAME + "'");
        Assert.assertFalse(s.hasTrackResults(), "Tab Playlists lai hien track (co ' • ')");
        ExtentReportManager.getTest().log(Status.PASS, "Tab Playlists chi hien playlists.");
    }

    @Test(description = "TC_SL_017: Cung query, doi tab loc lai")
    public void TC_SL_017_change_tab_refilter() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);

        s.typeQuery(Q_BOTH);
        home.sleep(1200);
        s.tapTabTracks();
        home.sleep(1200);
        Assert.assertTrue(s.hasTrackResults(), "Tab Tracks khong ra track cho query '" + Q_BOTH + "'");

        s.tapTabAlbums();
        home.sleep(1200);
        Assert.assertFalse(s.hasTrackResults(), "Doi sang Albums nhung van con track (' • ') -> chua loc lai");
        ExtentReportManager.getTest().log(Status.PASS, "Doi tab Tracks->Albums loc lai dung (track bien mat).");
    }

    @Test(description = "TC_SL_018: Giu query khi chuyen tab")
    public void TC_SL_018_keep_query_on_tab_switch() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);

        s.typeQuery(Q_TRACK);
        home.sleep(1000);
        Assert.assertEquals(s.getQueryText().trim(), Q_TRACK, "Query nhap vao khong dung");

        s.tapTabAlbums();  home.sleep(900);
        Assert.assertEquals(s.getQueryText().trim(), Q_TRACK, "Doi tab lam mat query");
        s.tapTabPlaylists(); home.sleep(900);
        Assert.assertEquals(s.getQueryText().trim(), Q_TRACK, "Doi tab lam mat query");
        ExtentReportManager.getTest().log(Status.PASS, "Query duoc giu khi chuyen tab.");
    }
}