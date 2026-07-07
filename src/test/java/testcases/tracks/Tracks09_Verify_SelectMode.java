package testcases.tracks;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.TracksPage;
import report.ExtentReportManager;

/**
 * Module: Tracks - Select mode (TC_TRK_063..067) + cac case BO SUNG (TC_TRK_068..071):
 * khi CHUA chon item nao ma nhan cac action o toolbar duoi -> khong co tac dung (no-op).
 *
 * !!! TC_TRK_066 XOA THAT cac bai da chon - da duoc cho phep.
 */
public class Tracks09_Verify_SelectMode extends BaseTest {

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

    /** Vao select mode va dam bao dang co 0 item duoc chon. */
    private TracksPage enterSelectZero(HomePage home) {
        TracksPage tracks = goTracks(home);
        tracks.longPressTrackToSelect(0);
        home.sleep(1100);
        Assert.assertTrue(tracks.isSelectModeActive(), "Khong vao duoc Select mode");
        if (tracks.getSelectedCount() > 0) { tracks.selectTrack(0); home.sleep(600); }
        Assert.assertEquals(tracks.getSelectedCount(), 0, "Khong dua ve 0 item selected duoc");
        return tracks;
    }

    /**
     * Sau MOI test cua file nay app co the con ket o SELECT MODE (TC_068..071 ket thuc trong
     * select mode) -> BACK thoat roi ve Tracks list. Tranh module sau (Artists) khoi dau o man
     * select khong co bottom nav. Chay TRUOC BaseTest.tearDown (subclass @AfterMethod chay truoc).
     */
    @AfterMethod(alwaysRun = true)
    public void backToTracksList() {
        try {
            TracksPage tracks = new TracksPage();
            HomePage home = new HomePage();
            for (int i = 0; i < 4 && tracks.isSelectModeActive(); i++) { home.pressBack(); home.sleep(500); }
            home.tapNavTracks();
        } catch (Exception ignored) {}
    }

    @Test(description = "TC_TRK_063: Select mode - 4 action, label, chon them tang count, select all")
    public void TC_TRK_063_select_mode_basics() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.longPressTrackToSelect(0); home.sleep(1100);
        Assert.assertTrue(tracks.isSelectModeActive(), "Khong vao Select mode");
        Assert.assertTrue(tracks.areSelectActionsDisplayed(), "Thieu 4 action o toolbar select");

        int c0 = tracks.getSelectedCount();
        tracks.selectTrack(1); home.sleep(400);
        tracks.selectTrack(2); home.sleep(400);
        int c1 = tracks.getSelectedCount();
        Assert.assertTrue(c1 > c0, "Chon them item nhung count khong tang (c0=" + c0 + " c1=" + c1 + ")");

        tracks.tapSelectAll(); home.sleep(800);
        int c2 = tracks.getSelectedCount();
        Assert.assertTrue(c2 > c1, "Select all khong tang count (c1=" + c1 + " c2=" + c2 + ")");
        ExtentReportManager.getTest().log(Status.PASS,
                "Select mode: 4 action + label, chon them tang count (" + c0 + "->" + c1 + "), select all (" + c2 + ").");
    }

    @Test(description = "TC_TRK_064: Thoat select mode ve Tracks")
    public void TC_TRK_064_exit_select_mode() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.longPressTrackToSelect(0); home.sleep(1100);
        Assert.assertTrue(tracks.isSelectModeActive(), "Khong vao Select mode");
        tracks.exitSelectMode(); home.sleep(900);
        Assert.assertFalse(tracks.isSelectModeActive(), "Khong thoat duoc Select mode");
        Assert.assertTrue(tracks.isTracksScreenDisplayed(), "Khong ve man Tracks");
        ExtentReportManager.getTest().log(Status.PASS, "Thoat Select mode ve Tracks.");
    }

    @Test(description = "TC_TRK_065: Select va Add to queue tong queue tang dung so da chon")
    public void TC_TRK_065_select_add_queue() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.tapPlayAll(); home.sleep(2000);
        int before = openQueueTotal(home, tracks);

        tracks.longPressTrackToSelect(0); home.sleep(1100);
        tracks.selectTrack(1); home.sleep(400);
        tracks.selectTrack(2); home.sleep(400);
        int sel = tracks.getSelectedCount();
        Assert.assertTrue(sel > 0, "Chua chon duoc item nao");
        tracks.tapSelAddToQueue(); home.sleep(1300);
        // Thoat select mode neu con (de hien mini player cho openQueueTotal).
        if (tracks.isSelectModeActive()) { tracks.exitSelectMode(); home.sleep(800); }

        int after = openQueueTotal(home, tracks);
        ExtentReportManager.getTest().log(Status.INFO, "Chon=" + sel + " | queue truoc=" + before + " sau=" + after);
        Assert.assertEquals(after, before + sel, "Queue khong tang dung so bai da chon");
        ExtentReportManager.getTest().log(Status.PASS, "Select + Add to queue tang dung " + sel + " bai.");
    }

    @Test(description = "TC_TRK_066: Select va Delete file THAT - so track giam dung so da chon")
    public void TC_TRK_066_select_delete_real() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        int before = tracks.getTrackCount();
        tracks.longPressTrackToSelect(0); home.sleep(1100);
        tracks.selectTrack(1); home.sleep(400);
        tracks.selectTrack(2); home.sleep(400);
        int sel = tracks.getSelectedCount();
        Assert.assertTrue(sel > 0, "Chua chon duoc item nao");

        tracks.tapSelDeleteFile(); home.sleep(900);
        Assert.assertTrue(tracks.isDeleteConfirmOpen(), "Khong mo confirm dialog");
        Assert.assertTrue(tracks.deleteMessageContains("song(s)"), "Confirm khong dung dang multi ('these N song(s)?')");
        tracks.tapDeleteConfirm(); home.sleep(900);

        // Android co the hien dialog quyen xoa (allow neu co) HOAC xoa thang.
        boolean sysShown = tracks.allowSystemDeleteIfPresent(6000);
        ExtentReportManager.getTest().log(Status.INFO, "Dialog quyen he thong: " + sysShown);
        home.sleep(1500);
        // Sau khi xoa, select mode co the CON active (header "N item selected" -> getTrackCount=-1).
        // Thoat select mode de hien lai header "N tracks".
        if (tracks.isSelectModeActive()) { tracks.exitSelectMode(); home.sleep(800); }

        // List re-render -> poll cho header "N tracks" ve before-sel.
        final int target = before - sel;
        home.waitUntil(() -> tracks.getTrackCount() == target, 10000);
        int after = tracks.getTrackCount();
        ExtentReportManager.getTest().log(Status.INFO, "Chon=" + sel + " | truoc=" + before + " sau=" + after);
        Assert.assertEquals(after, target, "Xoa that nhung so track khong giam dung so da chon");
        ExtentReportManager.getTest().log(Status.PASS, "Select + Delete THAT giam dung " + sel + " bai.");
    }

    @Test(description = "TC_TRK_067: Select Delete - xac nhan xoa (co dialog quyen thi Allow, khong co thi xoa thang) -> so track GIAM")
    public void TC_TRK_067_select_delete_deny() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        int before = tracks.getTrackCount();
        tracks.longPressTrackToSelect(0); home.sleep(1100);
        tracks.selectTrack(1); home.sleep(400);
        int sel = tracks.getSelectedCount();
        Assert.assertTrue(sel > 0, "Chua chon duoc item nao");

        tracks.tapSelDeleteFile(); home.sleep(900);
        Assert.assertTrue(tracks.isDeleteConfirmOpen(), "Khong mo confirm dialog");
        tracks.tapDeleteConfirm(); home.sleep(900);
        // Logic: TAP TRUNG vao viec file CO BI XOA (so track giam) hay khong, KHONG quan trong
        // co hien dialog quyen he thong hay khong.
        //  - HIEN dialog quyen -> Allow (xac nhan xoa).
        //  - KHONG hien (file app tu tao, Android xoa thang) -> cung dung, khong lam gi them.
        boolean sysShown = tracks.waitSystemDeleteOpen(5000);
        if (sysShown) { tracks.systemDeleteAllow(); home.sleep(1500); }
        ExtentReportManager.getTest().log(Status.INFO, "Dialog quyen he thong: " + sysShown + " (Allow neu co)");
        // Thoat select mode de header hien lai "N tracks".
        if (tracks.isSelectModeActive()) { tracks.exitSelectMode(); home.sleep(800); }

        // Xoa thanh cong -> count GIAM. Poll cho header cap nhat.
        home.waitUntil(() -> { int c = tracks.getTrackCount(); return c >= 0 && c < before; }, 6000);
        int after = tracks.getTrackCount();
        ExtentReportManager.getTest().log(Status.INFO, "truoc=" + before + " sau=" + after);
        Assert.assertTrue(after < before,
                "Xoa xong ma so track KHONG giam (truoc=" + before + " sau=" + after + ")");
        ExtentReportManager.getTest().log(Status.PASS, "Delete thanh cong (co/khong co dialog quyen) -> so track giam.");
    }

    // ===================== CASE BO SUNG: 0 item selected + nhan action =====================

    @Test(description = "TC_TRK_068: 0 item selected + Add to queue -> khong tac dung")
    public void TC_TRK_068_zero_add_queue_noop() {
        HomePage home = new HomePage();
        TracksPage tracks = enterSelectZero(home);

        tracks.tapSelAddToQueue(); home.sleep(1000);
        Assert.assertTrue(tracks.isSelectModeActive(), "0 selected + Add to queue ma da roi Select mode");
        Assert.assertEquals(tracks.getSelectedCount(), 0, "0 selected + Add to queue ma count thay doi");
        ExtentReportManager.getTest().log(Status.PASS, "0 selected + Add to queue = no-op.");
    }

    @Test(description = "TC_TRK_069: 0 item selected + Add to list -> khong mo Add to playlist")
    public void TC_TRK_069_zero_add_list_noop() {
        HomePage home = new HomePage();
        TracksPage tracks = enterSelectZero(home);

        tracks.tapSelAddToList(); home.sleep(1000);
        Assert.assertFalse(tracks.isAddToPlaylistOpen(), "0 selected + Add to list ma van mo Add to playlist");
        Assert.assertTrue(tracks.isSelectModeActive(), "0 selected + Add to list ma da roi Select mode");
        ExtentReportManager.getTest().log(Status.PASS, "0 selected + Add to list = no-op.");
    }

    @Test(description = "TC_TRK_070: 0 item selected + Share file -> khong mo share resolver")
    public void TC_TRK_070_zero_share_noop() {
        HomePage home = new HomePage();
        TracksPage tracks = enterSelectZero(home);

        tracks.tapSelShareFile(); home.sleep(1500);
        Assert.assertFalse(tracks.isShareSheetOpen(), "0 selected + Share file ma van mo share resolver");
        Assert.assertTrue(tracks.isSelectModeActive(), "0 selected + Share file ma da roi Select mode");
        ExtentReportManager.getTest().log(Status.PASS, "0 selected + Share file = no-op.");
    }

    @Test(description = "TC_TRK_071: 0 item selected + Delete file -> khong mo confirm xoa")
    public void TC_TRK_071_zero_delete_noop() {
        HomePage home = new HomePage();
        TracksPage tracks = enterSelectZero(home);

        tracks.tapSelDeleteFile(); home.sleep(1000);
        Assert.assertFalse(tracks.isDeleteConfirmOpen(), "0 selected + Delete file ma van mo confirm xoa");
        Assert.assertTrue(tracks.isSelectModeActive(), "0 selected + Delete file ma da roi Select mode");
        ExtentReportManager.getTest().log(Status.PASS, "0 selected + Delete file = no-op.");
    }
}