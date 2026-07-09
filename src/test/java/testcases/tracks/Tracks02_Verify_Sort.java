package testcases.tracks;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.TracksPage;
import report.ExtentReportManager;

/**
 * Module: Tracks - Sort dialog (TC_TRK_004..014).
 * Sort dialog: title "Sort by", 7 option, mac dinh 'Date modified'. Option ACTIVE co 1 View con
 * (mui ten chieu) -> isSortActive(name) detect bang xpath con.
 *
 * HANH VI THAT (xac nhan qua DOM): khi chon 1 option, app AP DUNG ngay va DONG dialog. Do do
 * muon check indicator phai MO LAI dialog (reopenSortDialog) de doc trang thai da luu.
 * Vi noReset luu sort cu giua cac session -> cac test set sort ro rang truoc khi assert.
 *
 * Oracle:
 *  - Indicator: mo lai dialog -> isSortActive(option) == true, cac option khac == false.
 *  - Title/Duration: so sanh title/duration row dau giua 2 chieu (toggle phai doi thu tu).
 *  - Artist/Album/File name/Date added/Date modified: metadata dong nhat / khong hien thi
 *    -> chi assert indicator dung + list con item.
 */
public class Tracks02_Verify_Sort extends BaseTest {

    private TracksPage goTracks(HomePage home) {
        TracksPage tracks = new TracksPage();
        home.tapNavTracks();
        home.waitUntil(tracks::isTracksScreenDisplayed, 6000);
        Assert.assertTrue(tracks.isTracksScreenDisplayed(), "Khong vao duoc man Tracks");
        return tracks;
    }

    /** Chon 1 option (dialog tu dong dong sau khi chon). */
    private void selectSort(HomePage home, TracksPage tracks, String option) {
        tracks.ensureSortDialogOpen();
        tracks.tapSortOption(option);
        home.sleep(600);
        if (tracks.isSortDialogOpen()) tracks.closeSortViaX();
        home.waitUntil(() -> !tracks.isSortDialogOpen(), 2000);
    }

    /** Chon option roi tra ve title row dau (sau khi list resort). */
    private String firstTitleAfterSort(HomePage home, TracksPage tracks, String option) {
        selectSort(home, tracks, option);
        home.sleep(500);
        return tracks.getFirstTrackTitle();
    }

    /** Chon option roi tra ve duration(giay) row dau. */
    private int firstDurationAfterSort(HomePage home, TracksPage tracks, String option) {
        selectSort(home, tracks, option);
        home.sleep(500);
        return tracks.getFirstTrackDurationSec();
    }

    /** Chon option, mo lai dialog, kiem indicator co o option do khong. KHONG dong sau cung. */
    private boolean activeAfterSelect(HomePage home, TracksPage tracks, String option) {
        selectSort(home, tracks, option);
        tracks.reopenSortDialog();
        return tracks.isSortActive(option);
    }

    @Test(description = "TC_TRK_004: Sort dialog mo voi 7 lua chon, set & verify 'Date modified' active")
    public void TC_TRK_004_sort_dialog_open_default() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        // noReset luu sort cu -> set ve 'Date modified' (mac dinh app) cho xac dinh, roi mo lai verify
        selectSort(home, tracks, "Date modified");
        tracks.reopenSortDialog();
        Assert.assertTrue(tracks.isSortDialogOpen(), "Khong mo duoc Sort dialog");
        Assert.assertTrue(tracks.areAllSortOptionsDisplayed(), "Thieu option sort (can du 7)");
        Assert.assertTrue(tracks.isSortActive("Date modified"), "Indicator khong o 'Date modified'");
        // MINH CHUNG: chup Sort dialog dang mo (7 option, Date modified active) truoc khi dong
        ExtentReportManager.attachProof("Sort dialog dang mo, 7 option, Date modified active - minh chung");
        tracks.closeSortViaX();
    }

    @Test(description = "TC_TRK_005: Dong Sort dialog bang X, Scrim hoac BACK")
    public void TC_TRK_005_sort_dialog_close_ways() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.ensureSortDialogOpen();
        tracks.closeSortViaX(); home.waitUntil(() -> !tracks.isSortDialogOpen(), 2500);
        Assert.assertFalse(tracks.isSortDialogOpen(), "Khong dong duoc bang X");

        tracks.ensureSortDialogOpen();
        tracks.closeSortViaScrim(); home.waitUntil(() -> !tracks.isSortDialogOpen(), 2500);
        Assert.assertFalse(tracks.isSortDialogOpen(), "Khong dong duoc bang Scrim");

        tracks.ensureSortDialogOpen();
        tracks.closeSortViaBack(); home.waitUntil(() -> !tracks.isSortDialogOpen(), 2500);
        Assert.assertFalse(tracks.isSortDialogOpen(), "Khong dong duoc bang BACK");

        ExtentReportManager.getTest().log(Status.PASS, "Dong Sort dialog bang X/Scrim/BACK deu OK.");
    }

    @Test(description = "TC_TRK_006: Sort indicator chuyen sang option duoc chon")
    public void TC_TRK_006_sort_indicator_moves() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        Assert.assertTrue(activeAfterSelect(home, tracks, "Title"), "Indicator khong sang o 'Title'");
        Assert.assertFalse(tracks.isSortActive("Date modified"), "Indicator van con o 'Date modified'");
        // MINH CHUNG: chup Sort dialog dang mo voi indicator o 'Title' truoc khi dong
        ExtentReportManager.attachProof("Sort dialog dang mo, indicator o Title - minh chung");
        tracks.closeSortViaX();
    }

    @Test(description = "TC_TRK_007: Sort theo Title - toggle tang/giam doi thu tu list")
    public void TC_TRK_007_sort_title_toggle() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        String asc = firstTitleAfterSort(home, tracks, "Title");
        String desc = firstTitleAfterSort(home, tracks, "Title"); // tap lai = toggle chieu
        ExtentReportManager.getTest().log(Status.INFO, "Title asc[0]=" + asc + " | desc[0]=" + desc);
        Assert.assertNotEquals(asc, desc, "Toggle Title khong doi thu tu (title row dau khong doi)");
        ExtentReportManager.getTest().log(Status.PASS, "Sort Title tang/giam doi thu tu OK.");
    }

    @Test(description = "TC_TRK_008: Sort theo Artist (indicator + list con item)")
    public void TC_TRK_008_sort_artist() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);
        Assert.assertTrue(activeAfterSelect(home, tracks, "Artist"), "Indicator khong o 'Artist'");
        // MINH CHUNG: chup Sort dialog dang mo voi indicator o 'Artist' truoc khi dong
        ExtentReportManager.attachProof("Sort dialog dang mo, indicator o Artist - minh chung");
        tracks.closeSortViaX(); home.sleep(500);
        Assert.assertTrue(tracks.getRowCount() > 0, "List rong sau sort Artist");
        ExtentReportManager.getTest().log(Status.PASS,
                "Sort Artist set indicator + list con item (thu tu khong verify duoc do artist dong nhat).");
    }

    @Test(description = "TC_TRK_009: Sort theo Album (indicator + list con item)")
    public void TC_TRK_009_sort_album() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);
        Assert.assertTrue(activeAfterSelect(home, tracks, "Album"), "Indicator khong o 'Album'");
        // MINH CHUNG: chup Sort dialog dang mo voi indicator o 'Album' truoc khi dong
        ExtentReportManager.attachProof("Sort dialog dang mo, indicator o Album - minh chung");
        tracks.closeSortViaX(); home.sleep(500);
        Assert.assertTrue(tracks.getRowCount() > 0, "List rong sau sort Album");
        ExtentReportManager.getTest().log(Status.PASS, "Sort Album set indicator + list con item.");
    }

    @Test(description = "TC_TRK_010: Sort theo File name (indicator + list con item)")
    public void TC_TRK_010_sort_filename() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);
        Assert.assertTrue(activeAfterSelect(home, tracks, "File name"), "Indicator khong o 'File name'");
        // MINH CHUNG: chup Sort dialog dang mo voi indicator o 'File name' truoc khi dong
        ExtentReportManager.attachProof("Sort dialog dang mo, indicator o File name - minh chung");
        tracks.closeSortViaX(); home.sleep(500);
        Assert.assertTrue(tracks.getRowCount() > 0, "List rong sau sort File name");
        ExtentReportManager.getTest().log(Status.PASS,
                "Sort File name set indicator + list con item (file name khong hien thi de verify thu tu).");
    }

    @Test(description = "TC_TRK_011: Sort theo Duration - toggle doi thu tu (verify bang duration giay)")
    public void TC_TRK_011_sort_duration_toggle() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        int d1 = firstDurationAfterSort(home, tracks, "Duration");
        int d2 = firstDurationAfterSort(home, tracks, "Duration"); // toggle chieu

        ExtentReportManager.getTest().log(Status.INFO, "Duration chieu1[0]=" + d1 + "s | chieu2[0]=" + d2 + "s");
        Assert.assertTrue(d1 >= 0 && d2 >= 0, "Khong doc duoc duration");
        Assert.assertNotEquals(d1, d2, "Toggle Duration khong doi thu tu (duration row dau khong doi)");
        ExtentReportManager.getTest().log(Status.PASS,
                "Sort Duration toggle doi thu tu (min=" + Math.min(d1, d2) + "s, max=" + Math.max(d1, d2) + "s).");
    }

    @Test(description = "TC_TRK_012: Sort theo Date added (indicator + list con item)")
    public void TC_TRK_012_sort_date_added() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);
        Assert.assertTrue(activeAfterSelect(home, tracks, "Date added"), "Indicator khong o 'Date added'");
        // MINH CHUNG: chup Sort dialog dang mo voi indicator o 'Date added' truoc khi dong
        ExtentReportManager.attachProof("Sort dialog dang mo, indicator o Date added - minh chung");
        tracks.closeSortViaX(); home.sleep(500);
        Assert.assertTrue(tracks.getRowCount() > 0, "List rong sau sort Date added");
        ExtentReportManager.getTest().log(Status.PASS, "Sort Date added set indicator + list con item.");
    }

    @Test(description = "TC_TRK_013: Sort theo Date modified (indicator + list con item)")
    public void TC_TRK_013_sort_date_modified() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);
        Assert.assertTrue(activeAfterSelect(home, tracks, "Date modified"), "Indicator khong o 'Date modified'");
        // MINH CHUNG: chup Sort dialog dang mo voi indicator o 'Date modified' truoc khi dong
        ExtentReportManager.attachProof("Sort dialog dang mo, indicator o Date modified - minh chung");
        tracks.closeSortViaX(); home.sleep(500);
        Assert.assertTrue(tracks.getRowCount() > 0, "List rong sau sort Date modified");
        ExtentReportManager.getTest().log(Status.PASS, "Sort Date modified set indicator + list con item.");
    }

    @Test(description = "TC_TRK_014: Doi sort - thu tu theo Title khac theo Duration")
    public void TC_TRK_014_title_order_differs_from_duration() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        String byTitle = firstTitleAfterSort(home, tracks, "Title");
        String byDuration = firstTitleAfterSort(home, tracks, "Duration");
        ExtentReportManager.getTest().log(Status.INFO, "Title[0]=" + byTitle + " | Duration[0]=" + byDuration);
        Assert.assertNotEquals(byTitle, byDuration, "Thu tu Title va Duration giong nhau (bat thuong)");
        ExtentReportManager.getTest().log(Status.PASS, "Doi sort thay thu tu list thay doi.");
    }
}
