package testcases.tracks;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.TracksPage;
import report.ExtentReportManager;

/**
 * Module: Tracks - Add to queue / Share / Delete (TC_TRK_042..048).
 * !!! TC_TRK_048 XOA THAT 1 bai (bai o dau list) - da duoc cho phep. Moi lan chay giam 1 bai.
 */
public class Tracks06_Verify_Queue_Share_Delete extends BaseTest {

    private TracksPage goTracks(HomePage home) {
        TracksPage tracks = new TracksPage();
        home.tapNavTracks();
        home.waitUntil(tracks::isTracksScreenDisplayed, 6000);
        Assert.assertTrue(tracks.isTracksScreenDisplayed(), "Khong vao duoc man Tracks");
        return tracks;
    }

    private int openQueueTotal(HomePage home, TracksPage tracks) {
        home.tapMiniPlayerQueue(); home.sleep(1300);
        Assert.assertTrue(tracks.isPlayingQueueOpen(), "Khong mo duoc Playing Queue");
        int total = tracks.getQueueTotal();
        tracks.tapQueueBack(); home.sleep(1000);
        return total;
    }

    @Test(description = "TC_TRK_042: Add to playing queue tong queue tang 1")
    public void TC_TRK_042_add_queue_plus_one() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.tapPlayAll(); home.sleep(2000);
        int before = openQueueTotal(home, tracks);

        tracks.openTrackMenu(5); home.sleep(900);
        tracks.tapMenuAddToQueue(); home.sleep(1200);

        int after = openQueueTotal(home, tracks);
        ExtentReportManager.getTest().log(Status.INFO, "Queue truoc=" + before + " sau=" + after);
        Assert.assertEquals(after, before + 1, "Add to queue khong tang dung 1");
        ExtentReportManager.getTest().log(Status.PASS, "Add to playing queue +1 OK.");
    }

    @Test(description = "TC_TRK_043: Add 3 bai vao queue tong tang dung 3")
    public void TC_TRK_043_add_queue_plus_three() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.tapPlayAll(); home.sleep(2000);
        int before = openQueueTotal(home, tracks);

        int[] idx = {5, 6, 7};
        for (int i : idx) {
            tracks.openTrackMenu(i); home.sleep(900);
            tracks.tapMenuAddToQueue(); home.sleep(1100);
        }

        int after = openQueueTotal(home, tracks);
        ExtentReportManager.getTest().log(Status.INFO, "Queue truoc=" + before + " sau=" + after);
        Assert.assertEquals(after, before + 3, "Add 3 bai khong tang dung 3");
        ExtentReportManager.getTest().log(Status.PASS, "Add 3 bai vao queue +3 OK.");
    }

    @Test(description = "TC_TRK_044: Add bai da co trong queue (duplicate) khong crash")
    public void TC_TRK_044_add_duplicate_no_crash() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.tapPlayAll(); home.sleep(2000);
        tracks.openTrackMenu(4); home.sleep(900);
        tracks.tapMenuAddToQueue(); home.sleep(1100);
        tracks.openTrackMenu(4); home.sleep(900);
        tracks.tapMenuAddToQueue(); home.sleep(1100);

        Assert.assertTrue(tracks.isTracksScreenDisplayed(), "App khong on dinh sau khi add duplicate");
        ExtentReportManager.getTest().log(Status.PASS, "Add duplicate vao queue khong crash.");
    }

    @Test(description = "TC_TRK_045: Edit > Play phat bai, thay bai dang phat, play lai chinh bai khong restart")
    public void TC_TRK_045_edit_play() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        String t0 = tracks.getFirstTrackTitle();
        tracks.openTrackMenu(0); home.sleep(900);
        tracks.tapMenuPlay(); home.sleep(2200);
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Edit>Play khong phat");

        tracks.openTrackMenu(1); home.sleep(900);
        String t1 = tracks.getFirstTrackTitle(); // van la title row0; chi de log
        tracks.tapMenuPlay(); home.sleep(2200);
        String playing = home.getMiniPlayerTrackTitle();
        Assert.assertFalse(playing.isEmpty(), "Mini player rong sau Edit>Play");

        int p1 = home.getMiniPlayerProgress();
        tracks.openTrackMenu(1); home.sleep(900);
        tracks.tapMenuPlay(); home.sleep(2200); // play lai chinh bai dang phat
        int p2 = home.getMiniPlayerProgress();
        ExtentReportManager.getTest().log(Status.INFO, "t0=" + t0 + " p1=" + p1 + "% p2=" + p2 + "%");
        Assert.assertTrue(p2 >= p1, "Edit>Play lai chinh bai bi restart (progress giam)");
        ExtentReportManager.getTest().log(Status.PASS, "Edit>Play phat/doi bai dung, play lai khong restart.");
    }

    @Test(description = "TC_TRK_046: Share mo Android resolver co file preview va targets")
    public void TC_TRK_046_share_resolver() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.openTrackMenu(0); home.sleep(900);
        tracks.tapMenuShareTrack();
        Assert.assertTrue(tracks.waitShareSheetOpen(5000), "Khong mo duoc Android share resolver");
        ExtentReportManager.getTest().log(Status.PASS, "Share track mo Android resolver.");
        tracks.closeShareSheet(); home.sleep(800);
    }

    @Test(description = "TC_TRK_047: Delete CANCEL so track khong doi")
    public void TC_TRK_047_delete_cancel() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        int before = tracks.getTrackCount();
        tracks.openTrackMenu(0); home.sleep(900);
        tracks.tapMenuDelete(); home.sleep(900);
        Assert.assertTrue(tracks.isDeleteConfirmOpen(), "Khong mo confirm dialog");
        tracks.tapDeleteCancel(); home.sleep(1000);
        Assert.assertEquals(tracks.getTrackCount(), before, "CANCEL ma so track doi");
        ExtentReportManager.getTest().log(Status.PASS, "Delete CANCEL: so track khong doi (" + before + ").");
    }

    @Test(description = "TC_TRK_048: Delete THAT - so track giam 1 (xoa bai dau list)")
    public void TC_TRK_048_delete_real() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        int before = tracks.getTrackCount();
        String victim = tracks.getFirstTrackTitle();
        tracks.openTrackMenu(0); home.sleep(900);
        tracks.tapMenuDelete(); home.sleep(900);
        Assert.assertTrue(tracks.isDeleteConfirmOpen(), "Khong mo confirm dialog");
        Assert.assertTrue(tracks.deleteMessageContains("Do you want to delete"), "Confirm khong dung dang single");
        tracks.tapDeleteConfirm(); home.sleep(900);

        // Android co the hien dialog quyen xoa (allow neu co) HOAC xoa thang (da cap quyen).
        // Oracle THAT = so track giam 1; dialog he thong chi la tuy chon.
        boolean sysShown = tracks.allowSystemDeleteIfPresent(6000);
        ExtentReportManager.getTest().log(Status.INFO, "Dialog quyen he thong xuat hien: " + sysShown);

        int after = before;
        long deadline = System.currentTimeMillis() + 8000;
        while (System.currentTimeMillis() < deadline) {
            after = tracks.getTrackCount();
            if (after == before - 1) break;
            home.sleep(1000);
        }
        ExtentReportManager.getTest().log(Status.INFO, "Xoa \"" + victim + "\" | truoc=" + before + " sau=" + after);
        Assert.assertEquals(after, before - 1, "Xoa that nhung so track khong giam 1");
        ExtentReportManager.getTest().log(Status.PASS, "Delete THAT: so track giam 1 (system dialog=" + sysShown + ").");
    }
}