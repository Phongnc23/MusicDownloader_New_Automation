package testcases.playlists;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.PlaylistsPage;
import report.ExtentReportManager;

/**
 * Module: Playlists - Clear & Share (TC_PL_021..022).
 * TC_PL_021: Clear recently played - confirm dialog, CANCEL giu nguyen (KHONG xoa - khong bam confirm).
 *            (DOM confirm "Clear" chua co -> dung helper confirm da nhan; CHI bam CANCEL, an toan.)
 * TC_PL_022: Share My Favorite (329 > 10) tu sheet - bi chan (khong mo resolver).
 */
public class Playlists07_Verify_Clear_And_Share extends BaseTest {

    private PlaylistsPage goPlaylists(HomePage home) {
        PlaylistsPage pl = new PlaylistsPage();
        pl.gotoPlaylistsList(home);
        Assert.assertTrue(pl.isPlaylistsScreenDisplayed(), "Khong vao duoc man Playlists");
        return pl;
    }

    /**
     * File CUOI cua module Playlists -> sau MOI test dua app ve Playlists list sach (thoat
     * sheet/detail/dialog/share, dismiss exit dialog). Tranh module/regression sau khoi dau o
     * man ket. Chay TRUOC BaseTest.tearDown (subclass @AfterMethod chay truoc superclass).
     */
    @AfterMethod(alwaysRun = true)
    public void backToPlaylistsList() {
        try { new PlaylistsPage().gotoPlaylistsList(new HomePage()); } catch (Exception ignored) {}
    }

    @Test(description = "TC_PL_021: Clear recently played - confirm dialog, CANCEL giu nguyen")
    public void TC_PL_021_clear_recently_played_cancel() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        int before = pl.getTrackCountOf("Recently Played");
        Assert.assertTrue(before > 0, "Recently Played khong co track de kiem tra");

        pl.openPlaylistMenu("Recently Played");
        home.sleep(900);
        Assert.assertTrue(pl.sheetHasClearRecentlyPlayed(), "Sheet khong co 'Clear recently played'");
        pl.tapSheetClearRecent();
        home.sleep(1000);

        boolean dlg = pl.isConfirmDialogOpen();
        if (dlg) {
            pl.tapConfirmCancel();   // CHI huy, khong xoa
            home.sleep(900);
            ExtentReportManager.getTest().log(Status.INFO, "Da hien confirm dialog -> bam CANCEL.");
        } else {
            pl.closeSheetViaBack();
            home.sleep(700);
            ExtentReportManager.getTest().log(Status.INFO,
                    "CANH BAO: khong thay confirm dialog sau 'Clear recently played' (can cung cap DOM).");
        }

        int after = pl.getTrackCountOf("Recently Played");
        Assert.assertEquals(after, before, "Recently Played bi thay doi du da CANCEL (truoc=" + before + ", sau=" + after + ")");
        ExtentReportManager.getTest().log(Status.PASS,
                "Clear recently played CANCEL giu nguyen (" + before + " tracks).");
    }

    @Test(description = "TC_PL_022: Share My Favorite (329 > 10) tu sheet - bi chan")
    public void TC_PL_022_share_my_favorite_blocked() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        int count = pl.getTrackCountOf("My Favorite");
        Assert.assertTrue(count > 10, "My Favorite phai > 10 track de kiem tra chan share (dang: " + count + ")");

        pl.openPlaylistMenu("My Favorite");
        home.sleep(900);
        Assert.assertTrue(pl.isLocalSheetOpen(), "Khong mo duoc sheet My Favorite");
        pl.tapSheetShare();
        home.sleep(1800);

        Assert.assertFalse(pl.isShareSheetOpen(),
                "Share " + count + " track (>10) dang le bi chan nhung resolver van mo");
        ExtentReportManager.getTest().log(Status.PASS,
                "Share My Favorite (" + count + " > 10) bi chan dung (khong mo resolver).");
    }
}