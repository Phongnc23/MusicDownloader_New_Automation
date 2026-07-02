package testcases.tracks;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.TracksPage;
import report.ExtentReportManager;

/**
 * Module: Tracks - Playing Queue (TC_TRK_060..062).
 */
public class Tracks08_Verify_PlayingQueue extends BaseTest {

    private TracksPage goQueue(HomePage home) {
        TracksPage tracks = new TracksPage();
        home.tapNavTracks();
        home.waitUntil(tracks::isTracksScreenDisplayed, 6000);
        Assert.assertTrue(tracks.isTracksScreenDisplayed(), "Khong vao duoc man Tracks");
        tracks.tapPlayAll(); home.sleep(2000);
        home.tapMiniPlayerQueue(); home.sleep(1400);
        Assert.assertTrue(tracks.isPlayingQueueOpen(), "Khong mo duoc Playing Queue");
        return tracks;
    }

    @Test(description = "TC_TRK_060: Playing Queue vi tri bai dang phat, header Shuffle/Repeat toggle, mini player")
    public void TC_TRK_060_queue_layout() {
        HomePage home = new HomePage();
        TracksPage tracks = goQueue(home);

        Assert.assertTrue(tracks.getQueuePosition() >= 1, "Khong doc duoc vi tri bai dang phat (c/N)");
        Assert.assertTrue(tracks.getQueueTotal() > 0, "Tong queue khong hop le");
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Queue khong co mini player");
        ExtentReportManager.getTest().log(Status.INFO,
                "Vi tri=" + tracks.getQueuePosition() + "/" + tracks.getQueueTotal());

        tracks.tapQueueShuffle(); home.sleep(800);
        Assert.assertTrue(tracks.isPlayingQueueOpen(), "Sau Shuffle header man queue bi mat");
        tracks.tapQueueRepeat(); home.sleep(800);
        Assert.assertTrue(tracks.isPlayingQueueOpen(), "Sau Repeat header man queue bi mat");
        ExtentReportManager.getTest().log(Status.PASS, "Queue hien vi tri + Shuffle/Repeat toggle + mini player.");
    }

    @Test(description = "TC_TRK_061: Remove from queue count giam 1")
    public void TC_TRK_061_remove_from_queue() {
        HomePage home = new HomePage();
        TracksPage tracks = goQueue(home);

        int before = tracks.getQueueTotal();
        Assert.assertTrue(before > 0, "Khong doc duoc tong queue ban dau");
        tracks.removeFromQueue(1);
        home.sleep(1000);
        // Sau khi xoa, Playing Queue co the dong/re-render (count label bien mat) -> mo lai neu can.
        if (!tracks.isPlayingQueueOpen() || tracks.getQueueTotal() < 0) {
            home.tapMiniPlayerQueue();
            home.waitUntil(tracks::isPlayingQueueOpen, 4000);
        }
        final int target = before - 1;
        home.waitUntil(() -> tracks.getQueueTotal() == target, 6000);
        int after = tracks.getQueueTotal();
        ExtentReportManager.getTest().log(Status.INFO, "Queue truoc=" + before + " sau=" + after);
        Assert.assertEquals(after, target, "Remove from queue khong giam 1");
        ExtentReportManager.getTest().log(Status.PASS, "Remove from queue -1 OK.");
    }
}
