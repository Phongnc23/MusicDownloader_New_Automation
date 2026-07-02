package testcases.tracks;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.TracksPage;
import report.ExtentReportManager;

/**
 * Module: Tracks - List & Top controls.
 * TC_TRK_001..003: header/top-control/bottom-nav, list co item (title + duration), mini player sau Play all.
 */
public class Tracks01_Verify_List_And_TopControls extends BaseTest {

    private TracksPage goTracks(HomePage home) {
        TracksPage tracks = new TracksPage();
        home.tapNavTracks();
        home.waitUntil(tracks::isTracksScreenDisplayed, 6000);
        Assert.assertTrue(tracks.isTracksScreenDisplayed(), "Khong vao duoc man Tracks");
        return tracks;
    }

    @Test(description = "TC_TRK_001: Header, Play all/Shuffle va bottom nav hien thi day du")
    public void TC_TRK_001_header_topcontrols_bottomnav() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        Assert.assertTrue(tracks.areTopControlsDisplayed(), "Thieu title/Play all/Shuffle");
        Assert.assertTrue(home.isBottomNavDisplayed(), "Thieu bottom nav");
        Assert.assertTrue(tracks.getTrackCount() > 0, "Header 'N tracks' khong hop le");
        ExtentReportManager.getTest().log(Status.PASS,
                "Tracks hien header + top controls + bottom nav, so bai = " + tracks.getTrackCount());
    }

    @Test(description = "TC_TRK_002: List co item, moi item co title va duration M:SS")
    public void TC_TRK_002_list_items_title_duration() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        Assert.assertTrue(tracks.getRowCount() > 0, "List rong");
        Assert.assertTrue(tracks.firstRowHasTitleAndDuration(),
                "Row dau khong co title + duration (content-desc thieu newline/' • ')");
        Assert.assertTrue(tracks.getFirstTrackDurationSec() >= 0, "Khong doc duoc duration M:SS");
        // Truncate title dai la hanh vi hien thi (content-desc van giu full text).
        ExtentReportManager.getTest().log(Status.PASS,
                "List co item, row dau: \"" + tracks.getFirstTrackTitle() + "\", duration(s)="
                        + tracks.getFirstTrackDurationSec());
    }

    @Test(description = "TC_TRK_003: Mini player hien thi sau khi Play all")
    public void TC_TRK_003_miniplayer_after_play_all() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.tapPlayAll();
        home.sleep(2000);
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Khong thay mini player sau Play all");
        ExtentReportManager.getTest().log(Status.PASS, "Play all -> mini player hien thi.");
    }
}